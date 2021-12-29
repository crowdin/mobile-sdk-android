package com.crowdin.platform.example

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.crowdin.platform.Crowdin

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //Comment if you want open Main Activity after time span
//        startMainActivityAfterTimeSpan()

        //Uncomment if you want load translations and after that open Main Activity
        startMainActivityAfterLoadingTranslations()
    }

    private fun startMainActivityAfterTimeSpan() {
        Handler(Looper.myLooper()!!).postDelayed({
            startMainActivity()
        }, 500)
    }

    private fun startMainActivityAfterLoadingTranslations() {
        Crowdin.forceUpdate(this) {
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}