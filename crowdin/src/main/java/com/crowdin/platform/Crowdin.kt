package com.crowdin.platform

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Handler
import android.view.Menu
import android.view.WindowManager
import android.widget.Toast
import com.crowdin.platform.repository.StringDataManager
import com.crowdin.platform.repository.TextIdProvider
import com.crowdin.platform.repository.local.LocalStringRepositoryFactory
import com.crowdin.platform.repository.parser.StringResourceParser
import com.crowdin.platform.repository.parser.XmlReader
import com.crowdin.platform.repository.remote.CrowdinRetrofitService
import com.crowdin.platform.repository.remote.DefaultRemoteRepository
import com.crowdin.platform.transformers.*
import com.crowdin.platform.utils.FeatureFlags
import com.crowdin.platform.utils.TextUtils


/**
 * Entry point for Crowdin. it will be used for setting new strings, wrapping activity context.
 */
object Crowdin {

    private lateinit var viewTransformerManager: ViewTransformerManager
    private lateinit var config: CrowdinConfig
    private var stringDataManager: StringDataManager? = null

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
        initViewTransformer(context)
        FeatureFlags.registerConfig(config)
//        stringDataManager?.updateData(context, config.networkType)

        // ShakeDetector initialization
        val mSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val shakeDetector = ShakeDetector()
        shakeDetector.setOnShakeListener(object : ShakeDetector.OnShakeListener {

            override fun onShake(count: Int) {
                forceUpdate(context)
                Toast.makeText(context, "Shake: force update", Toast.LENGTH_SHORT).show()
            }
        })
        mSensorManager.registerListener(shakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
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

    @JvmStatic
    fun invalidate() {
        if (FeatureFlags.isRealTimeUpdateEnabled) {
            viewTransformerManager.invalidate()
        }
    }

    @JvmStatic
    fun drawOnUi() {
        if (FeatureFlags.isRealTimeUpdateEnabled) {
            viewTransformerManager.drawOnLocalizedUI()
        }
    }

    @JvmStatic
    fun setRealTimeUpdates(value: Boolean) {
        FeatureFlags.isRealTimeUpdateEnabled = value
    }

    private fun initCrowdinApi() {
        CrowdinRetrofitService.instance.init()
    }

    private fun initStringDataManager(context: Context, config: CrowdinConfig) {
        val remoteRepository = DefaultRemoteRepository(
                CrowdinRetrofitService.instance.getCrowdinApi(),
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

    // TODO: remove context if not needed
    private fun initViewTransformer(context: Context) {
        viewTransformerManager = ViewTransformerManager()
        viewTransformerManager.registerTransformer(TextViewTransformer(stringDataManager as TextIdProvider))
        viewTransformerManager.registerTransformer(ToolbarTransformer(stringDataManager as TextIdProvider))
        viewTransformerManager.registerTransformer(SupportToolbarTransformer(stringDataManager as TextIdProvider))
        viewTransformerManager.registerTransformer(BottomNavigationViewTransformer())
        viewTransformerManager.registerTransformer(NavigationViewTransformer())
        viewTransformerManager.registerTransformer(SpinnerTransformer())
    }
}

interface LocalDataChangeObserver {

    fun onDataChanged()
}