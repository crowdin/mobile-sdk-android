package com.crowdin.platform.repository;

import android.content.Context;

import com.crowdin.platform.api.LanguageData;
import com.crowdin.platform.repository.local.LocalRepository;
import com.crowdin.platform.repository.remote.RemoteRepository;
import com.crowdin.platform.utils.LocaleUtils;

public class StringDataManager {

    private final LocalRepository localRepository;
    private final RemoteRepository remoteRepository;

    public StringDataManager(RemoteRepository remoteRepository, LocalRepository localRepository) {
        this.remoteRepository = remoteRepository;
        this.localRepository = localRepository;
    }

    public String getString(String language, String stringKey) {
        return localRepository.getString(language, stringKey);
    }

    public void setString(String language, String key, String value) {
        localRepository.setString(language, key, value);
    }

    public String[] getStringArray(String key) {
        return localRepository.getStringArray(key);
    }

    public String getStringPlural(String resourceKey, String quantityKey) {
        return localRepository.getStringPlural(resourceKey, quantityKey);
    }

    public void updateData(Context context) {
        String language = LocaleUtils.getCurrentLanguage();

        if (localRepository.isExist(language)) return;

        remoteRepository.fetchData(context, language, new LanguageDataCallback() {
            @Override
            public void onDataLoaded(LanguageData languageData) {
                localRepository.saveLanguageData(languageData);
            }
        });
    }
}
