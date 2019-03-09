package com.crowdin.platform.transformers

import android.annotation.TargetApi
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.Toolbar
import com.crowdin.platform.utils.TextUtils

/**
 * A transformer which transforms Toolbar: it transforms the text set as title.
 */
internal class ToolbarTransformer : ViewTransformerManager.Transformer {

    override val viewType: Class<out View>
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        get() = Toolbar::class.java

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun transform(view: View, attrs: AttributeSet): View {
        if (!viewType.isInstance(view)) {
            return view
        }

        val resources = view.context.resources
        for (index in 0 until attrs.attributeCount) {
            val attributeName = attrs.getAttributeName(index)
            when (attributeName) {
                ATTRIBUTE_ANDROID_TITLE, ATTRIBUTE_TITLE -> {
                    val title = TextUtils.getTextForAttribute(attrs, index, view, resources)
                    if (title != null) {
                        (view as Toolbar).title = title
                    }
                }
            }
        }
        return view
    }

    internal companion object {

        private val ATTRIBUTE_TITLE = "title"
        private val ATTRIBUTE_ANDROID_TITLE = "android:title"
    }
}