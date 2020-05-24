package com.crowdin.platform

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.view.Menu
import androidx.annotation.MenuRes
import com.crowdin.platform.auth.AuthActivity
import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.DistributionInfoCallback
import com.crowdin.platform.data.TextMetaDataProvider
import com.crowdin.platform.data.local.LocalStringRepositoryFactory
import com.crowdin.platform.data.model.AuthConfig
import com.crowdin.platform.data.model.AuthInfo
import com.crowdin.platform.data.parser.StringResourceParser
import com.crowdin.platform.data.parser.XmlReader
import com.crowdin.platform.data.remote.CrowdinRetrofitService
import com.crowdin.platform.data.remote.DistributionInfoManager
import com.crowdin.platform.data.remote.MappingRepository
import com.crowdin.platform.data.remote.StringDataRemoteRepository
import com.crowdin.platform.data.remote.TranslationDataRepository
import com.crowdin.platform.realtimeupdate.RealTimeUpdateManager
import com.crowdin.platform.recurringwork.RecurringManager
import com.crowdin.platform.screenshot.ScreenshotCallback
import com.crowdin.platform.screenshot.ScreenshotManager
import com.crowdin.platform.screenshot.ScreenshotUtils
import com.crowdin.platform.transformer.BottomNavigationViewTransformer
import com.crowdin.platform.transformer.NavigationViewTransformer
import com.crowdin.platform.transformer.SpinnerTransformer
import com.crowdin.platform.transformer.SupportToolbarTransformer
import com.crowdin.platform.transformer.TextViewTransformer
import com.crowdin.platform.transformer.ToolbarTransformer
import com.crowdin.platform.transformer.ViewTransformerManager
import com.crowdin.platform.util.FeatureFlags
import com.crowdin.platform.util.TextUtils
import com.crowdin.platform.util.createAuthDialog

/**
 * Entry point for Crowdin. it will be used for setting new strings, wrapping activity context.
 */
object Crowdin {

    const val CROWDIN_TAG = "CrowdingSDK:"
    private lateinit var viewTransformerManager: ViewTransformerManager
    private lateinit var config: CrowdinConfig
    private lateinit var crowdinPreferences: Preferences
    private var dataManager: DataManager? = null
    private var realTimeUpdateManager: RealTimeUpdateManager? = null
    private var distributionInfoManager: DistributionInfoManager? = null
    private var screenshotManager: ScreenshotManager? = null
    private var shakeDetectorManager: ShakeDetectorManager? = null
    private var translationDataRepository: TranslationDataRepository? = null

    /**
     * Initialize Crowdin with the specified configuration.
     *
     * @param context of the application.
     * @param config of the Crowdin.
     */
    @JvmStatic
    fun init(context: Context, config: CrowdinConfig) {
        this.config = config
        FeatureFlags.registerConfig(config)
        initPreferences(context)
        initStringDataManager(context, config)
        initViewTransformer()
        initFeatureManagers()
        initTranslationDataManager()

        when {
            config.updateInterval >= RecurringManager.MIN_PERIODIC_INTERVAL_MILLIS ->
                RecurringManager.setPeriodicUpdates(context, config)
            else -> {
                RecurringManager.cancel(context)
                if (!FeatureFlags.isRealTimeUpdateEnabled) {
                    forceUpdate(context)
                }
                loadMapping()
            }
        }
    }

