package com.crowdin.platform

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.util.Log
import android.view.Menu
import androidx.annotation.MenuRes
import com.crowdin.platform.auth.AuthActivity
import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.DistributionInfoCallback
import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.TextMetaDataProvider
import com.crowdin.platform.data.local.LocalStringRepositoryFactory
import com.crowdin.platform.data.model.ApiAuthConfig
import com.crowdin.platform.data.model.AuthConfig
import com.crowdin.platform.data.model.AuthInfo
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.LanguagesInfo
import com.crowdin.platform.data.model.ManifestData
import com.crowdin.platform.data.parser.StringResourceParser
import com.crowdin.platform.data.parser.XmlReader
import com.crowdin.platform.data.remote.Connectivity
import com.crowdin.platform.data.remote.CrowdinRetrofitService
import com.crowdin.platform.data.remote.DistributionInfoManager
import com.crowdin.platform.data.remote.MappingRepository
import com.crowdin.platform.data.remote.StringDataRemoteRepository
import com.crowdin.platform.data.remote.TranslationDataRepository
import com.crowdin.platform.data.remote.TranslationDownloadCallback
import com.crowdin.platform.data.remote.api.CrowdinApi
import com.crowdin.platform.realtimeupdate.RealTimeUpdateManager
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
import com.crowdin.platform.util.ThreadUtils
import com.crowdin.platform.util.UiUtil
import com.crowdin.platform.util.parseToDateTimeFormat

/**
 * Entry point for Crowdin. it will be used for setting new strings, wrapping activity context.
 */
object Crowdin {
    const val CROWDIN_TAG = "CrowdinSDK"

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
    fun init(
        context: Context,
        config: CrowdinConfig,
    ) {
        this.config = config
        FeatureFlags.registerConfig(config)
        initPreferences(context)
        initStringDataManager(context, config)
        initViewTransformer()
        initFeatureManagers()
        initTranslationDataManager()
        initRealTimeUpdates()
        initLoading(context)
        loadMapping()

        initDistributionInfo()
    }

    private fun initLoading(context: Context) {
        val lastUpdate = crowdinPreferences.getLastUpdate()
        val timeDiff = System.currentTimeMillis() - lastUpdate
        if (lastUpdate != 0L && timeDiff < config.updateInterval) {
            return
        }

        if (config.isInitSyncEnabled && !FeatureFlags.isRealTimeUpdateEnabled) {
            forceUpdate(context)
        }
    }

    private fun initRealTimeUpdates() {
        if (config.isRealTimeUpdateEnabled && isAuthorized()) {
            createRealTimeConnection()
        }
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
            try {
                CrowdinContextWrapper.wrap(base, dataManager, viewTransformerManager)
            } catch (ex: Exception) {
                Log.d(CROWDIN_TAG, "Couldn't wrap context without first calling `Crowdin.init`")
                base
            }
        }

