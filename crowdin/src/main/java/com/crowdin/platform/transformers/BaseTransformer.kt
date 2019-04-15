package com.crowdin.platform.transformers

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.View
import android.widget.Switch
import android.widget.TextView
import android.widget.ToggleButton
import com.crowdin.platform.repository.model.TextMetaData
import java.util.*


internal abstract class BaseTransformer : Transformer {

    companion object {
        private const val UNKNOWN_ID = 0
        private const val TYPE_STRING = "string"
        private const val TYPE_ARRAYS = "array"
        private const val TYPE_PLURALS = "plurals"
    }

    val createdViews = WeakHashMap<TextView, TextMetaData>()

    override fun drawOnLocalizedUI() {
        for (createdView in createdViews) {
            val view = createdView.key
            if (view.visibility != View.VISIBLE) return

            Log.d("TAG", "TopStart:[${view.x}:${view.y}]" +
                    " BottomEnd:${view.x + view.width}: ${view.y + view.height}")

            val textMetaData = createdView.value

            if (textMetaData.hasAttributeKey
                    || textMetaData.isArrayItem
                    || textMetaData.isPluralData) {
                val shape = GradientDrawable()
                shape.shape = GradientDrawable.RECTANGLE
                shape.setStroke(2, Color.BLACK)
                view.background = shape
                when {
                    textMetaData.hasAttributeKey -> view.text = textMetaData.textAttributeKey
                    textMetaData.isArrayItem -> view.text = "${textMetaData.arrayName}, ${textMetaData.arrayIndex}"
                    textMetaData.isPluralData -> view.text = "${textMetaData.pluralName}, ${textMetaData.pluralQuantity}"
                }
            } else {
                view.text = "unknown"
            }
        }
    }

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
            val id = view.context.resources.getIdentifier(textMetaData.arrayName,
                    TYPE_ARRAYS, view.context.packageName)
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

            val id = view.context.resources.getIdentifier(pluralName, TYPE_PLURALS, view.context.packageName)
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
        if (textMetaData.hasAttributeKey) {
            val id = view.context.resources.getIdentifier(textMetaData.textAttributeKey,
                    TYPE_STRING, view.context.packageName)
            if (id != UNKNOWN_ID) {
                when {
                    textMetaData.stringDefault.isNotEmpty() -> view.text = view.context.resources.getText(id, textMetaData.stringDefault)
                    textMetaData.stringsFormatArgs.isNotEmpty() -> view.text = view.context.resources.getString(id, *textMetaData.stringsFormatArgs)
                    else -> view.text = view.context.resources.getText(id)
                }
            }
        }
    }

    private fun invalidateHint(view: TextView, textMetaData: TextMetaData) {
        if (textMetaData.hintAttributeKey.isNotEmpty()) {
            val id = view.context.resources.getIdentifier(textMetaData.hintAttributeKey,
                    TYPE_STRING, view.context.packageName)
            if (id != UNKNOWN_ID) {
                view.hint = view.context.resources.getText(id)
            }
        }
    }

    private fun invalidateTextOn(view: TextView, textMetaData: TextMetaData) {
        if (textMetaData.textOnAttributeKey.isNotEmpty()) {
            val id = view.context.resources.getIdentifier(textMetaData.textOnAttributeKey,
                    TYPE_STRING, view.context.packageName)
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
            val id = view.context.resources.getIdentifier(textMetaData.textOffAttributeKey,
                    TYPE_STRING, view.context.packageName)
            if (id != UNKNOWN_ID) {
                when (view) {
                    is Switch -> view.textOff = view.context.resources.getText(id)
                    is ToggleButton -> view.textOff = view.context.resources.getText(id)
                }
            }
        }
    }
}