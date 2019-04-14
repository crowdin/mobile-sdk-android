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

            invalidateArrayItem(view, textMetaData)
            invalidateQuantityText(view, textMetaData)
            invalidateSimpleText(view, textMetaData)
            invalidateHint(view, textMetaData)
            invalidateTextOn(view, textMetaData)
            invalidateTextOff(view, textMetaData)
        }
    }

    private fun invalidateArrayItem(view: TextView, textMetaData: TextMetaData) {
        if (textMetaData.isArrayItem) {
            val id = getArrayIdentifier(view.context, textMetaData.arrayName!!)
            if (id != UNKNOWN_ID) {
                view.text = view.context.resources.getStringArray(id)[textMetaData.arrayIndex]
            }
        }
    }

    private fun invalidateQuantityText(view: TextView, textMetaData: TextMetaData) {
        if (textMetaData.isPluralData) {
            val pluralName = textMetaData.pluralName
            val pluralQuantity = textMetaData.pluralQuantity
            val pluralFormatArgs = textMetaData.pluralFormatArgs

            val id = getPluralIdentifier(view.context, pluralName!!)
            if (id != UNKNOWN_ID) {
                when {
                    pluralFormatArgs.isNotEmpty() ->
                        view.text = view.context.resources.getQuantityString(id, pluralQuantity, *pluralFormatArgs)
                    else -> view.text = view.context.resources.getQuantityText(id, pluralQuantity)
                }
            }
        }
    }

    private fun invalidateSimpleText(view: TextView, textMetaData: TextMetaData) {
        if (textMetaData.textAttributeKey.isNotEmpty()) {
            val id = getStringIdentifier(view.context, textMetaData.textAttributeKey)
            if (id != UNKNOWN_ID) {
                view.text = view.context.resources.getText(id)
            }
        }
    }

    private fun invalidateHint(view: TextView, textMetaData: TextMetaData) {
        if (textMetaData.hintAttributeKey.isNotEmpty()) {
            val id = getStringIdentifier(view.context, textMetaData.hintAttributeKey)
            if (id != UNKNOWN_ID) {
                view.hint = view.context.resources.getText(id)
            }
        }
    }

    private fun invalidateTextOn(view: TextView, textMetaData: TextMetaData) {
        if (textMetaData.textOnAttributeKey.isNotEmpty()) {
            val id = getStringIdentifier(view.context, textMetaData.textOnAttributeKey)
            if (id != UNKNOWN_ID) {
                when (view) {
                    is Switch -> view.textOn = view.context.resources.getText(id)
                    is ToggleButton -> view.textOn = view.context.resources.getText(id)
                }
            }
        }
    }

    private fun invalidateTextOff(view: TextView, textMetaData: TextMetaData) {
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

    private fun getPluralIdentifier(context: Context, value: String): Int =
            context.resources.getIdentifier(value, "plurals", context.packageName)

    private fun getStringIdentifier(context: Context, value: String): Int =
            context.resources.getIdentifier(value, "string", context.packageName)

    private fun getArrayIdentifier(context: Context, value: String): Int =
            context.resources.getIdentifier(value, "array", context.packageName)

}