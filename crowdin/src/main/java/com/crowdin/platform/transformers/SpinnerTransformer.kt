package com.crowdin.platform.transformers

import android.util.AttributeSet
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner

/**
 * A transformer which transforms Spinner: it transforms the entries attribute
 * and uses default android.layouts for displaying
 */
internal class SpinnerTransformer : Transformer {

    override val viewType: Class<out View>
        get() = Spinner::class.java

    override fun transform(view: View, attrs: AttributeSet): View {
        if (!viewType.isInstance(view)) {
            return view
        }

        val resources = view.context.resources
        for (index in 0 until attrs.attributeCount) {
            val attributeName = attrs.getAttributeName(index)
            when (attributeName) {
                Attributes.ATTRIBUTE_ENTRIES, Attributes.ATTRIBUTE_ANDROID_ENTRIES -> {
                    val value = attrs.getAttributeValue(index)
                    if (value != null && value.startsWith("@")) {
                        val stringArray = resources.getStringArray(attrs.getAttributeResourceValue(index, 0))
                        val adapter = ArrayAdapter(view.context,
                                android.R.layout.simple_spinner_item,
                                stringArray)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        (view as Spinner).adapter = adapter
                    }
                }
            }
        }
        return view
    }
}
