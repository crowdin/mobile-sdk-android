package com.crowdin.platform.transformers;

import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * A transformer which transforms Spinner: it transforms the entries attribute
 * and uses default android.layouts for displaying
 */
public class SpinnerTransformer implements ViewTransformerManager.Transformer {

    @Override
    public Class<? extends View> getViewType() {
        return Spinner.class;
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
                case Constants.ATTRIBUTE_ENTRIES:
                case Constants.ATTRIBUTE_ANDROID_ENTRIES:
                    String value = attrs.getAttributeValue(index);
                    if (value != null && value.startsWith("@")) {
                        String[] stringArray = resources.getStringArray(attrs.getAttributeResourceValue(index, 0));
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(),
                                android.R.layout.simple_spinner_item,
                                stringArray);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        ((Spinner) view).setAdapter(adapter);
                    }
                    break;

                default:
                    break;
            }
        }
        return view;
    }
}
