package com.crowdin.platform.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableIntStateOf
import com.crowdin.platform.Crowdin
import com.crowdin.platform.LoadingStateListener
import com.crowdin.platform.example.ui.CrowdinTodoApp
import com.crowdin.platform.example.ui.LocalTranslationsVersion
import com.crowdin.platform.example.ui.theme.CrowdinTodoTheme
import com.crowdin.platform.util.getLocale

class MainActivity : BaseActivity(), LoadingStateListener {

    /**
     * Incremented whenever the SDK delivers new translations. Every string in the UI is read
     * through `localizedString`, which observes this value and recomposes with the new text.
     */
    private val translationsVersion = mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CompositionLocalProvider(LocalTranslationsVersion provides translationsVersion.intValue) {
                CrowdinTodoTheme {
                    CrowdinTodoApp()
                }
            }
        }

        // Register data observer. When data loaded you can invalidate your UI to apply new resources.
        Crowdin.registerDataLoadingObserver(this)

        // Test of public API that returns string by key or empty if not found
        val languageTag = this.resources.configuration.getLocale().toLanguageTag()
        Crowdin.getString(languageTag, "settings")
    }

    override fun onDestroy() {
        super.onDestroy()
        Crowdin.unregisterDataLoadingObserver(this)
    }

    override fun onDataChanged() {
        translationsVersion.intValue++
    }

    override fun onFailure(throwable: Throwable) {
    }
}
