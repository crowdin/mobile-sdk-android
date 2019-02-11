package com.crowdin.platform.repository;

import com.crowdin.platform.api.LanguageData;

public interface LanguageDataCallback {

    void onDataLoaded(LanguageData languageData);
}