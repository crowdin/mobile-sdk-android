package com.crowdin.platform.transformers

import android.annotation.TargetApi
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toolbar
import com.crowdin.platform.utils.TextUtils
import java.util.*

/**
 * A transformer which transforms Toolbar: it transforms the text set as title.
 */
internal class ToolbarTransformer : Transformer {

    private val createdViews = WeakHashMap<Toolbar, String>()

    override val viewType: Class<out View>
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        get() = Toolbar::class.java

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
                ATTRIBUTE_ANDROID_TITLE, ATTRIBUTE_TITLE -> {
                    val title = TextUtils.getTextForAttribute(attrs, index, resources)
                    if (title != null) {
                        view.title = title
                        createdViews[view] = title.toString()
                    }
                }
            }
        }
        return view
    }

//    override fun invalidate() {
//        for (createdView in createdViews) {
//            if (createdView.value != Transformer.UNKNOWN_ID) {
//                val view = createdView.key
//                val id = view.context.resources.getIdentifier(createdView.value, "string",
//                        view.context.packageName)
//                Log.d("TAG", "Toolbar invalidate: ${createdView.value}, id: $id")
//                view.title = view.context.resources.getText(id)
//                Log.d("TAG", "Toolbar invalidate: ${view.context.resources.getText(id)}")
//            }
//        }
//    }

    internal companion object {

        private const val ATTRIBUTE_TITLE = "title"
        private const val ATTRIBUTE_ANDROID_TITLE = "android:title"
    }
}