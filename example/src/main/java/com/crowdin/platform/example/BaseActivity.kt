package com.crowdin.platform.example

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.crowdin.platform.Crowdin

/**
 * We should wrap the base context of our activities, which is better to put it
 * on BaseActivity, so that we don't have to repeat it for all activities one-by-one.
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(Crowdin.wrapContext(newBase))
    }
}
