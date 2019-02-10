package com.crowdin.platform;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.crowdin.platform.api.LanguageData;
import com.crowdin.platform.api.ResourcesResponse;
import com.crowdin.platform.repository.StringDataManager;
import com.crowdin.platform.utils.FileUtils;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

/**
 * Try to load all strings for different languages by a StringsLoader.
 * All string loads happen on background thread, and saving into repository happens on main thread.
 * <p>
 * FIRST it retrieves all supported languages,
 * THEN it retrieves all strings(key, value) for each language.
 */
class StringsLoaderTask extends AsyncTask<Void, Void, List<LanguageData>> {

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
    protected List<LanguageData> doInBackground(Void... voids) {
        List<LanguageData> dataList = Collections.emptyList();
        ResourcesResponse data = FileUtils.getJsonFromApi(context.get());
        if (data != null) {
            dataList = data.getDataList();
        }

        return dataList;
    }

    @Override
    protected void onPostExecute(List<LanguageData> languageDataList) {
        for (LanguageData languageData : languageDataList) {
            stringDataManager.saveLanguageData(languageData.getLanguage(), languageData);
        }

        Toast.makeText(context.get(), "Resources loaded", Toast.LENGTH_SHORT).show();
    }
}