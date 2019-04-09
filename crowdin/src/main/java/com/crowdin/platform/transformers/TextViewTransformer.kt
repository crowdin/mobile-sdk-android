package com.crowdin.platform.transformers

import android.content.Context
import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Switch
import android.widget.TextView
import android.widget.ToggleButton
import com.crowdin.platform.repository.TextIdProvider
import com.crowdin.platform.utils.TextUtils
import java.util.*

/**
 * A transformer which transforms TextView(or any view extends it like Button, EditText, ...):
 * it transforms "text" & "hint" attributes.
 */
internal class TextViewTransformer(val context: Context, val textIdProvider: TextIdProvider) :
        ViewTransformerManager.Transformer {

    private val createdViews = WeakHashMap<TextView, String>()

    companion object {
        private const val UNKNOWN_ID = "-1"
    }

    override val viewType: Class<out View>
        get() = TextView::class.java

    override fun transform(view: View, attrs: AttributeSet): View {
        if (!viewType.isInstance(view)) {
            return view
        }

        createdViews[(view as TextView)] = UNKNOWN_ID
        view.addTextChangedListener(Watcher(view))

        val resources = view.context.resources
        for (index in 0 until attrs.attributeCount) {
            val attributeName = attrs.getAttributeName(index)
            when (attributeName) {
                Attributes.ATTRIBUTE_ANDROID_TEXT, Attributes.ATTRIBUTE_TEXT -> {
                    val text = TextUtils.getTextForAttribute(attrs, index, resources)
                    if (text != null) {
                        view.text = text
                        val id = TextUtils.getTextAttributeKey(resources, attrs, index)
                        if (id != null) {
                            createdViews[view] = id
                            Log.d("TAG", "TextView added: $id")
                        }
                    }
                }
                Attributes.ATTRIBUTE_ANDROID_HINT, Attributes.ATTRIBUTE_HINT -> {
                    val hint = TextUtils.getTextForAttribute(attrs, index, resources)
                    if (hint != null) {
                        view.hint = hint
                    }
                }
                Attributes.ATTRIBUTE_TEXT_ON, Attributes.ATTRIBUTE_ANDROID_TEXT_ON -> {
                    val textOn = TextUtils.getTextForAttribute(attrs, index, resources)
                    if (textOn != null) {
                        when (view) {
                            is Switch -> view.textOn = textOn
                            is ToggleButton -> view.textOn = textOn
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
                    }
                }
            }
        }

        return view
    }

    override fun invalidate() {
        for (createdView in createdViews) {
            if (createdView.value != UNKNOWN_ID) {
                val view = createdView.key
                val id = view.context.resources.getIdentifier(createdView.value, "string", context.packageName)
                Log.d("TAG", "invalidate: ${createdView.value}, id: $id")
                view.text = view.context.resources.getText(id)
                Log.d("TAG", "invalidate: ${view.context.resources.getText(id)}")
            }
        }
    }

    inner class Watcher(var view: TextView) : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            Log.d("TAG", "afterTextChanged ${s.toString()}")

            val textKey = textIdProvider.provideTextKey(s.toString())
            if (textKey != null) {
                createdViews[view] = textKey
                Log.d("TAG", "afterTextChanged: add to view: $textKey")
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }
}
