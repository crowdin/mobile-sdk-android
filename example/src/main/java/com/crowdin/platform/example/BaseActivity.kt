package com.crowdin.platform.example

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//      Init Crowdin SDK overlay controls
        initCrowdinControl(this)
//      Java
//      CrowdinControlUtil.initCrowdinControl(this);
    }

    override fun onDestroy() {
        super.onDestroy()
//      Destroy crowdin overlay view.
        destroyCrowdinControl(this)
//      Java
//      CrowdinControlUtil.destroyCrowdinControl(this);
    }
}
