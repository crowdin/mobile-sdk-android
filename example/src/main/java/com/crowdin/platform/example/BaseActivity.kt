package com.crowdin.platform.example

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.BaseContextWrappingDelegate
import com.crowdin.crowdin_controls.destroyCrowdinControl
import com.crowdin.crowdin_controls.initCrowdinControl
import com.crowdin.platform.example.utils.updateLocale

abstract class BaseActivity : AppCompatActivity() {

    /**
     * We should wrap the base context of our activities, which is better to put it
     * on BaseActivity, so that we don't have to repeat it for all activities one-by-one.
     *
     * @see BaseContextWrappingDelegate.attachBaseContext2
     */
    override fun getDelegate() = BaseContextWrappingDelegate(super.getDelegate())

    /**
     * Should be overridden in case you want to change locale programmatically.
     * Update configuration with your locale. Should be done for all activities to use your
     * `values-localeCode` resources properly.
     */
    override fun attachBaseContext(newBase: Context) {
        val newLocaleCode =
            (newBase.applicationContext as App).languagePreferences.getLanguageCode()
        val newContext = newBase.updateLocale(newLocaleCode)

        super.attachBaseContext(newContext)
    }

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
