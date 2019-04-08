package com.crowdin.platform

import android.content.Context
import android.content.res.Resources
import android.view.Menu
import com.crowdin.platform.repository.StringDataManager
import com.crowdin.platform.repository.local.LocalStringRepositoryFactory
import com.crowdin.platform.repository.parser.StringResourceParser
import com.crowdin.platform.repository.parser.XmlReader
import com.crowdin.platform.repository.remote.CrowdinRetrofitService
import com.crowdin.platform.repository.remote.DefaultRemoteRepository
import com.crowdin.platform.transformers.*
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
        initCrowdinApi(context)
        initStringDataManager(context, config)
        initViewTransformer()
        stringDataManager?.updateData(context, config.networkType)
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

    private fun initCrowdinApi(context: Context) {
        CrowdinRetrofitService.instance.init(context)
    }

    private fun initStringDataManager(context: Context, config: CrowdinConfig) {
        val remoteRepository = DefaultRemoteRepository(
                CrowdinRetrofitService.instance.getCrowdinApi(),
                XmlReader(StringResourceParser()),
                config.distributionKey,
                config.filePaths)
        val localRepository = LocalStringRepositoryFactory.createLocalRepository(context, config)

        stringDataManager = StringDataManager(remoteRepository, localRepository)
    }

    private fun initViewTransformer() {
        viewTransformerManager = ViewTransformerManager()
        viewTransformerManager.registerTransformer(TextViewTransformer())
        viewTransformerManager.registerTransformer(ToolbarTransformer())
        viewTransformerManager.registerTransformer(SupportToolbarTransformer())
        viewTransformerManager.registerTransformer(BottomNavigationViewTransformer())
        viewTransformerManager.registerTransformer(NavigationViewTransformer())
        viewTransformerManager.registerTransformer(SpinnerTransformer())
    }
}