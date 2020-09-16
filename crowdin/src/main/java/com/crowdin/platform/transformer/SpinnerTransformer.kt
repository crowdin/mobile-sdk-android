package com.crowdin.platform.transformer

import android.util.AttributeSet
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.crowdin.platform.util.FeatureFlags
import java.util.WeakHashMap

/**
 * A transformer which transforms Spinner: it transforms the entries attribute
 * and uses default android.layouts for displaying
 */
internal class SpinnerTransformer : BaseTransformer() {

    private val createdView = WeakHashMap<Spinner, Int>()

    override val viewType = Spinner::class.java

    override fun transform(view: View, attrs: AttributeSet): View {
        if (!viewType.isInstance(view)) {
            return view
        }

        view as Spinner
        for (index in 0 until attrs.attributeCount) {
            when (attrs.getAttributeName(index)) {
                Attributes.ATTRIBUTE_ENTRIES, Attributes.ATTRIBUTE_ANDROID_ENTRIES -> {
                    val value = attrs.getAttributeValue(index)

                    if (value != null && value.startsWith("@")) {
                        val resId = attrs.getAttributeResourceValue(index, 0)
                        setAdapter(view, resId)

                        if (FeatureFlags.isRealTimeUpdateEnabled || FeatureFlags.isScreenshotEnabled) {
                            createdView[view] = resId
                        }
                    }
                }
            }
        }
        return view
    }

    override fun invalidate() {
        createdView.forEach {
            setAdapter(it.key, it.value)
        }
    }

    private fun setAdapter(view: Spinner, resId: Int) {
        val resources = view.context.resources
        val stringArray = resources.getStringArray(resId)
        val adapter = ArrayAdapter(
            view.context,
            android.R.layout.simple_spinner_item,
            stringArray
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.adapter = adapter
    }
}
