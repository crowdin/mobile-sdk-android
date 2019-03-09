package com.crowdin.platform.transformers

import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.crowdin.platform.utils.TextUtils

/**
 * A transformer which transforms TextView(or any view extends it like Button, EditText, ...):
 * it transforms "text" & "hint" attributes.
 */
internal class TextViewTransformer : ViewTransformerManager.Transformer {

    override val viewType: Class<out View>
        get() = TextView::class.java

    override fun transform(view: View, attrs: AttributeSet): View {
        if (!viewType.isInstance(view)) {
            return view
        }

        val resources = view.context.resources
        for (index in 0 until attrs.attributeCount) {
            val attributeName = attrs.getAttributeName(index)
            when (attributeName) {
                Attributes.ATTRIBUTE_ANDROID_TEXT, Attributes.ATTRIBUTE_TEXT -> {
                    val text = TextUtils.getTextForAttribute(attrs, index, view, resources)
                    if (text != null) {
                        (view as TextView).text = text
                    }
                }

                Attributes.ATTRIBUTE_ANDROID_HINT, Attributes.ATTRIBUTE_HINT -> {
                    val hint = TextUtils.getTextForAttribute(attrs, index, view, resources)
                    if (hint != null) {
                        (view as TextView).hint = hint
                    }
                }
            }
        }
        return view
    }
}
