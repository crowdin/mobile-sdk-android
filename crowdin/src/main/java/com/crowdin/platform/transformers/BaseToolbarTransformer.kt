package com.crowdin.platform.transformers

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.crowdin.platform.repository.TextIdProvider
import com.crowdin.platform.repository.model.TextMetaData

internal abstract class BaseToolbarTransformer(val textIdProvider: TextIdProvider) : BaseTransformer() {

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
        view.setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {

            override fun onChildViewAdded(parent: View?, child: View?) {
                if (child is TextView) {
                    addTextWatcherToChild(child)
                }
            }

            override fun onChildViewRemoved(parent: View?, child: View?) {
                if (child is TextView) {
                    createdViews.remove(child)
                }
            }
        })
    }

    fun addTextWatcherToChild(textView: TextView?) {
        textView?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val resultData = textIdProvider.provideTextKey(s.toString())
                var textMetaData = createdViews[textView]
                if (textMetaData == null) {
                    textMetaData = TextMetaData()
                }
                textMetaData.parseResult(resultData)
                createdViews[textView] = textMetaData
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }
}