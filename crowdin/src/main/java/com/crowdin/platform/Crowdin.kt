package com.crowdin.platform

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.view.Menu
import com.crowdin.platform.data.DistributionInfoCallback
import com.crowdin.platform.data.StringDataManager
import com.crowdin.platform.data.TextMetaDataProvider
import com.crowdin.platform.data.local.LocalStringRepositoryFactory
import com.crowdin.platform.data.model.AuthInfo
import com.crowdin.platform.data.parser.StringResourceParser
import com.crowdin.platform.data.parser.XmlReader
import com.crowdin.platform.data.remote.CrowdinRetrofitService
import com.crowdin.platform.data.remote.DistributionInfoManager
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
    private var distributionInfoManager: DistributionInfoManager? = null
    private var screenshotManager: ScreenshotManager? = null
    private var shakeDetectorManager: ShakeDetectorManager? = null

    /**
     * Initialize Crowdin with the specified configuration.
     *
     * @param context of the application.
     * @param config  of the Crowdin.
     */
    @JvmStatic
    fun init(context: Context, config: CrowdinConfig) {
        this.config = config
        FeatureFlags.registerConfig(config)
        initCrowdinApi()
        initStringDataManager(context, config)
        initViewTransformer()
        initFeatureManagers()

        when {
            config.updateInterval >= RecurringManager.MIN_PERIODIC_INTERVAL_MILLIS ->
                RecurringManager.setPeriodicUpdates(context, config)
            else -> {
                RecurringManager.cancel(context)
                forceUpdate(context)
                loadMapping()
            }
        }
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

    /**
     * Update text for all text views on current screen.
     */
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
     * @param activity              required for accessing current window
     * @param screenshotCallback    optional, will provide status of screenshot creating process
     */
    @JvmStatic
    @JvmOverloads
    fun sendScreenshot(activity: Activity, screenshotCallback: ScreenshotCallback? = null) {
        if (!FeatureFlags.isRealTimeUpdateEnabled) return
        if (stringDataManager == null) return

        val view = activity.window.decorView.rootView
        ScreenshotUtils.getBitmapFromView(view, activity) {
            screenshotManager?.setScreenshotCallback(screenshotCallback)
            screenshotManager?.sendScreenshot(it, viewTransformerManager.getViewData())
        }
    }

    /**
     * Send screenshot of current screen to the crowdin platform.
     * Will attach tags (keys and position) to UI components on the screen.
     *
     * @param filePath              path to created screenshot file
     * @param screenshotCallback    optional, will provide status of screenshot creating process
     */
    @JvmStatic
    @JvmOverloads
    fun sendScreenshot(filePath: String, screenshotCallback: ScreenshotCallback? = null) {
        if (!FeatureFlags.isRealTimeUpdateEnabled) return
        if (stringDataManager == null) return

        val bitmap = BitmapFactory.decodeFile(filePath)
        screenshotManager?.setScreenshotCallback(screenshotCallback)
        screenshotManager?.sendScreenshot(bitmap, viewTransformerManager.getViewData())
    }

    /**
     * Register screenshot observer.
     *
     * @param context of the application.
     */
    @JvmStatic
    fun registerScreenShotContentObserver(context: Context) {
        screenshotManager?.registerScreenShotContentObserver(context)
    }

    /**
     * Unregister screenshot observer.
     *
     * @param context of the application.
     */
    @JvmStatic
    fun unregisterScreenShotContentObserver(context: Context) {
        screenshotManager?.unregisterScreenShotContentObserver(context)
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

    /**
     * Register shake detector. Will trigger force update on shake event.
     */
    @JvmStatic
    fun registerShakeDetector(context: Context) {
        if (shakeDetectorManager == null) {
            shakeDetectorManager = ShakeDetectorManager()
        }
        shakeDetectorManager?.registerShakeDetector(context)
    }

    /**
     * Unregister shake detector.
     */
    @JvmStatic
    fun unregisterShakeDetector() {
        shakeDetectorManager?.unregisterShakeDetector()
    }

    internal fun saveAuthInfo(authInfo: AuthInfo) {
        stringDataManager?.saveData(StringDataManager.AUTH_INFO, authInfo)
    }

    internal fun getDistributionInfo(userAgent: String,
                                     cookies: String,
                                     xCsrfToken: String,
                                     callback: DistributionInfoCallback) {
        if (stringDataManager == null) {
            callback.onError(Throwable("Local repository could not be null"))
            return
        }
        distributionInfoManager?.getDistributionInfo(userAgent, cookies, xCsrfToken, callback)
    }

    private fun initCrowdinApi() {
        CrowdinRetrofitService.instance.init()
    }

    private fun initFeatureManagers() {
        if (FeatureFlags.isRealTimeUpdateEnabled) {
            distributionInfoManager = DistributionInfoManager(
                    CrowdinRetrofitService.instance.getCrowdinApi(),
                    stringDataManager!!,
                    Crowdin.config.distributionKey)

            realTimeUpdateManager = RealTimeUpdateManager(
                    Crowdin.config.distributionKey,
                    Crowdin.config.sourceLanguage,
                    stringDataManager,
                    viewTransformerManager)

            screenshotManager = ScreenshotManager(
                    CrowdinRetrofitService.instance.getTmpCrowdinApi(),
                    stringDataManager!!,
                    Crowdin.config.sourceLanguage)
        }
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

    private fun loadMapping() {
        if (config.isRealTimeUpdateEnabled) {
            stringDataManager ?: return

            val mappingRepository = MappingRepository(
                    CrowdinRetrofitService.instance.getCrowdinDistributionApi(),
                    XmlReader(StringResourceParser()),
                    stringDataManager!!,
                    config.distributionKey,
                    config.filePaths,
                    config.sourceLanguage)
            mappingRepository.getMapping()
        }
    }
}