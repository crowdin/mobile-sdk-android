package com.crowdin.platform.transformers;

import android.content.res.Resources;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import com.crowdin.platform.utils.TextUtils;

/**
 * A transformer which transforms Toolbar(from support library): it transforms the text set as title.
 */
public class SupportToolbarTransformer implements ViewTransformerManager.Transformer {

    @Override
    public Class<? extends View> getViewType() {
        return Toolbar.class;
    }

    @Override
    public View transform(View view, AttributeSet attrs) {
        if (view == null || !getViewType().isInstance(view)) {
            return view;
        }

        Resources resources = view.getContext().getResources();
        for (int index = 0; index < attrs.getAttributeCount(); index++) {
            String attributeName = attrs.getAttributeName(index);
            switch (attributeName) {
                case Constants.ATTRIBUTE_APP_TITLE:
                case Constants.ATTRIBUTE_TITLE:
                    String title = TextUtils.getTextForAttribute(attrs, index, view, resources);
                    if (title != null) {
                        ((Toolbar) view).setTitle(title);
                    }
                    break;

                default:
                    break;
            }
        }
        return view;
    }
}
