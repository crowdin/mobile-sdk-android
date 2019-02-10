package com.crowdin.platform.repository.local;

import android.support.annotation.Nullable;

import com.crowdin.platform.api.ArrayData;
import com.crowdin.platform.api.LanguageData;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A StringRepository which keeps the stringsData ONLY in memory.
 * <p>
 * it's not ThreadSafe.
 */
public class MemoryStringRepository implements StringRepository {

    private Map<String, LanguageData> stringsData = new LinkedHashMap<>();

    @Override
    public void setString(String language, LanguageData languageData) {
        stringsData.put(language, languageData);
    }

    @Override
    public void setString(String language, String key, String value) {
        LanguageData data = stringsData.get(language);
        if (data == null) {
            stringsData.put(language, new LanguageData(language));
        } else {
            data.getResources().put(key, value);
        }
    }

    @Override
    public String getString(String language, String key) {
        LanguageData languageData = stringsData.get(language);
        if (languageData == null || !languageData.getResources().containsKey(key)) {
            return null;
        }
        return languageData.getResources().get(key);
    }

    @Nullable
    @Override
    public LanguageData getStrings(String language) {
        if (!stringsData.containsKey(language)) {
            return null;
        }

        return stringsData.get(language);
    }

    @Nullable
    @Override
    public String[] getStringArray(String language, String arrayId) {
        LanguageData languageData = stringsData.get(language);
        if (languageData != null) {
            for (ArrayData array : languageData.getArrays()) {
                if (array.getName().equals(arrayId)) {
                    return array.getValues();
                }
            }
        }

        return null;
    }
}