package com.crowdin.platform.transformer

import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.Switch
import android.widget.TextView
import android.widget.ToggleButton
import com.crowdin.platform.data.TextMetaDataProvider
import com.crowdin.platform.data.model.TextMetaData
import com.crowdin.platform.util.FeatureFlags
import com.crowdin.platform.util.TextUtils
import java.lang.ref.WeakReference

/**
 * A transformer which transforms TextView(or any view extends it like Button, EditText, ...):
 * it transforms "text" & "hint" attributes.
 */
internal class TextViewTransformer(
    val textMetaDataProvider: TextMetaDataProvider,
) : BaseTransformer() {
    override val viewType = TextView::class.java

    override fun transform(
        view: View,
        attrs: AttributeSet,
    ): View {
        if (!viewType.isInstance(view)) {
            return view
        }

        view as TextView
        val textMetaData = TextMetaData()

        val resources = view.context.resources
        for (index in 0 until attrs.attributeCount) {
            val id = TextUtils.getTextAttributeKey(resources, attrs, index)

            when (attrs.getAttributeName(index)) {
                Attributes.ATTRIBUTE_ANDROID_TEXT, Attributes.ATTRIBUTE_TEXT -> {
                    val text = TextUtils.getTextForAttribute(attrs, index, resources)
                    if (text != null) {
                        view.text = text
                        if (FeatureFlags.isRealTimeUpdateEnabled || FeatureFlags.isScreenshotEnabled) {
                            if (id != null) {
                                textMetaData.textAttributeKey = id
                            }
                        }
                    }
                }
                Attributes.ATTRIBUTE_ANDROID_HINT, Attributes.ATTRIBUTE_HINT -> {
                    val hint = TextUtils.getTextForAttribute(attrs, index, resources)
                    if (hint != null) {
                        view.hint = hint
                        if (FeatureFlags.isRealTimeUpdateEnabled || FeatureFlags.isScreenshotEnabled) {
                            if (id != null) {
                                textMetaData.hintAttributeKey = id
                            }
                        }
                    }
                }
                Attributes.ATTRIBUTE_TEXT_ON, Attributes.ATTRIBUTE_ANDROID_TEXT_ON -> {
                    val textOn = TextUtils.getTextForAttribute(attrs, index, resources)
                    if (textOn != null) {
                        when (view) {
                            is Switch -> view.textOn = textOn
                            is ToggleButton -> view.textOn = textOn
                        }
                        if (FeatureFlags.isRealTimeUpdateEnabled || FeatureFlags.isScreenshotEnabled) {
                            if (id != null) {
                                textMetaData.textOnAttributeKey = id
                            }
                        }
                    }
                }
                Attributes.ATTRIBUTE_TEXT_OFF, Attributes.ATTRIBUTE_ANDROID_TEXT_OFF -> {
                    val textOff = TextUtils.getTextForAttribute(attrs, index, resources)
                    if (textOff != null) {
                        when (view) {
                            is Switch -> view.textOff = textOff
                            is ToggleButton -> view.textOff = textOff
                        }
                        if (FeatureFlags.isRealTimeUpdateEnabled || FeatureFlags.isScreenshotEnabled) {
                            if (id != null) {
                                textMetaData.textOffAttributeKey = id
                            }
                        }
                    }
                }
            }
        }

        if (FeatureFlags.isRealTimeUpdateEnabled || FeatureFlags.isScreenshotEnabled) {
            addViewWithData(view, textMetaData)
            view.addTextChangedListener(Watcher(WeakReference(view)))
            listener?.onChange(Pair(view, textMetaData))
        }

        return view
    }

    inner class Watcher(
        var view: WeakReference<TextView>,
    ) : TextWatcher {
        // Handle case when @string res set programmatically
        override fun afterTextChanged(s: Editable?) {
            view.get()?.let {
                val resultData = textMetaDataProvider.provideTextMetaData(s.toString())
                var textMetaData = getViewTextMetaData(it)
                if (textMetaData == null) {
                    textMetaData = TextMetaData()
                }

                textMetaData.parseResult(resultData)
                addViewWithData(it, textMetaData)
                listener?.onChange(Pair(it, textMetaData))
            }
        }

        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int,
        ) {
        }

        override fun onTextChanged(
            s: CharSequence?,
            start: Int,
            before: Int,
            count: Int,
        ) {
        }
    }
}
