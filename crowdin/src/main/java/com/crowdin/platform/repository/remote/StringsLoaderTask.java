package com.crowdin.platform.repository.remote;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.crowdin.platform.api.LanguageData;
import com.crowdin.platform.api.ResourcesResponse;
import com.crowdin.platform.repository.LanguageDataCallback;
import com.crowdin.platform.utils.FileUtils;

import java.lang.ref.WeakReference;

/**
 * Try to load all strings for different languages by a StringsLoader.
 * All string loads happen on background thread, and saving into repository happens on main thread.
 * <p>
 * FIRST it retrieves all supported languages,
 * THEN it retrieves all strings(key, value) for each language.
 */
public class StringsLoaderTask extends AsyncTask<Void, Void, LanguageData> {

    private final LanguageDataCallback languageDataCallback;
    private WeakReference<Context> context;

    StringsLoaderTask(Context context, LanguageDataCallback languageDataCallback) {
        this.context = new WeakReference<>(context);
        this.languageDataCallback = languageDataCallback;
    }

    void run() {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected LanguageData doInBackground(Void... voids) {
        LanguageData languageData = null;
        ResourcesResponse response = FileUtils.getJsonFromApi(context.get());
        if (response != null) {
            languageData = response.getData();
        }

        return languageData;
    }

    @Override
    protected void onPostExecute(LanguageData languageData) {
        languageDataCallback.onDataLoaded(languageData);

        Toast.makeText(context.get(), "Resources loaded", Toast.LENGTH_SHORT).show();
    }
}