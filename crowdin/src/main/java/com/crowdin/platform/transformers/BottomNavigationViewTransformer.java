package com.crowdin.platform.transformers;

import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.View;

/**
 * A transformer which transforms BottomNavigationView: it transforms the texts coming from the menu.
 */
public class BottomNavigationViewTransformer extends BaseNavigationViewTransformer {

    @Override
    public Class<? extends View> getViewType() {
        return BottomNavigationView.class;
    }

    @Override
    protected Menu getMenu(View view) {
        return ((BottomNavigationView) view).getMenu();
    }
}
