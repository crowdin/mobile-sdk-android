package com.crowdin.platform.repository;

import com.crowdin.platform.repository.local.LocalStringRepository;
import com.crowdin.platform.repository.remote.RemoteStringRepository;

import java.util.Map;

public class StringDataManager {

    private final LocalStringRepository localRepository;
    private final RemoteStringRepository remoteRepository;

    public StringDataManager(RemoteStringRepository remoteRepository, LocalStringRepository localRepository) {
        this.remoteRepository = remoteRepository;
        this.localRepository = localRepository;

        remoteRepository.checkUpdates();
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
