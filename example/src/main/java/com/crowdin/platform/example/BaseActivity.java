package com.crowdin.platform.example;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.crowdin.platform.Crowdin;

/**
 * We should wrap the base context of our activities, which is better to put it
 * on BaseActivity, so that we don't have to repeat it for all activities one-by-one.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Crowdin.wrapContext(newBase));
    }
}
