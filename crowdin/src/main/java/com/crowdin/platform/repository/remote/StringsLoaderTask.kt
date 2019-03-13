package com.crowdin.platform.repository.remote

import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import com.crowdin.platform.repository.remote.api.LanguageData
import com.crowdin.platform.repository.LanguageDataCallback
import com.crowdin.platform.utils.FileUtils
import java.lang.ref.WeakReference

/**
 * Try to load all strings for different languages by a StringsLoader.
 * All string loads happen on background thread, and saving into repository happens on main thread.
 *
 *
 * FIRST it retrieves all supported languages,
 * THEN it retrieves all strings(key, value) for each language.
 */
internal class StringsLoaderTask internal constructor(context: Context,
                                                      private val languageDataCallback: LanguageDataCallback) :
        AsyncTask<Void, Void, LanguageData>() {

    private val context: WeakReference<Context> = WeakReference(context)

    internal fun run() {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    override fun doInBackground(vararg voids: Void): LanguageData? {
        var languageData: LanguageData? = null
        val response = FileUtils.getJsonFromApi(context.get())
        if (response != null) {
            languageData = response.data
        }

        return languageData
    }

    override fun onPostExecute(languageData: LanguageData) {
        languageDataCallback.onDataLoaded(languageData)

        Toast.makeText(context.get(), "Resources loaded", Toast.LENGTH_SHORT).show()
    }
}