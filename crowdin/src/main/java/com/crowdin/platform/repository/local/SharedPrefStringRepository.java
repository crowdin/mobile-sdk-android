package com.crowdin.platform.repository.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.crowdin.platform.api.LanguageData;
import com.google.gson.Gson;

import java.util.Map;

/**
 * A StringRepository which saves/loads the strings in Shared Preferences.
 * it also keeps the strings in memory by using MemoryStringRepository internally for faster access.
 * <p>
 * it's not ThreadSafe.
 */
public class SharedPrefStringRepository implements StringRepository {
    private static final String SHARED_PREF_NAME = "Restrings";

    private SharedPreferences sharedPreferences;
    private StringRepository memoryStringRepository = new MemoryStringRepository();

    public SharedPrefStringRepository(Context context) {
        initSharedPreferences(context);
        loadStrings();
    }

    @Override
    public void saveLanguageData(String language, LanguageData languageData) {
        memoryStringRepository.saveLanguageData(language, languageData);
        saveStrings(language, languageData);
    }

    @Override
    public void setString(String language, String key, String value) {
        memoryStringRepository.setString(language, key, value);

        LanguageData languageData = memoryStringRepository.getStrings(language);
        if (languageData == null) {
            return;
        }
        languageData.getResources().put(key, value);
        saveStrings(language, languageData);
    }

    @Override
    public String getString(String language, String key) {
        return memoryStringRepository.getString(language, key);
    }

    @Nullable
    @Override
    public LanguageData getStrings(String language) {
        return memoryStringRepository.getStrings(language);
    }

    @Override
    public String[] getStringArray(String language, String key) {
        return memoryStringRepository.getStringArray(language, key);
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
            String language = entry.getKey();
            LanguageData languageData = deserializeKeyValues(value);
            memoryStringRepository.saveLanguageData(language, languageData);
        }
    }

    private void saveStrings(String language, LanguageData languageData) {
        Gson gson = new Gson();
        String json = gson.toJson(languageData);
        sharedPreferences.edit()
                .putString(language, json)
                .apply();
    }

    private LanguageData deserializeKeyValues(String content) {
        Gson gson = new Gson();
        return gson.fromJson(content, LanguageData.class);
    }
}
