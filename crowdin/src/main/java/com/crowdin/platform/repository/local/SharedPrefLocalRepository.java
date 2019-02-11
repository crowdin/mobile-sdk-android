package com.crowdin.platform.repository.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.crowdin.platform.api.LanguageData;
import com.google.gson.Gson;

import java.util.Map;

/**
 * A LocalRepository which saves/loads the strings in Shared Preferences.
 * it also keeps the strings in memory by using MemoryLocalRepository internally for faster access.
 * <p>
 * it's not ThreadSafe.
 */
public class SharedPrefLocalRepository implements LocalRepository {

    private static final String SHARED_PREF_NAME = "Restrings";

    private SharedPreferences sharedPreferences;
    private LocalRepository memoryLocalRepository = new MemoryLocalRepository();

    SharedPrefLocalRepository(Context context) {
        initSharedPreferences(context);
        loadStrings();
    }

    @Override
    public void saveLanguageData(LanguageData languageData) {
        memoryLocalRepository.saveLanguageData(languageData);
        saveStrings(languageData);
    }

    @Override
    public void setString(String language, String key, String value) {
        memoryLocalRepository.setString(language, key, value);

        LanguageData languageData = memoryLocalRepository.getStrings(language);
        if (languageData == null) {
            return;
        }
        languageData.getResources().put(key, value);
        saveStrings(languageData);
    }

    @Override
    public String getString(String language, String key) {
        return memoryLocalRepository.getString(language, key);
    }

    @Nullable
    @Override
    public LanguageData getStrings(String language) {
        return memoryLocalRepository.getStrings(language);
    }

    @Override
    public String[] getStringArray(String language, String key) {
        return memoryLocalRepository.getStringArray(language, key);
    }

    @Override
    public boolean isExist(String language) {
        return memoryLocalRepository.isExist(language);
    }

    private void initSharedPreferences(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    private void loadStrings() {
        Map<String, ?> strings = sharedPreferences.getAll();

        for (Map.Entry<String, ?> entry : strings.entrySet()) {
            if (!(entry.getValue() instanceof String)) {
                continue;
            }

            String value = (String) entry.getValue();
            LanguageData languageData = deserializeKeyValues(value);
            memoryLocalRepository.saveLanguageData(languageData);
        }
    }

    private void saveStrings(LanguageData languageData) {
        Gson gson = new Gson();
        String json = gson.toJson(languageData);
        sharedPreferences.edit()
                .putString(languageData.getLanguage(), json)
                .apply();
    }

    private LanguageData deserializeKeyValues(String content) {
        Gson gson = new Gson();
        return gson.fromJson(content, LanguageData.class);
    }
}
