package com.crowdin.platform.repository.local;

import android.support.annotation.Nullable;

import com.crowdin.platform.api.ArrayData;
import com.crowdin.platform.api.LanguageData;
import com.crowdin.platform.api.PluralData;
import com.crowdin.platform.utils.LocaleUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A LocalRepository which keeps the stringsData ONLY in memory.
 * <p>
 * it's not ThreadSafe.
 */
public class MemoryLocalRepository implements LocalRepository {

    private Map<String, LanguageData> stringsData = new LinkedHashMap<>();

    @Override
    public void saveLanguageData(LanguageData languageData) {
        stringsData.put(languageData.getLanguage(), languageData);
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
    public String[] getStringArray(String key) {
        LanguageData languageData = stringsData.get(LocaleUtils.getCurrentLanguage());
        if (languageData != null) {
            for (ArrayData array : languageData.getArrays()) {
                if (array.getName().equals(key)) {
                    return array.getValues();
                }
            }
        }

        return null;
    }

    @Nullable
    @Override
    public String getStringPlural(String resourceKey, String quantityKey) {
        LanguageData languageData = stringsData.get(LocaleUtils.getCurrentLanguage());
        if (languageData != null) {
            for (PluralData pluralData : languageData.getPlurals()) {
                if (pluralData.getName().equals(resourceKey)) {
                    return pluralData.getQuantity().get(quantityKey);
                }
            }
        }

        return null;
    }

    @Override
    public boolean isExist(String language) {
        return stringsData.get(language) != null;
    }
}