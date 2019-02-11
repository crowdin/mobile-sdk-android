package com.crowdin.platform.repository.remote;

import android.content.Context;

import com.crowdin.platform.api.LanguageData;
import com.crowdin.platform.repository.LanguageDataCallback;

/**
 * Repository of strings from network.
 */
public interface RemoteRepository {

    /**
     * Save {@link LanguageData} for a specific language.
     *  @param currentLocale        the default device Locale.
     * @param languageDataCallback delivers data back to caller.
     */
    void fetchData(Context context, String currentLocale, LanguageDataCallback languageDataCallback);
}
