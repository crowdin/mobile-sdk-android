package com.crowdin.platform

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.crowdin.platform.transformer.ViewTransformerManager

/**
 * Crowdin custom layout inflater. it puts hook on view creation, and tries to apply some transformations
 * to the newly created views.
 *
 * Transformations can consist of transforming the texts applied on XML layout resources, so that it checks if
 * the string attribute set as a string resource it transforms the text and apply it to the view again.
 */
internal class CrowdinLayoutInflater constructor(original: LayoutInflater,
                                                 newContext: Context,
                                                 private val viewTransformerManager: ViewTransformerManager)
    : LayoutInflater(original, newContext) {

    companion object {
        private val sClassPrefixList = arrayOf("android.widget.", "android.webkit.", "android.app.")
    }

    override fun cloneInContext(newContext: Context): LayoutInflater {
        return CrowdinLayoutInflater(this, newContext, viewTransformerManager)
    }

    override fun setFactory(factory: Factory?) {
        if (factory is WrapperFactory) {
            super.setFactory(factory)
        } else {
            super.setFactory(WrapperFactory(factory))
        }
    }

    override fun setFactory2(factory2: Factory2?) {
        if (factory2 is PrivateWrapperFactory2) {
            super.setFactory2(factory2)
        } else {
            super.setFactory2(PrivateWrapperFactory2(factory2))
        }
    }

    @Throws(ClassNotFoundException::class)
    override fun onCreateView(name: String, attrs: AttributeSet): View? {
        for (prefix in sClassPrefixList) {
            try {
                val view = createView(name, prefix, attrs)
                if (view != null) {
                    return applyChange(view, attrs)
                }
            } catch (e: ClassNotFoundException) {
                // In this case we want to let the base class take a crack at it.
            }
        }
        return super.onCreateView(name, attrs)
    }

    private fun applyChange(view: View?, attrs: AttributeSet): View? {
        return viewTransformerManager.transform(view, attrs)
    }

    private inner class WrapperFactory(val factory: Factory?) : Factory {

        override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
            val view = factory?.onCreateView(name, context, attrs)
            return applyChange(view, attrs)
        }
    }

    private fun createCustomViewInternal(view: View?, name: String, attrs: AttributeSet): View? {
        @Suppress("NAME_SHADOWING")
        var view = view
        // If CustomViewCreation is off skip this.
        if (view == null && name.indexOf('.') > -1) {
            try {
                view = createView(name, null, attrs)
            } catch (ignored: ClassNotFoundException) {
            }
        }
        return view
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private inner class PrivateWrapperFactory2(
            val factory2: Factory2?) : Factory2 {

        override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
            var view = factory2?.onCreateView(parent, name, context, attrs)
            view = createCustomViewInternal(view, name, attrs)
            return applyChange(view, attrs)
        }

        override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
            var view = factory2?.onCreateView(name, context, attrs)
            view = createCustomViewInternal(view, name, attrs)
            return applyChange(view, attrs)
        }
    }
}
