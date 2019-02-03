package com.crowdin.platform;

import android.content.Context;
import android.content.ContextWrapper;

import com.crowdin.platform.api.CrowdinRetrofitService;
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

import java.util.List;
import java.util.Map;

/**
 * Entry point for Crowdin. it will be used for initializing Crowdin components, setting new strings,
 * wrapping activity context.
 */
public abstract class Crowdin {

    private static boolean isInitialized = false;
    private static ViewTransformerManager viewTransformerManager;

    private static StringDataManager stringDataManager;

    /**
     * Initialize Crowdin with default configuration.
     *
     * @param context of the application.
     */
    public static void init(Context context) {
        init(context, CrowdinConfig.getDefault());
    }

    /**
     * Initialize Crowdin with the specified configuration.
     *
     * @param context of the application.
     * @param config  of the Crowdin.
     */
    public static void init(Context context, CrowdinConfig config) {
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
     * Set strings of a language.
     *
     * @param language   the strings are for.
     * @param newStrings the strings of the language.
     */
    public static void setStrings(String language, Map<String, String> newStrings) {
        stringDataManager.setStrings(language, newStrings);
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

    public static void startLoading(Context context) {
        new StringsLoaderTask(context, null, stringDataManager).run();
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

    /**
     * Loader of strings skeleton. Clients can implement this interface if they want to load strings on initialization.
     * First the list of languages will be asked, then strings of each language.
     */
    public interface StringsLoader {

        /**
         * Get supported languages.
         *
         * @return the list of languages.
         */
        List<String> getLanguages();

        /**
         * Get strings of a language as keys &amp; values.
         *
         * @param language of the strings.
         * @return the strings as (key, value).
         */
        Map<String, String> getStrings(String language);
    }
}