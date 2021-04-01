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

        startMainActivityAfterTimeSpan()

        //Uncomment if you want load translations and after that open Main Activity
        //startMainActivityAfterLoadingTranslations()
    }

    private fun startMainActivityAfterTimeSpan() {
        Handler(Looper.myLooper()!!).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 500)
    }

    private fun startMainActivityAfterLoadingTranslations() {
        Crowdin.forceUpdate(this) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}