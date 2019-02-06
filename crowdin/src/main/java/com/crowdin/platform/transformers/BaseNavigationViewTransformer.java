package com.crowdin.platform.transformers;

import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.View;

import com.crowdin.platform.utils.TextUtils;

/**
 * A transformer which transforms navigation views: it transforms the texts coming from the menu.
 */
public abstract class BaseNavigationViewTransformer implements ViewTransformerManager.Transformer {

    private static final String TAG = BaseNavigationViewTransformer.class.getSimpleName();

    protected abstract Menu getMenu(View view);

    @Override
    public View transform(View view, AttributeSet attrs) {
        if (view == null || !getViewType().isInstance(view)) {
            return view;
        }

        for (int index = 0; index < attrs.getAttributeCount(); index++) {
            String attributeName = attrs.getAttributeName(index);
            switch (attributeName) {
                case Attributes.ATTRIBUTE_APP_MENU:
                case Attributes.ATTRIBUTE_MENU:
                    updateText(view, attrs, index);
                    break;

                default:
                    break;
            }
        }

        return view;
    }

    private void updateText(View view, AttributeSet attrs, int index) {
        Resources resources = view.getContext().getResources();
        String value = attrs.getAttributeValue(index);
        if (value == null || !value.startsWith("@")) return;

        int resId = attrs.getAttributeResourceValue(index, 0);
        Menu menu = getMenu(view);
        TextUtils.updateMenuItemsText(menu, resources, resId);
    }
}
