package com.crowdin.platform

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.hardware.Sensor
import android.hardware.SensorManager
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import com.crowdin.platform.data.StringDataManager
import com.crowdin.platform.data.TextMetaDataProvider
import com.crowdin.platform.data.local.LocalStringRepositoryFactory
import com.crowdin.platform.data.model.AuthInfo
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.parser.StringResourceParser
import com.crowdin.platform.data.parser.XmlReader
import com.crowdin.platform.data.remote.CrowdinRetrofitService
import com.crowdin.platform.data.remote.MappingCallback
import com.crowdin.platform.data.remote.MappingRepository
import com.crowdin.platform.data.remote.StringDataRemoteRepository
import com.crowdin.platform.realtimeupdate.RealTimeUpdateManager
import com.crowdin.platform.recurringwork.RecurringManager
import com.crowdin.platform.screenshot.ScreenshotCallback
import com.crowdin.platform.screenshot.ScreenshotManager
import com.crowdin.platform.transformer.*
import com.crowdin.platform.util.FeatureFlags
import com.crowdin.platform.util.ScreenshotUtils
import com.crowdin.platform.util.TextUtils

/**
 * Entry point for Crowdin. it will be used for setting new strings, wrapping activity context.
 */
object Crowdin {

    private lateinit var viewTransformerManager: ViewTransformerManager
    private lateinit var config: CrowdinConfig
    private var stringDataManager: StringDataManager? = null
    private var realTimeUpdateManager: RealTimeUpdateManager? = null

    /**
     * Initialize Crowdin with the specified configuration.
     *
     * @param context of the application.
     * @param config  of the Crowdin.
     */
    @JvmStatic
    fun init(context: Context, config: CrowdinConfig) {
        this.config = config
        initCrowdinApi()
        initStringDataManager(context, config)
        initViewTransformer()
        FeatureFlags.registerConfig(config)

        when {
            config.updateInterval >= RecurringManager.MIN_PERIODIC_INTERVAL_MILLIS ->
                RecurringManager.setPeriodicUpdates(context, config)
            else -> {
                RecurringManager.cancel(context)
                forceUpdate(context)
                loadMapping()
            }
        }
//        TODO: remove
        initShake(context)
    }

    internal fun initForUpdate(context: Context) {
        this.config = RecurringManager.getConfig(context)
        initCrowdinApi()
        initStringDataManager(context, config)
        forceUpdate(context)
    }

    /**
     * Wraps context of an activity to provide Crowdin features.
     *
     * @param base context of an activity.
     * @return the Crowdin wrapped context.
     */
    @JvmStatic
    fun wrapContext(base: Context): Context =
            if (stringDataManager == null) base
            else CrowdinContextWrapper.wrap(base, stringDataManager, viewTransformerManager)

    /**
     * Set a single string for a language.
     *
     * @param language the string is for.
     * @param key      the string key.
     * @param value    the string value.
     */
    @JvmStatic
    fun setString(language: String, key: String, value: String) {
        stringDataManager?.setString(language, key, value)
    }

    /**
     * Tries to update title for all items defined in menu xml file.
     *
     * @param menu      the options menu in which you place your items.
     * @param resources class for accessing an application's resources..
     * @param menuId    the id of menu file.
     */
    @JvmStatic
    fun updateMenuItemsText(menu: Menu, resources: Resources, menuId: Int) {
        TextUtils.updateMenuItemsText(menu, resources, menuId)
    }

    /**
     * Initialize force update from the network.
     */
    @JvmStatic
    fun forceUpdate(context: Context) {
        stringDataManager?.updateData(context, config.networkType)
    }

    //    TODO: consider to be local private in real-time update
    @JvmStatic
    fun invalidate() {
        if (FeatureFlags.isRealTimeUpdateEnabled) {
            viewTransformerManager.invalidate()
        }
    }

    /**
     * Send screenshot of current screen to the crowdin platform.
     * Will attach tags (keys and position) to UI components on the screen.
     *
     * @param view                  required for getting bitmap
     * @param activity              required for accessing current window
     * @param screenshotCallback    optional, will provide status of screenshot creating process
     */
    @JvmStatic
    fun sendScreenshot(view: View, activity: Activity, screenshotCallback: ScreenshotCallback? = null) {
        if (!FeatureFlags.isRealTimeUpdateEnabled) return
        if (stringDataManager == null) return

        ScreenshotUtils.getBitmapFromView(view, activity) {
            ScreenshotManager(
                    CrowdinRetrofitService.instance.getTmpCrowdinApi(),
                    it,
                    stringDataManager!!,
                    viewTransformerManager.getViewData(),
                    config.sourceLanguage,
                    screenshotCallback)
            ScreenshotManager.sendScreenshot()
        }
    }

