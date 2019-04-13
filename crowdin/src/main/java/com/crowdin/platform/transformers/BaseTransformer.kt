package com.crowdin.platform.transformers

import android.content.Context
import android.widget.Switch
import android.widget.TextView
import android.widget.ToggleButton
import com.crowdin.platform.repository.TextMetaData
import java.util.*

internal abstract class BaseTransformer : Transformer {

    companion object {
        const val UNKNOWN_ID = 0
    }

    val createdViews = WeakHashMap<TextView, TextMetaData>()

    override fun invalidate() {
        for (createdView in createdViews) {
            val view = createdView.key
            val textMetaData = createdView.value

            if (textMetaData.isArrayItem) {
                val id = getArrayIdentifier(view.context, textMetaData.arrayName!!)
                if (id != UNKNOWN_ID) {
                    view.text = view.context.resources.getStringArray(id)[textMetaData.arrayIndex]
                }
            }

            if (textMetaData.textAttributeKey.isNotEmpty()) {
                val id = getStringIdentifier(view.context, textMetaData.textAttributeKey)
                if (id != UNKNOWN_ID) {
                    view.text = view.context.resources.getText(id)
                }
            }

            if (textMetaData.hintAttributeKey.isNotEmpty()) {
                val id = getStringIdentifier(view.context, textMetaData.hintAttributeKey)
                if (id != UNKNOWN_ID) {
                    view.hint = view.context.resources.getText(id)
                }
            }

            if (textMetaData.textOnAttributeKey.isNotEmpty()) {
                val id = getStringIdentifier(view.context, textMetaData.textOnAttributeKey)
                if (id != UNKNOWN_ID) {
                    when (view) {
                        is Switch -> view.textOn = view.context.resources.getText(id)
                        is ToggleButton -> view.textOn = view.context.resources.getText(id)
                    }
                }
            }

            if (textMetaData.textOffAttributeKey.isNotEmpty()) {
                val id = getStringIdentifier(view.context, textMetaData.textOffAttributeKey)
                if (id != UNKNOWN_ID) {
                    when (view) {
                        is Switch -> view.textOff = view.context.resources.getText(id)
                        is ToggleButton -> view.textOff = view.context.resources.getText(id)
                    }
                }
            }
        }
    }

    private fun getStringIdentifier(context: Context, value: String): Int =
            context.resources.getIdentifier(value, "string", context.packageName)

    private fun getArrayIdentifier(context: Context, value: String): Int =
            context.resources.getIdentifier(value, "array", context.packageName)

}