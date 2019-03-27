package com.crowdin.platform;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.crowdin.platform.transformers.ViewTransformerManager;
import com.crowdin.platform.utils.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * Crowdin custom layout inflater. it puts hook on view creation, and tries to apply some transformations
 * to the newly created views.
 * <p>
 * Transformations can consist of transforming the texts applied on XML layout resources, so that it checks if
 * the string attribute set as a string resource it transforms the text and apply it to the view again.
 */
public class CrowdinLayoutInflater extends LayoutInflater {

    private Field mConstructorArgs = null;
    private final ViewTransformerManager viewTransformerManager;

    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "android.app."
    };

    CrowdinLayoutInflater(LayoutInflater original,
                          Context newContext,
                          ViewTransformerManager viewTransformerManager) {
        super(original, newContext);
        this.viewTransformerManager = viewTransformerManager;
    }

    @Override
    public LayoutInflater cloneInContext(Context newContext) {
        return new CrowdinLayoutInflater(this, newContext, viewTransformerManager);
    }

    @Override
    public void setFactory(Factory factory) {
        if (!(factory instanceof WrapperFactory)) {
            super.setFactory(new WrapperFactory(factory));
        } else {
            super.setFactory(factory);
        }
    }

    @Override
    public void setFactory2(Factory2 factory2) {
        if (!(factory2 instanceof PrivateWrapperFactory2)) {
            super.setFactory2(new PrivateWrapperFactory2(factory2));
        } else {
            super.setFactory2(factory2);
        }
    }

    @Override
    protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
        for (String prefix : sClassPrefixList) {
            try {
                View view = createView(name, prefix, attrs);
                if (view != null) {
                    return applyChange(view, attrs);
                }
            } catch (ClassNotFoundException e) {
                // In this case we want to let the base class take a crack at it.
            }
        }

        return super.onCreateView(name, attrs);
    }

    private View applyChange(View view, AttributeSet attrs) {
        return viewTransformerManager.transform(view, attrs);
    }

    private class WrapperFactory implements Factory {

        private final Factory factory;

        WrapperFactory(Factory factory) {
            this.factory = factory;
        }

        @Override
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            View view = factory.onCreateView(name, context, attrs);
            return applyChange(view, attrs);
        }
    }

    private View createCustomViewInternal(View view, String name, Context viewContext, AttributeSet attrs) {
        // I by no means advise anyone to do this normally, but Google have locked down access to
        // the createView() method, so we never get a callback with attributes at the end of the
        // createViewFromTag chain (which would solve all this unnecessary rubbish).
        // We at the very least try to optimise this as much as possible.
        // We only call for customViews (As they are the ones that never go through onCreateView(...)).
        // We also maintain the Field reference and make it accessible which will make a pretty
        // significant difference to performance on Android 4.0+.

        // If CustomViewCreation is off skip this.
        if (view == null && name.indexOf('.') > -1) {
            if (mConstructorArgs == null)
                mConstructorArgs = ReflectionUtils.getField(LayoutInflater.class, "mConstructorArgs");

            final Object[] mConstructorArgsArr = (Object[]) ReflectionUtils.getValue(mConstructorArgs, this);
            final Object lastContext = mConstructorArgsArr[0];
            // The LayoutInflater actually finds out the correct context to use. We just need to set
            // it on the mConstructor for the internal method.
            // Set the constructor ars up for the createView, not sure why we can't pass these in.
            mConstructorArgsArr[0] = viewContext;
            ReflectionUtils.setValue(mConstructorArgs, this, mConstructorArgsArr);
            try {
                view = createView(name, null, attrs);
            } catch (ClassNotFoundException ignored) {
            } finally {
                mConstructorArgsArr[0] = lastContext;
                ReflectionUtils.setValue(mConstructorArgs, this, mConstructorArgsArr);
            }
        }
        return view;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private class PrivateWrapperFactory2 implements Factory2 {

        private final Factory2 factory2;

        PrivateWrapperFactory2(Factory2 factory2) {
            this.factory2 = factory2;
        }

        @Override
        public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
            View view = factory2.onCreateView(parent, name, context, attrs);
            view = createCustomViewInternal(view, name, context, attrs);
            return applyChange(view, attrs);
        }

        @Override
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            View view = factory2.onCreateView(name, context, attrs);
            view = createCustomViewInternal(view, name, context, attrs);
            return applyChange(view, attrs);
        }
    }
}
