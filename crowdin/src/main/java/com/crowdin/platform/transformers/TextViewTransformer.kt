package com.crowdin.platform.transformers

import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.Switch
import android.widget.TextView
import android.widget.ToggleButton
import com.crowdin.platform.repository.TextIdProvider
import com.crowdin.platform.repository.local.TextMetaData
import com.crowdin.platform.utils.FeatureFlags
import com.crowdin.platform.utils.TextUtils
import java.lang.ref.WeakReference

/**
 * A transformer which transforms TextView(or any view extends it like Button, EditText, ...):
 * it transforms "text" & "hint" attributes.
 */
internal class TextViewTransformer(val textIdProvider: TextIdProvider) : BaseTransformer() {

    override val viewType = TextView::class.java

    override fun transform(view: View, attrs: AttributeSet): View {
        if (!viewType.isInstance(view)) {
            return view
        }
        view as TextView
        var isTextView = false
        val textMetaData = TextMetaData()
        textMetaData.textAttributeKey = Transformer.UNKNOWN_ID

        val resources = view.context.resources
        for (index in 0 until attrs.attributeCount) {
            val id = TextUtils.getTextAttributeKey(resources, attrs, index)

            when (attrs.getAttributeName(index)) {
                Attributes.ATTRIBUTE_ANDROID_TEXT, Attributes.ATTRIBUTE_TEXT -> {
                    val text = TextUtils.getTextForAttribute(attrs, index, resources)
                    if (text != null) {
                        view.text = text
                        if (FeatureFlags.isRealTimeUpdateEnabled) {
                            if (id != null) {
                                textMetaData.textAttributeKey = id
                                isTextView = true
                            }
                        }
                    }
                }
                Attributes.ATTRIBUTE_ANDROID_HINT, Attributes.ATTRIBUTE_HINT -> {
                    val hint = TextUtils.getTextForAttribute(attrs, index, resources)
                    if (hint != null) {
                        view.hint = hint
                        if (FeatureFlags.isRealTimeUpdateEnabled) {
                            if (id != null) {
                                textMetaData.hintAttributeKey = id
                                isTextView = true
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
                        if (FeatureFlags.isRealTimeUpdateEnabled) {
                            if (id != null) {
                                textMetaData.textOnAttributeKey = id
                                isTextView = true
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
                        if (FeatureFlags.isRealTimeUpdateEnabled) {
                            if (id != null) {
                                textMetaData.textOffAttributeKey = id
                                isTextView = true
                            }
                        }
                    }
                }
            }
        }

        if (FeatureFlags.isRealTimeUpdateEnabled && isTextView) {
            createdViews[view] = textMetaData
            view.addTextChangedListener(Watcher(WeakReference(view)))
        }

        return view
    }

    inner class Watcher(var view: WeakReference<TextView>) : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            view.get()?.let {
                val textKey = textIdProvider.provideTextKey(s.toString())
                if (textKey != null) {
                    val textMetaData = createdViews[it]
                    textMetaData?.textAttributeKey = textKey
                    createdViews[it] = textMetaData
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }
}
