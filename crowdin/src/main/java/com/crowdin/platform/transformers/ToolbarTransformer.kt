package com.crowdin.platform.transformers

import android.annotation.TargetApi
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.Toolbar
import com.crowdin.platform.repository.TextIdProvider
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
        val child = findChildView(view)
        addTextWatcherToChild(child)

        val resources = view.context.resources
        for (index in 0 until attrs.attributeCount) {
            val attributeName = attrs.getAttributeName(index)
            when (attributeName) {
                Attributes.ATTRIBUTE_ANDROID_TITLE, Attributes.ATTRIBUTE_TITLE -> {
                    val title = TextUtils.getTextForAttribute(attrs, index, resources)
                    if (title != null) {
                        view.title = title
                        val id = TextUtils.getTextAttributeKey(resources, attrs, index)
                        if (id != null && child != null) {
                            createdViews[child] = id
                        }
                    }
                }
            }
        }

        addHierarchyChangeListener(view)

        return view
    }
}