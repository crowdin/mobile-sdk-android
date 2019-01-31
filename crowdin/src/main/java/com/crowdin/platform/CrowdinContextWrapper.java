package com.crowdin.platform;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;

import com.crowdin.platform.repository.StringRepository;
import com.crowdin.platform.transformers.ViewTransformerManager;

/**
 * Main Crowdin context wrapper which wraps the context for providing another layout inflater & resources.
 */
class CrowdinContextWrapper extends ContextWrapper {

    private CrowdinLayoutInflater layoutInflater;
    private ViewTransformerManager viewTransformerManager;

    public static CrowdinContextWrapper wrap(Context context,
                                             StringRepository stringRepository,
                                             ViewTransformerManager viewTransformerManager) {
        return new CrowdinContextWrapper(context, stringRepository, viewTransformerManager);
    }

    private CrowdinContextWrapper(Context base,
                                  StringRepository stringRepository,
                                  ViewTransformerManager viewTransformerManager) {
        super(new CustomResourcesContextWrapper(base, new CrowdinResources(base.getResources(), stringRepository)));
        this.viewTransformerManager = viewTransformerManager;
    }

    @Override
    public Object getSystemService(String name) {
        if (LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (layoutInflater == null) {
                layoutInflater = new CrowdinLayoutInflater(LayoutInflater.from(getBaseContext()),
                        this, viewTransformerManager, true);
            }
            return layoutInflater;
        }

        return super.getSystemService(name);
    }
}