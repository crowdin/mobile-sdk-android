package com.crowdin.platform.transformers;

import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ToggleButton;

import com.crowdin.platform.utils.TextUtils;

/**
 * A transformer which transforms ToggleButton: it transforms the text, hint, textOn, textOff attributes.
 */
public class ToggleButtonTransformer implements ViewTransformerManager.Transformer {

    @Override
    public Class<? extends View> getViewType() {
        return ToggleButton.class;
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
                case Constants.ATTRIBUTE_ANDROID_TEXT:
                case Constants.ATTRIBUTE_TEXT:
                    String text = TextUtils.getTextForAttribute(attrs, index, view, resources);
                    if (text != null) {
                        ((ToggleButton) view).setText(text);
                    }
                    break;

                case Constants.ATTRIBUTE_ANDROID_HINT:
                case Constants.ATTRIBUTE_HINT:
                    String hint = TextUtils.getTextForAttribute(attrs, index, view, resources);
                    if (hint != null) {
                        ((ToggleButton) view).setHint(hint);
                    }
                    break;
                case Constants.ATTRIBUTE_TEXT_ON:
                case Constants.ATTRIBUTE_ANDROID_TEXT_ON:
                    String textOn = TextUtils.getTextForAttribute(attrs, index, view, resources);
                    if (textOn != null) {
                        ((ToggleButton) view).setTextOn(textOn);
                    }
                    break;

                case Constants.ATTRIBUTE_TEXT_OFF:
                case Constants.ATTRIBUTE_ANDROID_TEXT_OFF:
                    String textOff = TextUtils.getTextForAttribute(attrs, index, view, resources);
                    if (textOff != null) {
                        ((ToggleButton) view).setTextOff(textOff);
                    }
                    break;

                default:
                    break;
            }
        }
        return view;
    }
}
