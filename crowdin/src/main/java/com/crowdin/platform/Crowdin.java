package com.crowdin.platform;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.support.annotation.VisibleForTesting;
import android.view.Menu;

import com.crowdin.platform.repository.StringDataManager;
import com.crowdin.platform.repository.local.LocalStringRepository;
import com.crowdin.platform.repository.remote.RemoteStringRepository;
import com.crowdin.platform.transformers.BottomNavigationViewTransformer;
import com.crowdin.platform.transformers.NavigationViewTransformer;
import com.crowdin.platform.transformers.SpinnerTransformer;
import com.crowdin.platform.transformers.SupportToolbarTransformer;
import com.crowdin.platform.transformers.SwitchTransformer;
import com.crowdin.platform.transformers.TextViewTransformer;
import com.crowdin.platform.transformers.ToggleButtonTransformer;
import com.crowdin.platform.transformers.ToolbarTransformer;
import com.crowdin.platform.transformers.ViewTransformerManager;
import com.crowdin.platform.utils.TextUtils;

/**
 * Entry point for Crowdin. it will be used for initializing Crowdin components, setting new strings,
 * wrapping activity context.
 */
public abstract class Crowdin {

    private static boolean isInitialized = false;
    private static ViewTransformerManager viewTransformerManager;

    private static StringDataManager stringDataManager;

    private Crowdin() {
    }

    /**
     * Initialize Crowdin with default configuration.
     *
     * @param context of the application.
     */
    static void init(Context context) {
        init(context, CrowdinConfig.getDefault());
    }

    /**
     * Initialize Crowdin with the specified configuration.
     *
     * @param context of the application.
     * @param config  of the Crowdin.
     */
    static void init(Context context, CrowdinConfig config) {
        if (isInitialized) {
            return;
        }

        isInitialized = true;
        initCrowdinApi(context);
        initStringDataManager(context, config);
        initViewTransformer();
    }

    /**
     * Wraps context of an activity to provide Crowdin features.
     *
     * @param base context of an activity.
     * @return the Crowdin wrapped context.
     */
    public static ContextWrapper wrapContext(Context base) {
        return CrowdinContextWrapper.wrap(base, stringDataManager, viewTransformerManager);
    }

    /**
     * Set a single string for a language.
     *
     * @param language the string is for.
     * @param key      the string key.
     * @param value    the string value.
     */
    public static void setString(String language, String key, String value) {
        stringDataManager.setString(language, key, value);
    }

    private static void initStringDataManager(Context context, CrowdinConfig config) {
        RemoteStringRepository remoteRepository = new RemoteStringRepository(
                CrowdinRetrofitService.getInstance().getCrowdinApi());
        LocalStringRepository localRepository = new LocalStringRepository(context, config);

        stringDataManager = new StringDataManager(remoteRepository, localRepository);
    }

    @VisibleForTesting
    public static void startLoading(Context context) {
        new StringsLoaderTask(context, stringDataManager).run();
    }

    private static void initViewTransformer() {
        viewTransformerManager = new ViewTransformerManager();
        viewTransformerManager.registerTransformer(new TextViewTransformer());
        viewTransformerManager.registerTransformer(new ToolbarTransformer());
        viewTransformerManager.registerTransformer(new SupportToolbarTransformer());
        viewTransformerManager.registerTransformer(new BottomNavigationViewTransformer());
        viewTransformerManager.registerTransformer(new NavigationViewTransformer());
        viewTransformerManager.registerTransformer(new SpinnerTransformer());
        viewTransformerManager.registerTransformer(new ToggleButtonTransformer());
        viewTransformerManager.registerTransformer(new SwitchTransformer());
    }

    private static void initCrowdinApi(Context context) {
        CrowdinRetrofitService.getInstance().init(context);
    }

    public static void updateMenuItemsText(Menu menu, Resources resources, int menuId) {
        TextUtils.updateMenuItemsText(menu, resources, menuId);
    }
}