    internal fun initForUpdate(context: Context) {
        this.config = RecurringManager.getConfig(context)
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
        if (dataManager == null) {
            base
        } else {
            CrowdinContextWrapper.wrap(base, dataManager, viewTransformerManager)
        }

    /**
     * Set a single string for a language.
     *
     * @param language language code. For example en, en-GB, en-US etc.
     *                  https://support.crowdin.com/api/language-codes/
     * @param key the string key.
     * @param value the string value.
     */
    @JvmStatic
    fun setString(language: String, key: String, value: String) {
        dataManager?.setString(language, key, value)
    }

    /**
     * Get a single string for a language.
     *
     * @param language language code. For example en, en-GB, en-US etc.
     *                  https://support.crowdin.com/api/language-codes/
     * @param key the string key.
     * @return resource or empty string.
     */
    @JvmStatic
    fun getString(language: String, key: String): String {
        return dataManager?.getString(language, key) ?: ""
    }

    /**
     * Get a json string for a language data. Including strings/arrays/plurals.
     *
     * @param language language code. For example en, en-GB, en-US etc.
     *                  https://support.crowdin.com/api/language-codes/
     * @return json or empty string. Example:
     *
     *  {
     *      "language":"de",
     *      "strings":{
     *          "stringKey0":"Text0",
     *          "stringKey1":"Text1"
     *      },
     *      "arrays":{
     *          "arrayKey0":[
     *              "Monday",
     *              "Wednesday"
     *          ]
     *      },
     *      "plurals":{
     *          "pluralsKey":{
     *              "one":"Text0",
     *              "other":"Text1"
     *          }
     *      }
     *  }
     */
    @JvmStatic
    fun getResources(language: String): String {
        return dataManager?.getLanguageData(language).toString()
    }

    /**
     * Tries to update title for all items defined in menu xml file.
     *
     * @param menuRes the id of menu file.
     * @param menu the options menu in which you place your items.
     * @param resources class for accessing an application's resources.
     */
    @JvmStatic
    fun updateMenuItemsText(@MenuRes menuRes: Int, menu: Menu, resources: Resources) {
        TextUtils.updateMenuItemsText(menuRes, menu, resources)
    }

    /**
     * Initialize force update from the network.
     */
    @JvmStatic
    fun forceUpdate(context: Context) {
        dataManager?.updateData(context, config.networkType)
    }

    /**
     * Update text for all text views on current screen.
     */
    @JvmStatic
    fun invalidate() {
        viewTransformerManager.invalidate()
    }

    /**
     * Send screenshot of current screen to the crowdin platform.
     * Will attach tags (keys and position) related to UI components from the screen.
     *
     * @param activity required for accessing current window.
     * @param screenshotCallback optional, will provide status of screenshot creating process.
     */
    @JvmStatic
    @JvmOverloads
    fun sendScreenshot(activity: Activity, screenshotCallback: ScreenshotCallback? = null) {
        screenshotManager?.let {
            val view = activity.window.decorView.rootView
            ScreenshotUtils.getBitmapFromView(view, activity) { bitmap ->
                it.setScreenshotCallback(screenshotCallback)
                it.sendScreenshot(
                    bitmap,
                    viewTransformerManager.getViewData(),
                    activity.localClassName
                )
            }
        }
    }

    /**
     * Send screenshot of current screen to the crowdin platform.
     * Will attach tags (keys and position) related to UI components from the screen.
     *
     * @param bitmap screenshot.
     * @param screenshotCallback optional, will provide status of screenshot creating process.
     */
    @JvmStatic
    @JvmOverloads
    fun sendScreenshot(bitmap: Bitmap, screenshotCallback: ScreenshotCallback? = null) {
        screenshotManager?.let {
            screenshotManager?.setScreenshotCallback(screenshotCallback)
            screenshotManager?.sendScreenshot(
                bitmap,
                viewTransformerManager.getViewData()
            )
        }
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
     * Register callback for tracking loading state.
     *
     * @see LoadingStateListener
     */
    @JvmStatic
    fun registerDataLoadingObserver(listener: LoadingStateListener) {
        dataManager?.addLoadingStateListener(listener)
    }

    /**
     * Remove callback for tracking loading state.
     *
     * @see LoadingStateListener
     */
    @JvmStatic
    fun unregisterDataLoadingObserver(listener: LoadingStateListener) {
        dataManager?.removeLoadingStateListener(listener)
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
     * Auth to Crowdin platform. Create connection for realtime updates if feature turned on.
     */
    @JvmStatic
    fun authorize(activity: Activity) {
        if (!FeatureFlags.isRealTimeUpdateEnabled || !FeatureFlags.isScreenshotEnabled) {
            return
        }

        if (isAuthorized()) {
            tryCreateRealTimeConnection()
        } else {
            createAuthDialog(activity) { AuthActivity.launchActivity(activity) }
        }
    }

    /**
     * Open realtime update connection.
     */
    @JvmStatic
    fun connectRealTimeUpdates() {
        tryCreateRealTimeConnection()
    }

    /**
     * Close realtime update connection.
     */
    @JvmStatic
    fun disconnectRealTimeUpdates() {
        realTimeUpdateManager?.closeConnection()
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

    internal fun isAuthorized(): Boolean {
        dataManager?.let {
            val oldHash = it.getDistributionHash()
            val newHash = config.distributionHash
            it.saveDistributionHash(newHash)

            return it.isAuthorized() && (oldHash == null || oldHash == newHash)
        }

        return false
    }

    internal fun tryCreateRealTimeConnection() {
        if (FeatureFlags.isRealTimeUpdateEnabled) {
            realTimeUpdateManager?.openConnection()
        }
    }

    internal fun saveAuthInfo(authInfo: AuthInfo?) {
        dataManager?.saveData(DataManager.AUTH_INFO, authInfo)
    }

    internal fun getDistributionInfo(callback: DistributionInfoCallback) {
        distributionInfoManager?.getDistributionInfo(callback)
    }

    private fun initFeatureManagers() {
        if (FeatureFlags.isRealTimeUpdateEnabled || FeatureFlags.isScreenshotEnabled) {
            distributionInfoManager = DistributionInfoManager(
                CrowdinRetrofitService.getCrowdinApi(
                    dataManager!!,
                    config.authConfig?.organizationName
                ),
                dataManager!!,
                config.distributionHash
            )
        }

        if (FeatureFlags.isRealTimeUpdateEnabled) {
            realTimeUpdateManager = RealTimeUpdateManager(
                config.sourceLanguage,
                dataManager,
                viewTransformerManager
            )
        }

        if (FeatureFlags.isScreenshotEnabled) {
            screenshotManager = ScreenshotManager(
                CrowdinRetrofitService.getCrowdinApi(
                    dataManager!!,
                    config.authConfig?.organizationName
                ),
                dataManager!!,
                config.sourceLanguage
            )
        }
    }

    private fun initPreferences(context: Context) {
        crowdinPreferences = CrowdinPreferences(context)
    }

    private fun initStringDataManager(context: Context, config: CrowdinConfig) {
        val remoteRepository = StringDataRemoteRepository(
            CrowdinRetrofitService.getCrowdinDistributionApi(),
            XmlReader(StringResourceParser()),
            config.distributionHash
        )
        val localRepository = LocalStringRepositoryFactory.createLocalRepository(context, config)

        dataManager =
            DataManager(
                remoteRepository,
                localRepository,
                crowdinPreferences,
                object : LocalDataChangeObserver {
                    override fun onDataChanged() {
                        viewTransformerManager.invalidate()
                    }
                })
    }

    private fun initViewTransformer() {
        viewTransformerManager = ViewTransformerManager()
        viewTransformerManager.registerTransformer(TextViewTransformer(dataManager as TextMetaDataProvider))
        viewTransformerManager.registerTransformer(ToolbarTransformer(dataManager as TextMetaDataProvider))
        viewTransformerManager.registerTransformer(SupportToolbarTransformer(dataManager as TextMetaDataProvider))
        viewTransformerManager.registerTransformer(BottomNavigationViewTransformer())
        viewTransformerManager.registerTransformer(NavigationViewTransformer())
        viewTransformerManager.registerTransformer(SpinnerTransformer())
    }

    private fun loadMapping() {
        if (FeatureFlags.isRealTimeUpdateEnabled || FeatureFlags.isScreenshotEnabled) {
            val mappingRepository = MappingRepository(
                CrowdinRetrofitService.getCrowdinDistributionApi(),
                XmlReader(StringResourceParser()),
                dataManager!!,
                config.distributionHash,
                config.sourceLanguage
            )
            mappingRepository.fetchData()
        }
    }

    internal fun getAuthConfig(): AuthConfig? = config.authConfig

    private fun initTranslationDataManager() {
        if (FeatureFlags.isRealTimeUpdateEnabled) {
            translationDataRepository = TranslationDataRepository(
                CrowdinRetrofitService.getCrowdinDistributionApi(),
                CrowdinRetrofitService.getCrowdinApi(
                    dataManager!!,
                    config.authConfig?.organizationName
                ),
                CrowdinRetrofitService.getCrowdinTranslationApi(),
                XmlReader(StringResourceParser()),
                dataManager!!,
                config.distributionHash
            )
            loadTranslation()
        }
    }

    internal fun loadTranslation() {
        if (dataManager?.isAuthorized() == true) {
            translationDataRepository?.fetchData()
        }
    }
}
