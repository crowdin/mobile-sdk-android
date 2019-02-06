package com.crowdin.platform.transformers;

import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Switch;

import com.crowdin.platform.utils.TextUtils;

/**
 * A transformer which transforms Switch: it transforms the text, hint, textOn, textOff attributes.
 */
public class SwitchTransformer implements ViewTransformerManager.Transformer {

    @Override
    public Class<? extends View> getViewType() {
        return Switch.class;
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
                        ((Switch) view).setText(text);
                    }
                    break;

                case Attributes.ATTRIBUTE_ANDROID_HINT:
                case Attributes.ATTRIBUTE_HINT:
                    String hint = TextUtils.getTextForAttribute(attrs, index, view, resources);
                    if (hint != null) {
                        ((Switch) view).setHint(hint);
                    }
                    break;
                case Attributes.ATTRIBUTE_TEXT_ON:
                case Attributes.ATTRIBUTE_ANDROID_TEXT_ON:
                    String textOn = TextUtils.getTextForAttribute(attrs, index, view, resources);
                    if (textOn != null) {
                        ((Switch) view).setTextOn(textOn);
                    }
                    break;

                case Attributes.ATTRIBUTE_TEXT_OFF:
                case Attributes.ATTRIBUTE_ANDROID_TEXT_OFF:
                    String textOff = TextUtils.getTextForAttribute(attrs, index, view, resources);
                    if (textOff != null) {
                        ((Switch) view).setTextOff(textOff);
                    }
                    break;

                default:
                    break;
            }
        }
        return view;
    }
}
