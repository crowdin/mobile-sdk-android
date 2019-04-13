package com.crowdin.platform.transformers

import android.content.Context
import android.widget.Switch
import android.widget.TextView
import android.widget.ToggleButton
import com.crowdin.platform.repository.TextMetaData
import java.util.*

internal abstract class BaseTransformer : Transformer {

    val createdViews = WeakHashMap<TextView, TextMetaData>()

    override fun invalidate() {
        for (createdView in createdViews) {
            val view = createdView.key
            val textMetaData = createdView.value

            if (textMetaData.textAttributeKey != Transformer.UNKNOWN_ID) {
                val id = getIdentifier(view.context, textMetaData.textAttributeKey)
                view.text = view.context.resources.getText(id)
            }

            if (textMetaData.hintAttributeKey.isNotEmpty()) {
                val id = getIdentifier(view.context, textMetaData.hintAttributeKey)
                view.hint = view.context.resources.getText(id)
            }

            if (textMetaData.textOnAttributeKey.isNotEmpty()) {
                val id = getIdentifier(view.context, textMetaData.textOnAttributeKey)
                when (view) {
                    is Switch -> view.textOn = view.context.resources.getText(id)
                    is ToggleButton -> view.textOn = view.context.resources.getText(id)
                }
            }

            if (textMetaData.textOffAttributeKey.isNotEmpty()) {
                val id = getIdentifier(view.context, textMetaData.textOffAttributeKey)
                when (view) {
                    is Switch -> view.textOff = view.context.resources.getText(id)
                    is ToggleButton -> view.textOff = view.context.resources.getText(id)
                }
            }
        }
    }

    private fun getIdentifier(context: Context, value: String): Int =
            context.resources.getIdentifier(value, "string", context.packageName)
}