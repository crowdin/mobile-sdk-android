package com.crowdin.platform

import android.content.Context
import android.content.ContextWrapper
import com.crowdin.platform.data.DataManager
import com.crowdin.platform.transformer.ViewTransformerManager

/**
 * Main Crowdin context wrapper which wraps the context for providing another layout inflater & resources.
 */
internal class CrowdinContextWrapper private constructor(
    base: Context,
    dataManager: DataManager,
    private val viewTransformerManager: ViewTransformerManager,
) : ContextWrapper(
        CustomResourcesContextWrapper(
            base,
            // Use application context resources to ensure we have access to all system resources
            // This prevents crashes when WebView or other system components try to inflate views
            // See: https://github.com/crowdin/mobile-sdk-android/issues/220
            // See: https://github.com/crowdin/mobile-sdk-android/issues/266
            CrowdinResources(
                base.applicationContext?.resources ?: base.resources,
                dataManager
            ),
        ),
    ) {
    override fun getSystemService(name: String): Any? {
        if (Context.LAYOUT_INFLATER_SERVICE == name) {
            return CrowdinLayoutInflater(
                context = baseContext,
                viewTransformerManager = viewTransformerManager,
            )
        }

        return super.getSystemService(name)
    }

    companion object {
        fun wrap(
            context: Context,
            dataManager: DataManager?,
            viewTransformerManager: ViewTransformerManager,
        ): Context =
            if (dataManager == null) {
                context
            } else {
                CrowdinContextWrapper(
                    context,
                    dataManager,
                    viewTransformerManager,
                )
            }
    }
}