    /**
     * Reload translations after configurations has changed.
     */
    fun onConfigurationChanged() {
        if (FeatureFlags.isRealTimeUpdateEnabled) {
            downloadTranslation()
        }
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
    fun setString(
        language: String,
        key: String,
        value: String,
    ) {
        dataManager?.setString(language, key, value)
    }

    /**
     * Tries to update title for all items defined in menu xml file.
     *
     * @param menuRes the id of menu file.
     * @param menu the options menu in which you place your items.
     * @param resources class for accessing an application's resources.
     */
    @JvmStatic
    fun updateMenuItemsText(
        @MenuRes menuRes: Int,
        menu: Menu,
        resources: Resources,
    ) {
        TextUtils.updateMenuItemsText(menuRes, menu, resources)
    }

    /**
     * Initialize force update from the network.
     */
    @JvmStatic
    fun forceUpdate(
        context: Context,
        onFinished: (() -> Unit)? = null,
    ) {
        Log.v(CROWDIN_TAG, "Force update started")
        dataManager?.updateData(context, config.networkType, onFinished)
    }

    /**
     * Update text for all text views on current screen.
     */
    @JvmStatic
    fun invalidate() {
        viewTransformerManager.invalidate()
    }

    /**
     * Sends a screenshot of the current screen to the Crowdin platform.
     * The screenshot will include tags with keys and positions related to UI components on the screen.
     *
     * If a screenshot with the same name already exists, it will be updated. Otherwise, a new screenshot
     * entry will be created on the platform.
     *
     * @param activity required for accessing current window.
     * @param screenshotName Optional name to identify the screenshot on the platform. If a screenshot with
     *                       this name already exists, it will be updated. This name should not include any file extensions.
     * @param screenshotCallback Optional callback that provides the status of the screenshot upload process,
     *                           including success or failure details.
     */
    @JvmStatic
    @JvmOverloads
    fun sendScreenshot(
        activity: Activity,
        screenshotName: String? = null,
        screenshotCallback: ScreenshotCallback? = null,
    ) {
        screenshotManager?.let {
            val view = activity.window.decorView.rootView
            val name = screenshotName ?: (activity.localClassName + "-" + System.currentTimeMillis().parseToDateTimeFormat())
            ScreenshotUtils.getBitmapFromView(view, activity) { bitmap ->
                it.setScreenshotCallback(screenshotCallback)
                it.sendScreenshot(
                    bitmap,
                    viewTransformerManager.getViewData(),
                    name,
                )
            }
        }
    }

    /**
     * Sends a screenshot of the current screen to the Crowdin platform.
     * The screenshot will include tags with keys and positions related to UI components on the screen.
     *
     * If a screenshot with the same name already exists, it will be updated. Otherwise, a new screenshot
     * entry will be created on the platform.
     *
     * @param bitmap The screenshot image as a `Bitmap`.
     * @param screenshotName Optional name to identify the screenshot on the platform. If a screenshot with
     *                       this name already exists, it will be updated. This name should not include any file extensions.
     * @param screenshotCallback Optional callback that provides the status of the screenshot upload process,
     *                           including success or failure details.
     */
    @JvmStatic
    @JvmOverloads
    fun sendScreenshot(
        bitmap: Bitmap,
        screenshotName: String? = null,
        screenshotCallback: ScreenshotCallback? = null,
    ) {
        screenshotManager?.let {
            it.setScreenshotCallback(screenshotCallback)
            it.sendScreenshot(
                bitmap = bitmap,
                viewDataList = viewTransformerManager.getViewData(),
                name = screenshotName,
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
     * Auth to Crowdin platform. Create connection for realtime updates if feature turned on.
     */
    @JvmStatic
    fun authorize(
        context: Context,
        onErrorAction: ((str: String) -> Unit)? = null,
    ) {
        Log.d(CROWDIN_TAG, "Authorize started")

        if (isAuthorized()) {
            createRealTimeConnection()
        } else if ((FeatureFlags.isRealTimeUpdateEnabled || FeatureFlags.isScreenshotEnabled) &&
            !isAuthorized()
        ) {
            if (!Connectivity.isOnline(context)) {
                onErrorAction?.invoke("No internet connection")
                return
            }

            if (config.authConfig?.requestAuthDialog == false) {
                AuthActivity.launchActivity(context)
            } else {
                UiUtil.createAuthDialog(context) { AuthActivity.launchActivity(context) }
            }
        }
    }

    /**
     * Logs out from Crowdin platform.
     */
    fun logOut() {
        dataManager?.invalidateAuthData()
        disconnectRealTimeUpdates()
    }

    fun isAuthorized(): Boolean {
        dataManager?.let {
            val oldHash = it.getDistributionHash()
            val newHash = config.distributionHash
            it.saveDistributionHash(newHash)

            return it.isAuthorized() && (oldHash == null || oldHash == newHash)
        }

        return false
    }

    /**
     * Create realtime update connection.
     */
    @JvmStatic
    fun createRealTimeConnection() {
        if (FeatureFlags.isRealTimeUpdateEnabled) {
            Log.v(CROWDIN_TAG, "Creating realtime connection")
            realTimeUpdateManager?.openConnection()
        } else {
            Log.v(CROWDIN_TAG, "Creating realtime connection skipped. Flag doesn't used")
        }
    }

    /**
     * Close realtime update connection.
     */
    @JvmStatic
    fun disconnectRealTimeUpdates() {
        realTimeUpdateManager?.closeConnection()
    }

    /**
     * Return `real-time` updates connected state. true - connected, false - disconnected.
     */
    fun isRealTimeUpdatesConnected(): Boolean = realTimeUpdateManager?.isConnectionCreated ?: false

    /**
     * Return 'capture-screenshot' feature enable state. true - enabled, false - disabled.
     */
    fun isCaptureScreenshotEnabled(): Boolean = config.isScreenshotEnabled

    /**
     * Return 'real-time' feature enable state. true - enabled, false - disabled.
     */
    fun isRealTimeUpdatesEnabled(): Boolean = config.isRealTimeUpdateEnabled

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

    /**
     * Download latest translations from Crowdin.
     */
    fun downloadTranslation(callback: TranslationDownloadCallback? = null) {
        if (FeatureFlags.isRealTimeUpdateEnabled && dataManager?.isAuthorized() == true) {
            translationDataRepository?.fetchData(
                languageDataCallback =
                    object : LanguageDataCallback {
                        override fun onDataLoaded(languageData: LanguageData) {
                            callback?.onSuccess()
                        }

                        override fun onFailure(throwable: Throwable) {
                            callback?.onFailure(throwable)
                        }
                    },
            )
        } else {
            val error =
                "This action requires authorization due to the 'withRealTimeUpdates' option is being enabled in config. Press the 'Log In' button to authorize"
            callback?.onFailure(Throwable(error))
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
            distributionInfoManager =
                DistributionInfoManager(
                    getCrowdinApi(),
                    dataManager!!,
                    config.distributionHash,
                )
        }

        if (FeatureFlags.isRealTimeUpdateEnabled) {
            realTimeUpdateManager =
                RealTimeUpdateManager(
                    config.sourceLanguage,
                    dataManager,
                    viewTransformerManager,
                )
        }

        if (FeatureFlags.isScreenshotEnabled) {
            screenshotManager =
                ScreenshotManager(
                    getCrowdinApi(),
                    dataManager!!,
                    config.sourceLanguage,
                )
        }
    }

    private fun initPreferences(context: Context) {
        crowdinPreferences = CrowdinPreferences(context)
    }

    private fun initStringDataManager(
        context: Context,
        config: CrowdinConfig,
    ) {
        val remoteRepository =
            StringDataRemoteRepository(
                crowdinPreferences,
                CrowdinRetrofitService.getCrowdinDistributionApi(),
                config.distributionHash,
            )
        val localRepository = LocalStringRepositoryFactory.createLocalRepository(context, config)

        dataManager =
            DataManager(
                remoteRepository,
                localRepository,
                crowdinPreferences,
                object : LocalDataChangeObserver {
                    override fun onDataChanged() {
                        ThreadUtils.executeOnMain {
                            viewTransformerManager.invalidate()
                        }
                    }
                },
            )
        remoteRepository.crowdinApi = getCrowdinApi()
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
            val mappingRepository =
                MappingRepository(
                    CrowdinRetrofitService.getCrowdinDistributionApi(),
                    XmlReader(StringResourceParser()),
                    dataManager!!,
                    config.distributionHash,
                    config.sourceLanguage,
                )
            mappingRepository.crowdinApi = getCrowdinApi()
            mappingRepository.fetchData()
        }
    }

    internal fun getAuthConfig(): AuthConfig? = config.authConfig

    internal fun getApiAuthConfig(): ApiAuthConfig? = config.apiAuthConfig

    internal fun getOrganizationName(): String? = config.organizationName

    private fun initTranslationDataManager() {
        if (FeatureFlags.isRealTimeUpdateEnabled) {
            translationDataRepository =
                TranslationDataRepository(
                    CrowdinRetrofitService.getCrowdinDistributionApi(),
                    CrowdinRetrofitService.getCrowdinTranslationApi(),
                    XmlReader(StringResourceParser()),
                    dataManager!!,
                    config.distributionHash,
                )
            translationDataRepository?.crowdinApi = getCrowdinApi()
            downloadTranslation()
        }
    }

    private fun getCrowdinApi(): CrowdinApi =
        CrowdinRetrofitService.getCrowdinApi(
            dataManager!!,
            config.organizationName,
        )

    fun getManifest(): ManifestData? = dataManager?.getManifest()

    fun getSupportedLanguages(): LanguagesInfo? = dataManager?.getSupportedLanguages()

    private fun initDistributionInfo() {
        if (config.apiAuthConfig?.apiToken == null) {
            return
        }

        getDistributionInfo(
            object : DistributionInfoCallback {
                override fun onResponse() {
                    Log.d(CROWDIN_TAG, "Distribution info loaded")
                }

                override fun onError(throwable: Throwable) {
                    Log.e(CROWDIN_TAG, "Distribution info not loaded", throwable)
                }
            },
        )
    }
}
