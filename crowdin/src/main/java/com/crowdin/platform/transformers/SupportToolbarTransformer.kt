package com.crowdin.platform.transformers

import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.View
import com.crowdin.platform.utils.TextUtils

/**
 * A transformer which transforms Toolbar(from support library): it transforms the text set as title.
 */
internal class SupportToolbarTransformer : ViewTransformerManager.Transformer {

    override val viewType: Class<out View>
        get() = Toolbar::class.java

    override fun transform(view: View, attrs: AttributeSet): View {
        if (!viewType.isInstance(view)) {
            return view
        }

        val resources = view.context.resources
        for (index in 0 until attrs.attributeCount) {
            val attributeName = attrs.getAttributeName(index)
            when (attributeName) {
                Attributes.ATTRIBUTE_APP_TITLE, Attributes.ATTRIBUTE_TITLE -> {
                    val title = TextUtils.getTextForAttribute(attrs, index, resources)
                    if (title != null) {
                        (view as Toolbar).title = title
                    }
                }
            }
        }
        return view
    }
}
