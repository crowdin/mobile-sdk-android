package com.crowdin.platform.transformers

import android.annotation.TargetApi
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import android.widget.Toolbar
import com.crowdin.platform.repository.TextIdProvider
import com.crowdin.platform.repository.local.TextMetaData
import com.crowdin.platform.utils.FeatureFlags
import com.crowdin.platform.utils.TextUtils

/**
 * A transformer which transforms Toolbar: it transforms the text set as title.
 */
internal class ToolbarTransformer(textIdProvider: TextIdProvider) : BaseToolbarTransformer(textIdProvider) {

    override val viewType = Toolbar::class.java

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun transform(view: View, attrs: AttributeSet): View {
        if (!viewType.isInstance(view)) {
            return view
        }
        view as Toolbar
        val textMetaData = TextMetaData()
        textMetaData.textAttributeKey = Transformer.UNKNOWN_ID

        var child: TextView? = null
        if (FeatureFlags.isRealTimeUpdateEnabled) {
            child = findChildView(view)
            addTextWatcherToChild(child)
        }

        val resources = view.context.resources
        for (index in 0 until attrs.attributeCount) {
            val id = TextUtils.getTextAttributeKey(resources, attrs, index)
            when (attrs.getAttributeName(index)) {
                Attributes.ATTRIBUTE_ANDROID_TITLE, Attributes.ATTRIBUTE_TITLE -> {
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
            child?.let { createdViews[it] = textMetaData }
            addHierarchyChangeListener(view)
        }

        return view
    }
}