package com.example.example_info

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.crowdin.platform.Crowdin

class InfoActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        //  We should wrap the base context of our activities, which is better to put it
        //  on BaseActivity, so that we don't have to repeat it for all activities one-by-one.
        super.attachBaseContext(Crowdin.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
    }
}
