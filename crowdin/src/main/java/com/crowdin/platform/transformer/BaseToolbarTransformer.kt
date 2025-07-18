package com.crowdin.platform.transformer

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.crowdin.platform.data.TextMetaDataProvider
import com.crowdin.platform.data.model.TextMetaData
import com.crowdin.platform.util.getFormattedCode
import com.crowdin.platform.util.getLocale

internal abstract class BaseToolbarTransformer(
    val textMetaDataProvider: TextMetaDataProvider,
) : BaseTransformer() {
    fun findChildView(parent: ViewGroup): TextView? {
        var textView: TextView? = null
        for (index in 0 until parent.childCount) {
            val child = parent.getChildAt(index)
            if (child is TextView) {
                textView = child
            }
        }

        return textView
    }

    fun addHierarchyChangeListener(view: ViewGroup) {
        view.setOnHierarchyChangeListener(
            object : ViewGroup.OnHierarchyChangeListener {
                override fun onChildViewAdded(
                    parent: View?,
                    child: View?,
                ) {
                    if (child is TextView) {
                        addTextWatcherToChild(child)
                    }
                }

                override fun onChildViewRemoved(
                    parent: View?,
                    child: View?,
                ) {
                    if (child is TextView) {
                        removeTextViewWithData(child)
                    }
                }
            },
        )
    }

    fun addTextWatcherToChild(textView: TextView?) {
        textView?.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val localeCode =
                        textView.resources.configuration
                            .getLocale()
                            .getFormattedCode()
                    val resultData = textMetaDataProvider.provideTextMetaData(localeCode, s.toString())
                    var textMetaData = getViewTextMetaData(textView)
                    if (textMetaData == null) {
                        textMetaData = TextMetaData()
                    }

                    textMetaData.parseResult(resultData)

                    addViewWithData(textView, textMetaData)
                    listener?.onChange(Pair(textView, textMetaData))
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {
                }
            },
        )
    }
}
