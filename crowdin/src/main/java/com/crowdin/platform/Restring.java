package com.crowdin.platform;

import android.content.Context;
import android.content.ContextWrapper;

import com.crowdin.platform.repository.MemoryStringRepository;
import com.crowdin.platform.repository.SharedPrefStringRepository;
import com.crowdin.platform.repository.StringRepository;
import com.crowdin.platform.transformers.BottomNavigationViewTransformer;
import com.crowdin.platform.transformers.SupportToolbarTransformer;
import com.crowdin.platform.transformers.TextViewTransformer;
import com.crowdin.platform.transformers.ToolbarTransformer;
import com.crowdin.platform.transformers.ViewTransformerManager;

import java.util.List;
import java.util.Map;

/**
 * Entry point for Restring. it will be used for initializing Restring components, setting new strings,
 * wrapping activity context.
 */
public abstract class Restring {

    private static boolean isInitialized = false;
    private static StringRepository stringRepository;
    private static ViewTransformerManager viewTransformerManager;

    /**
     * Initialize Restring with default configuration.
     *
     * @param context of the application.
     */
    public static void init(Context context) {
        init(context, RestringConfig.getDefault());
    }

    /**
     * Initialize Restring with the specified configuration.
     *
     * @param context of the application.
     * @param config  of the Restring.
     */
    public static void init(Context context, RestringConfig config) {
        if (isInitialized) {
            return;
        }

        isInitialized = true;
        initStringRepository(context, config);
        initViewTransformer();
    }

    /**
     * Wraps context of an activity to provide Restring features.
     *
     * @param base context of an activity.
     * @return the Restring wrapped context.
     */
    public static ContextWrapper wrapContext(Context base) {
        return RestringContextWrapper.wrap(base, stringRepository, viewTransformerManager);
    }

    /**
     * Set strings of a language.
     *
     * @param language   the strings are for.
     * @param newStrings the strings of the language.
     */
    public static void setStrings(String language, Map<String, String> newStrings) {
        stringRepository.setStrings(language, newStrings);
    }

    /**
     * Set a single string for a language.
     *
     * @param language the string is for.
     * @param key      the string key.
     * @param value    the string value.
     */
    public static void setString(String language, String key, String value) {
        stringRepository.setString(language, key, value);
    }

    static StringRepository getStringRepository() {
        return stringRepository;
    }

    private static void initStringRepository(Context context, RestringConfig config) {
        if (config.isPersist()) {
            stringRepository = new SharedPrefStringRepository(context);
        } else {
            stringRepository = new MemoryStringRepository();
        }

        // TODO: handle default loading if needed on init
//        if (config.getStringsLoader() != null) {
//            new StringsLoaderTask(context, config.getStringsLoader(), stringRepository).run(),
//        }
    }

    public static void startLoading(Context context) {
        new StringsLoaderTask(context, null, stringRepository).run();
    }

    private static void initViewTransformer() {
        viewTransformerManager = new ViewTransformerManager();
        viewTransformerManager.registerTransformer(new TextViewTransformer());
        viewTransformerManager.registerTransformer(new ToolbarTransformer());
        viewTransformerManager.registerTransformer(new SupportToolbarTransformer());
        viewTransformerManager.registerTransformer(new BottomNavigationViewTransformer());
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