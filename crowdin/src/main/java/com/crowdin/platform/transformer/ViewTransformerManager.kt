package com.crowdin.platform.transformer

import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.crowdin.platform.data.model.TextMetaData
import com.crowdin.platform.data.model.ViewData
import java.util.Collections
import java.util.WeakHashMap

/**
 * Manages all view transformers as a central point for layout inflater.
 * Layout inflater will ask this manager to transform the inflating views.
 */
internal class ViewTransformerManager {

    private val transformers = ArrayList<Pair<Class<out View>, Transformer>>()

    /**
     * Register a new view transformer to be applied on newly inflating views.
     *
     * @param transformer to be added to transformers list.
     */
    fun registerTransformer(transformer: Transformer) =
        transformers.add(Pair(transformer.viewType, transformer))

    /**
     * Transforms a view.
     * it tries to find proper transformers for view, and if exists, it will apply them on view,
     * and return the final result as a new view.
     *
     * @param view to be transformed.
     * @param attrs attributes of the view.
     * @return the transformed view.
     */
    fun transform(view: View?, attrs: AttributeSet): View? {
        view ?: return null
        var newView: View = view
        for (pair in transformers) {
            val type = pair.first
            if (!type.isInstance(view)) {
                continue
            }

            val transformer = pair.second
            newView = transformer.transform(newView, attrs)
        }

        return newView
    }

    fun invalidate() {
        transformers.forEach {
            it.second.invalidate()
        }
    }

    fun getViewData(): MutableList<ViewData> {
        val mutableList = mutableListOf<ViewData>()
        transformers.forEach {
            mutableList.addAll(it.second.getViewDataFromWindow())
        }

        return mutableList
    }

    fun getVisibleViewsWithData(): Map<TextView, TextMetaData> {
        val concurrentHashMap = Collections.synchronizedMap(WeakHashMap<TextView, TextMetaData>())
        transformers.forEach {
            concurrentHashMap.putAll(it.second.getVisibleViewsWithData())
        }

        return concurrentHashMap
    }

    fun setOnViewsChangeListener(listener: ViewsChangeListener?) {
        transformers.forEach {
            it.second.setOnViewsChangeListener(listener)
        }
    }
}

/**
 * A view transformer skeleton.
 */
internal interface Transformer {

    /**
     * The type of view this transformer is for.
     *
     * @return the type of view.
     */
    val viewType: Class<out View>

    /**
     * Apply transformation to a view.
     *
     * @param view to be transformed.
     * @param attrs attributes of the view.
     * @return the transformed view.
     */
    fun transform(view: View, attrs: AttributeSet): View

    /**
     * Update text attributes.
     */
    fun invalidate()

    /**
     * Collect data for visible views on a window.
     *
     * @return List<ViewData> data related to specific views visible on a window.
     * @see ViewData
     */
    fun getViewDataFromWindow(): MutableList<ViewData>

    /**
     * Collect visible on a window views and related to them data.
     *
     * @return ConcurrentHashMap<TextView, TextMetaData> map of views and text metadata.
     */
    fun getVisibleViewsWithData(): Map<TextView, TextMetaData>

    /**
     * Set view change listener.
     *
     * @see ViewsChangeListener
     */
    fun setOnViewsChangeListener(listener: ViewsChangeListener?)
}

/**
 * Allows to track when new view created or text has changed.
 */
internal interface ViewsChangeListener {

    /**
     * View created or text has changed.
     */
    fun onChange(pair: Pair<TextView, TextMetaData>) {}
}
