package com.crowdin.platform.transformers

import android.widget.TextView
import java.util.*

internal abstract class BaseTransformer : Transformer {

    val createdViews = WeakHashMap<TextView, String>()

    override fun invalidate() {
        for (createdView in createdViews) {
            if (createdView.value != Transformer.UNKNOWN_ID) {
                val view = createdView.key
                val id = view.context.resources.getIdentifier(createdView.value, "string",
                        view.context.packageName)
                view.text = view.context.resources.getText(id)
            }
        }
    }
}