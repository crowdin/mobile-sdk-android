package com.crowdin.platform.repository.local;

import android.content.Context;

import com.crowdin.platform.CrowdinConfig;
import com.crowdin.platform.api.LanguageData;

public class LocalStringRepository {

    private StringRepository localRepository;

    public LocalStringRepository(Context context, CrowdinConfig config) {
        if (config.isPersist()) {
            localRepository = new SharedPrefStringRepository(context);
        } else {
            localRepository = new MemoryStringRepository();
        }
    }

    public String getString(String language, String stringKey) {
        return localRepository.getString(language, stringKey);
    }

    public void saveLanguageData(String language, LanguageData languageData) {
        localRepository.saveLanguageData(language, languageData);
    }

    public void setString(String language, String key, String value) {
        localRepository.setString(language, key, value);
    }

    public String[] getStringArray(String language, String key) {
        return localRepository.getStringArray(language, key);
    }
}
