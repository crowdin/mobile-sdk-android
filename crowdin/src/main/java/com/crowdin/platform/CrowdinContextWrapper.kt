package com.crowdin.platform

import android.content.Context
import android.content.ContextWrapper
import android.view.LayoutInflater

import com.crowdin.platform.data.StringDataManager
import com.crowdin.platform.transformer.ViewTransformerManager

/**
 * Main Crowdin context wrapper which wraps the context for providing another layout inflater & resources.
 */
internal class CrowdinContextWrapper private constructor(base: Context,
                                                         stringDataManager: StringDataManager,
                                                         private val viewTransformerManager: ViewTransformerManager) :
        ContextWrapper(CustomResourcesContextWrapper(base, CrowdinResources(base.resources, stringDataManager))) {

    override fun getSystemService(name: String): Any {
        if (Context.LAYOUT_INFLATER_SERVICE == name) {
            return CrowdinLayoutInflater(LayoutInflater.from(baseContext),
                    this, viewTransformerManager)
        }

        return super.getSystemService(name)
    }

    internal companion object {

        fun wrap(context: Context, stringDataManager: StringDataManager?,
                 viewTransformerManager: ViewTransformerManager): Context {
            return if (stringDataManager == null) context else CrowdinContextWrapper(context,
                    stringDataManager, viewTransformerManager)
        }
    }
}