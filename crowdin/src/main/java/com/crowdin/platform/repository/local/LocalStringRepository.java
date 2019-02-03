package com.crowdin.platform.repository.local;

import android.content.Context;

import com.crowdin.platform.CrowdinConfig;

import java.util.Map;

public class LocalStringRepository {

    private StringRepository localRepository;

    public LocalStringRepository(Context context, CrowdinConfig config) {
        if (config.isPersist()) {
            localRepository = new SharedPrefStringRepository(context);
        } else {
            localRepository = new MemoryStringRepository();
        }
    }

    public String getString(String currentLanguage, String stringKey) {
        return localRepository.getString(currentLanguage, stringKey);
    }

    public void setStrings(String language, Map<String, String> newStrings) {
        localRepository.setStrings(language, newStrings);
    }

    public void setString(String language, String key, String value) {
        localRepository.setString(language, key, value);
    }
}
