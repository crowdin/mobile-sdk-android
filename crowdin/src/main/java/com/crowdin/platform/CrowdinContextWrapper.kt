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
    private val viewTransformerManager: ViewTransformerManager
) : ContextWrapper(
    CustomResourcesContextWrapper(
        base,
        CrowdinResources(base.resources, dataManager)
    )
) {

    override fun getSystemService(name: String): Any? {
        if (Context.LAYOUT_INFLATER_SERVICE == name) {
            return CrowdinLayoutInflater(
                context = baseContext,
                viewTransformerManager = viewTransformerManager
            )
        }

        return super.getSystemService(name)
    }

    companion object {

        fun wrap(
            context: Context,
            dataManager: DataManager?,
            viewTransformerManager: ViewTransformerManager
        ): Context {
            return if (dataManager == null) {
                context
            } else {
                CrowdinContextWrapper(
                    context,
                    dataManager,
                    viewTransformerManager
                )
            }
        }
    }
}