    /**
     * Register callback for tracking loading state
     *
     * @see LoadingStateListener
     */
    @JvmStatic
    fun registerDataLoadingObserver(listener: LoadingStateListener) {
        stringDataManager?.addLoadingStateListener(listener)
    }

    /**
     * Remove callback for tracking loading state
     *
     * @see LoadingStateListener
     */
    @JvmStatic
    fun unregisterDataLoadingObserver(listener: LoadingStateListener) {
        stringDataManager?.removeLoadingStateListener(listener)
    }

    /**
     * Cancel recurring job defined by interval during sdk init.
     *
     * @param context context of an activity.
     */
    @JvmStatic
    fun cancelRecurring(context: Context) {
        RecurringManager.cancel(context)
    }

    /**
     * Connect to Crowdin platform for receiving realtime updates.
     */
    @JvmStatic
    fun connectRealTimeUpdates() {
        if (!FeatureFlags.isRealTimeUpdateEnabled) return

        if (realTimeUpdateManager == null) {
            realTimeUpdateManager = RealTimeUpdateManager(
                    CrowdinRetrofitService.instance.getCrowdinApi(),
                    config.distributionKey,
                    config.sourceLanguage,
                    stringDataManager,
                    viewTransformerManager)
        }
        realTimeUpdateManager?.openConnection()
    }

    /**
     * Close realtime update connection.
     */
    @JvmStatic
    fun disconnectRealTimeUpdates() {
        if (FeatureFlags.isRealTimeUpdateEnabled) {
            realTimeUpdateManager?.closeConnection()
            realTimeUpdateManager = null
        }
    }

    private fun initCrowdinApi() {
        CrowdinRetrofitService.instance.init()
    }

    private fun initStringDataManager(context: Context, config: CrowdinConfig) {
        val remoteRepository = StringDataRemoteRepository(
                CrowdinRetrofitService.instance.getCrowdinDistributionApi(),
                XmlReader(StringResourceParser()),
                config.distributionKey,
                config.filePaths)
        val localRepository = LocalStringRepositoryFactory.createLocalRepository(context, config)

        stringDataManager = StringDataManager(remoteRepository, localRepository, object : LocalDataChangeObserver {
            override fun onDataChanged() {
                viewTransformerManager.invalidate()
            }
        })
    }

    private fun initViewTransformer() {
        viewTransformerManager = ViewTransformerManager()
        viewTransformerManager.registerTransformer(TextViewTransformer(stringDataManager as TextMetaDataProvider))
        viewTransformerManager.registerTransformer(ToolbarTransformer(stringDataManager as TextMetaDataProvider))
        viewTransformerManager.registerTransformer(SupportToolbarTransformer(stringDataManager as TextMetaDataProvider))
        viewTransformerManager.registerTransformer(BottomNavigationViewTransformer())
        viewTransformerManager.registerTransformer(NavigationViewTransformer())
        viewTransformerManager.registerTransformer(SpinnerTransformer())
    }

    //    TODO: remove, app responsibility
    private fun initShake(context: Context) {
        // ShakeDetector initialization
        val mSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val shakeDetector = ShakeDetector()
        shakeDetector.setOnShakeListener(object : ShakeDetector.OnShakeListener {

            override fun onShake(count: Int) {
                forceUpdate(context)
                invalidate()
                Toast.makeText(context, "Shake: force update", Toast.LENGTH_SHORT).show()
            }
        })
        mSensorManager.registerListener(shakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    internal fun saveAuthInfo(authInfo: AuthInfo) {
        stringDataManager?.saveAuthInfo(authInfo)
    }

    private fun loadMapping() {
        if (config.isRealTimeUpdateEnabled) {
            val mappingRepository = MappingRepository(
                    CrowdinRetrofitService.instance.getCrowdinDistributionApi(),
                    XmlReader(StringResourceParser()),
                    config.distributionKey,
                    config.filePaths,
                    config.sourceLanguage)
            mappingRepository.getMapping(config.sourceLanguage, object : MappingCallback {
                override fun onSuccess(languageData: LanguageData) {
                    stringDataManager?.saveMapping(languageData)
                }

                override fun onFailure(throwable: Throwable) {
                    Log.d(Crowdin::class.java.simpleName, "Get mapping, onFailure:${throwable.localizedMessage}")
                }
            })
        }
    }
}