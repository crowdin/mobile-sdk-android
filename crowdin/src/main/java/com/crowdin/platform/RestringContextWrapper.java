package com.crowdin.platform;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;

import com.crowdin.platform.repository.StringRepository;
import com.crowdin.platform.transformers.ViewTransformerManager;

/**
 * Main Restring context wrapper which wraps the context for providing another layout inflater & resources.
 */
class RestringContextWrapper extends ContextWrapper {

    private RestringLayoutInflater layoutInflater;
    private ViewTransformerManager viewTransformerManager;

    public static RestringContextWrapper wrap(Context context,
                                              StringRepository stringRepository,
                                              ViewTransformerManager viewTransformerManager) {
        return new RestringContextWrapper(context, stringRepository, viewTransformerManager);
    }

    private RestringContextWrapper(Context base,
                                   StringRepository stringRepository,
                                   ViewTransformerManager viewTransformerManager) {
        super(new CustomResourcesContextWrapper(base, new RestringResources(base.getResources(), stringRepository)));
        this.viewTransformerManager = viewTransformerManager;
    }

    @Override
    public Object getSystemService(String name) {
        if (LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (layoutInflater == null) {
                layoutInflater = new RestringLayoutInflater(LayoutInflater.from(getBaseContext()),
                        this, viewTransformerManager, true);
            }
            return layoutInflater;
        }

        return super.getSystemService(name);
    }
}