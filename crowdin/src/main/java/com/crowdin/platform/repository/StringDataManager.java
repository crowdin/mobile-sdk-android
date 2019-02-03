package com.crowdin.platform.repository;

import com.crowdin.platform.api.CrowdinApi;
import com.crowdin.platform.repository.local.StringRepository;

import java.util.Map;

public class StringDataManager {

    private final StringRepository stringRepository;
    private final CrowdinApi crowdinApi;

    public StringDataManager(CrowdinApi crowdinApi, StringRepository stringRepository) {
        this.stringRepository = stringRepository;
        this.crowdinApi = crowdinApi;
    }

    public String getString(String currentLanguage, String stringKey) {
        return stringRepository.getString(currentLanguage, stringKey);
    }

    public void setStrings(String language, Map<String, String> newStrings) {
        stringRepository.setStrings(language, newStrings);
    }

    public void setString(String language, String key, String value) {
        stringRepository.setString(language, key, value);
    }
}
