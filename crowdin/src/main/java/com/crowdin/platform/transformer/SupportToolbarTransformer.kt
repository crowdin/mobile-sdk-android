package com.crowdin.platform.transformer

import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.crowdin.platform.data.TextMetaDataProvider
import com.crowdin.platform.data.model.TextMetaData
import com.crowdin.platform.util.FeatureFlags
import com.crowdin.platform.util.TextUtils

/**
 * A transformer which transforms Toolbar(from support library): it transforms the text set as title.
 */
internal class SupportToolbarTransformer(textMetaDataProvider: TextMetaDataProvider) : BaseToolbarTransformer(textMetaDataProvider) {

    override val viewType = Toolbar::class.java

    override fun transform(view: View, attrs: AttributeSet): View {
        if (!viewType.isInstance(view)) {
            return view
        }

        view as Toolbar
        val textMetaData = TextMetaData()

        var child: TextView? = null
        if (FeatureFlags.isRealTimeUpdateEnabled) {
            child = findChildView(view)
            addTextWatcherToChild(child)
        }

        val resources = view.context.resources
        for (index in 0 until attrs.attributeCount) {
            val id = TextUtils.getTextAttributeKey(resources, attrs, index)
            when (attrs.getAttributeName(index)) {
                Attributes.ATTRIBUTE_APP_TITLE, Attributes.ATTRIBUTE_TITLE -> {
                    val title = TextUtils.getTextForAttribute(attrs, index, resources)
                    if (title != null) {
                        view.title = title
                        if (FeatureFlags.isRealTimeUpdateEnabled) {
                            if (id != null && child != null) {
                                textMetaData.textAttributeKey = id
                            }
                        }
                    }
                }
            }
        }

        if (FeatureFlags.isRealTimeUpdateEnabled) {
            child?.let {
                createdViews[it] = textMetaData
                listener?.onChange()
            }
            addHierarchyChangeListener(view)
        }

        return view
    }
}
