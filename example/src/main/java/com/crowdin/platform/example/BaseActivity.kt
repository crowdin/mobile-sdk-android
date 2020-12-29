package com.crowdin.platform.example

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.BaseContextWrappingDelegate
import com.crowdin.crowdin_controls.destroyCrowdinControl
import com.crowdin.crowdin_controls.initCrowdinControl

abstract class BaseActivity : AppCompatActivity() {

    /**
     * We should wrap the base context of our activities, which is better to put it
     * on BaseActivity, so that we don't have to repeat it for all activities one-by-one.
     *
     * @see BaseContextWrappingDelegate.attachBaseContext2
     */
    override fun getDelegate() = BaseContextWrappingDelegate(super.getDelegate())

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        com.crowdin.crowdin_controls.onActivityResult(this, requestCode)
    }

    override fun onResume() {
        super.onResume()
        // Init Crowdin SDK overlay controls
        initCrowdinControl(this)
    }

    override fun onPause() {
        super.onPause()
        // Destroy crowdin overlay view.
        destroyCrowdinControl(this)
    }
}
