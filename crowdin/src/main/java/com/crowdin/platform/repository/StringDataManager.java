package com.crowdin.platform.repository;

import com.crowdin.platform.api.LanguageData;
import com.crowdin.platform.repository.local.LocalStringRepository;
import com.crowdin.platform.repository.remote.RemoteStringRepository;

public class StringDataManager {

    private final LocalStringRepository localRepository;
    private final RemoteStringRepository remoteRepository;

    public StringDataManager(RemoteStringRepository remoteRepository, LocalStringRepository localRepository) {
        this.remoteRepository = remoteRepository;
        this.localRepository = localRepository;

        remoteRepository.checkUpdates();
    }

    public String getString(String language, String stringKey) {
        return localRepository.getString(language, stringKey);
    }

    public void setString(String language, LanguageData languageData) {
        localRepository.setString(language, languageData);
    }

    public void setString(String language, String key, String value) {
        localRepository.setString(language, key, value);
    }

    public String[] getStringArray(String language, String arrayId) {
        return localRepository.getStringArray(language, arrayId);
    }
}
