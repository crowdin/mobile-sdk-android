package com.crowdin.platform.transformers

import android.widget.TextView
import com.crowdin.platform.repository.local.TextMetaData
import java.util.*

internal abstract class BaseTransformer : Transformer {

    val createdViews = WeakHashMap<TextView, TextMetaData>()

    override fun invalidate() {
        for (createdView in createdViews) {
            if (createdView.value.textAttributeKey != Transformer.UNKNOWN_ID) {
                val view = createdView.key
                val id = view.context.resources.getIdentifier(createdView.value.textAttributeKey, "string",
                        view.context.packageName)
                view.text = view.context.resources.getText(id)
            }
        }
    }
}