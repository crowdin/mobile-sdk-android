package com.crowdin.platform;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.crowdin.platform.api.ArrayData;
import com.crowdin.platform.api.PluralData;
import com.crowdin.platform.api.ResourcesResponse;
import com.crowdin.platform.repository.StringDataManager;
import com.crowdin.platform.utils.FileUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Try to load all strings for different languages by a StringsLoader.
 * All string loads happen on background thread, and saving into repository happens on main thread.
 * <p>
 * FIRST it retrieves all supported languages,
 * THEN it retrieves all strings(key, value) for each language.
 */
class StringsLoaderTask extends AsyncTask<Void, Void, Map<String, Map<String, String>>> {

    private WeakReference<Context> context;
    //    TODO: consider API loader
    private StringDataManager stringDataManager;

    StringsLoaderTask(Context context, StringDataManager stringDataManager) {
        this.context = new WeakReference<>(context);
        this.stringDataManager = stringDataManager;
    }

    void run() {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected Map<String, Map<String, String>> doInBackground(Void... voids) {
        ResourcesResponse data = FileUtils.getJsonFromApi(context.get());
        Map<String, Map<String, String>> langStrings = new LinkedHashMap<>();

        if (data == null) return langStrings;

        List<ResourcesResponse.LanguageData> languageDataList = data.getDataList();
        for (ResourcesResponse.LanguageData languageData : languageDataList) {
            Map<String, String> keyValues = new HashMap<>(languageData.getResources());
            addArrays(languageData.getArrays(), keyValues);
            addPlurals(languageData.getPlurals(), keyValues);
            langStrings.put(languageData.getLanguage(), keyValues);
        }

        return langStrings;
    }

    private void addArrays(List<ArrayData> arrays, Map<String, String> keyValues) {
        if (arrays != null) {
            for (ArrayData array : arrays) {
                String value = array.getValues().toString()
                        .replace(",", "|")
                        .replaceAll("\\[", "")
                        .replaceAll("\\]", "");

                keyValues.put(array.getName(), value);
            }
        }
    }

    private void addPlurals(List<PluralData> plurals, Map<String, String> keyValues) {
        if (plurals != null) {
            for (PluralData plural : plurals) {
                keyValues.put(plural.getName(), plural.getQuantity().toString()
                        .replaceAll(",", "|")
                        .replaceAll("=", "^")
                        .replaceAll("\\{", "")
                        .replaceAll("\\}", ""));
            }
        }
    }

    @Override
    protected void onPostExecute(Map<String, Map<String, String>> langStrings) {
        for (Map.Entry<String, Map<String, String>> langItem : langStrings.entrySet()) {
            stringDataManager.setStrings(langItem.getKey(), langItem.getValue());
        }

        Toast.makeText(context.get(), "Resources loaded", Toast.LENGTH_SHORT).show();
    }
}