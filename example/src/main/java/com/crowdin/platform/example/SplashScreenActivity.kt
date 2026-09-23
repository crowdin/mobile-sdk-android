package com.crowdin.platform.example

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.BaseContextWrappingDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.crowdin.platform.Crowdin
import com.crowdin.platform.example.ui.localizedString
import com.crowdin.platform.example.ui.theme.CrowdinTodoTheme

/**
 * Kept deliberately as a real activity rather than the Android 12+ splash screen API: it is the
 * hook the sample uses to show the SDK's loading step (see [startMainActivityAfterLoadingTranslations]),
 * which the system splash screen cannot host.
 */
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    /**
     * Wraps the base context so the splash copy is translated by the SDK as well.
     *
     * @see BaseContextWrappingDelegate.attachBaseContext2
     */
    override fun getDelegate() = BaseContextWrappingDelegate(super.getDelegate())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CrowdinTodoTheme {
                SplashScreen()
            }
        }

        //Comment if you want open Main Activity after time span
        startMainActivityAfterTimeSpan()

        //Uncomment if you want load translations and after that open Main Activity
//        startMainActivityAfterLoadingTranslations()
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

@Composable
private fun SplashScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_crowdin_logo_badge),
                contentDescription = null,
                modifier = Modifier.width(180.dp),
            )
            Text(
                text = localizedString(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 24.dp),
            )
            LinearProgressIndicator(
                modifier = Modifier.padding(top = 24.dp).width(160.dp),
            )
        }
    }
}
