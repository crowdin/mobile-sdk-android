package com.crowdin.platform.transformers

import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView
import com.crowdin.platform.utils.TextUtils
import java.util.*

/**
 * A transformer which transforms Toolbar(from support library): it transforms the text set as title.
 */
internal class SupportToolbarTransformer : Transformer {

    private val createdViews = WeakHashMap<Toolbar, String>()

    override val viewType: Class<out View>
        get() = Toolbar::class.java

    override fun transform(view: View, attrs: AttributeSet): View {
        if (!viewType.isInstance(view)) {
            return view
        }

        view as Toolbar
        createdViews[view] = Transformer.UNKNOWN_ID

        val resources = view.context.resources
        for (index in 0 until attrs.attributeCount) {
            val attributeName = attrs.getAttributeName(index)
            when (attributeName) {
                Attributes.ATTRIBUTE_APP_TITLE, Attributes.ATTRIBUTE_TITLE -> {
                    val title = TextUtils.getTextForAttribute(attrs, index, resources)
                    if (title != null) {
                        view.title = title
                        val id = TextUtils.getTextAttributeKey(resources, attrs, index)
                        if (id != null) {
                            createdViews[view] = id
                            Log.d("TAG", "Toolbar v7 added: $id")
                        }
                    }
                }
            }
        }

        // TODO: handle add TextView or dynamic text changes
        for (index in 0 until view.childCount) {
            val child = view.getChildAt(index)
            if (child is TextView) {
                child.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        Log.d("TAG", "child text change: ${s.toString()}")
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })
            }
        }
        return view
    }

    override fun invalidate() {
        for (createdView in createdViews) {
            if (createdView.value != Transformer.UNKNOWN_ID) {
                val view = createdView.key
                val id = view.context.resources.getIdentifier(createdView.value, "string",
                        view.context.packageName)
                Log.d("TAG", "ToolbarV7 invalidate: ${createdView.value}, id: $id")
                view.title = view.context.resources.getText(id)
                Log.d("TAG", "ToolbarV7 invalidate: ${view.context.resources.getText(id)}")
            }
        }
    }
}
