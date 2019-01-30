package com.crowdin.platform.transformers;

import android.support.design.widget.NavigationView;
import android.view.Menu;
import android.view.View;

public class NavigationViewTransformer extends BaseNavigationViewTransformer {

    @Override
    public Class<? extends View> getViewType() {
        return NavigationView.class;
    }

    @Override
    protected Menu getMenu(View view) {
        return ((NavigationView) view).getMenu();
    }
}
