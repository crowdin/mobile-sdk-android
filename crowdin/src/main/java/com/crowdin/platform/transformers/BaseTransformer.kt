package com.crowdin.platform.transformers

import android.util.Log
import android.view.View
import android.widget.Switch
import android.widget.TextView
import android.widget.ToggleButton
import com.crowdin.platform.repository.model.TextMetaData
import com.crowdin.platform.repository.model.ViewData
import java.util.*


internal abstract class BaseTransformer : Transformer {

    companion object {
        private const val UNKNOWN_ID = 0
        private const val TYPE_STRING = "string"
        private const val TYPE_ARRAYS = "array"
        private const val TYPE_PLURALS = "plurals"
    }

    val createdViews = WeakHashMap<TextView, TextMetaData>()

    // TODO: remove
    override fun drawOnLocalizedUI() {
        for (createdView in createdViews) {
            val view = createdView.key
            if (view.visibility != View.VISIBLE) return

            val textMetaData = createdView.value

            val location = IntArray(2)
            view.getLocationInWindow(location)
            logCoordinates(view, location, textMetaData)
        }
    }

    // TODO: remove
    private fun logCoordinates(view: TextView, location: IntArray, textMetaData: TextMetaData) {
        if (location[0] >= view.rootView.x &&
                location[0] <= view.rootView.width &&
                location[1] >= view.rootView.y &&
                location[1] <= view.rootView.height) {

            Log.d("TAG", "Key:${textMetaData.textAttributeKey}, TopStart:[${location[0]}:${location[1]}]" +
                    " BottomEnd:${location[0] + view.width}: ${location[1] + view.height}")
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

    override fun getViewDataFromWindow(): List<ViewData>? {
        val listViewData = mutableListOf<ViewData>()
        for (createdView in createdViews) {
            val view = createdView.key
            if (view.visibility != View.VISIBLE) return null

            val textMetaData = createdView.value

            val location = IntArray(2)
            view.getLocationInWindow(location)

            if (location[0] >= view.rootView.x &&
                    location[0] <= view.rootView.width &&
                    location[1] >= view.rootView.y &&
                    location[1] <= view.rootView.height) {

                listViewData.add(ViewData(textMetaData.textAttributeKey,
                        location[0],
                        location[1],
                        location[0] + view.width,
                        location[1] + view.height))

                // TODO: remove
                Log.d("TAG", "Key:${textMetaData.textAttributeKey}, TopStart:[${location[0]}:${location[1]}]" +
                        " BottomEnd:${location[0] + view.width}: ${location[1] + view.height}")
            }
        }

        return listViewData
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