package com.crowdin.platform.transformers

import android.util.AttributeSet
import android.view.View
import android.widget.Switch
import com.crowdin.platform.utils.TextUtils

/**
 * A transformer which transforms Switch: it transforms the text, hint, textOn, textOff attributes.
 */
internal class SwitchTransformer : ViewTransformerManager.Transformer {

    override val viewType: Class<out View>
        get() = Switch::class.java

    override fun transform(view: View, attrs: AttributeSet): View {
        if (!viewType.isInstance(view)) {
            return view
        }

        val resources = view.context.resources
        for (index in 0 until attrs.attributeCount) {
            val attributeName = attrs.getAttributeName(index)
            when (attributeName) {
                Attributes.ATTRIBUTE_ANDROID_TEXT, Attributes.ATTRIBUTE_TEXT -> {
                    val text = TextUtils.getTextForAttribute(attrs, index, resources)
                    if (text != null) {
                        (view as Switch).text = text
                    }
                }

                Attributes.ATTRIBUTE_ANDROID_HINT, Attributes.ATTRIBUTE_HINT -> {
                    val hint = TextUtils.getTextForAttribute(attrs, index, resources)
                    if (hint != null) {
                        (view as Switch).hint = hint
                    }
                }
                Attributes.ATTRIBUTE_TEXT_ON, Attributes.ATTRIBUTE_ANDROID_TEXT_ON -> {
                    val textOn = TextUtils.getTextForAttribute(attrs, index, resources)
                    if (textOn != null) {
                        (view as Switch).textOn = textOn
                    }
                }

                Attributes.ATTRIBUTE_TEXT_OFF, Attributes.ATTRIBUTE_ANDROID_TEXT_OFF -> {
                    val textOff = TextUtils.getTextForAttribute(attrs, index, resources)
                    if (textOff != null) {
                        (view as Switch).textOff = textOff
                    }
                }
            }
        }
        return view
    }
}
