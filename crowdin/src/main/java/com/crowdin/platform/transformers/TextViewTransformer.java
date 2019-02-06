package com.crowdin.platform.transformers;

import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.crowdin.platform.utils.TextUtils;

/**
 * A transformer which transforms TextView(or any view extends it like Button, EditText, ...):
 * it transforms "text" & "hint" attributes.
 */
public class TextViewTransformer implements ViewTransformerManager.Transformer {

    @Override
    public Class<? extends View> getViewType() {
        return TextView.class;
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
                case Attributes.ATTRIBUTE_ANDROID_TEXT:
                case Attributes.ATTRIBUTE_TEXT:
                    String text = TextUtils.getTextForAttribute(attrs, index, view, resources);
                    if (text != null) {
                        ((TextView) view).setText(text);
                    }
                    break;

                case Attributes.ATTRIBUTE_ANDROID_HINT:
                case Attributes.ATTRIBUTE_HINT:
                    String hint = TextUtils.getTextForAttribute(attrs, index, view, resources);
                    if (hint != null) {
                        ((TextView) view).setHint(hint);
                    }
                    break;

                default:
                    break;
            }
        }
        return view;
    }
}
