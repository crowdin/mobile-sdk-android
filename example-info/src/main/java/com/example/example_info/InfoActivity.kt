package com.example.example_info

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.CrowdinBaseContextWrappingDelegate

class InfoActivity : AppCompatActivity() {

    private var delegate: AppCompatDelegate? = null
    /**
     * We should wrap the base context of our activities, which is better to put it
     * on BaseActivity, so that we don't have to repeat it for all activities one-by-one.
     *
     * @see CrowdinBaseContextWrappingDelegate.attachBaseContext2
     */
    override fun getDelegate() = delegate
            ?: CrowdinBaseContextWrappingDelegate(super.getDelegate()).also {
                delegate = it
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
    }
}
