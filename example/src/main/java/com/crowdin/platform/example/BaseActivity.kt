package com.crowdin.platform.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.BaseContextWrappingDelegate
import com.crowdin.crowdin_controls.OverlayActivityContract
import com.crowdin.crowdin_controls.destroyCrowdinControl
import com.crowdin.crowdin_controls.initCrowdinControl
import com.crowdin.platform.Crowdin

abstract class BaseActivity : AppCompatActivity() {

    var overlayPermissionActivityLauncher = registerForActivityResult(OverlayActivityContract()) { }

    /**
     * We should wrap the base context of our activities, which is better to put it
     * on BaseActivity, so that we don't have to repeat it for all activities one-by-one.
     *
     * @see BaseContextWrappingDelegate.attachBaseContext2
     */
    override fun getDelegate() = BaseContextWrappingDelegate(super.getDelegate())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeSavedLocale(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        // Init Crowdin SDK overlay controls
        initCrowdinControl(this, overlayPermissionActivityLauncher)
    }

    override fun onPause() {
        super.onPause()
        // Destroy crowdin overlay view.
        destroyCrowdinControl(this)
    }

    private fun initializeSavedLocale(savedInstanceState: Bundle?) {
        val app = application as App
        val isRecreatedForLocaleChange = savedInstanceState != null && app.languagePreferences.getLocaleChangeFlag()

        if (isRecreatedForLocaleChange) {
            // Clear the flag and force update
            app.languagePreferences.setLocaleChangeFlag(false)
            Crowdin.forceUpdate(this)
        }
    }
}

