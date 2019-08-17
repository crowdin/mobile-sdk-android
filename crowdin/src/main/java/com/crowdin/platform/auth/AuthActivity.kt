package com.crowdin.platform.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.crowdin.platform.BuildConfig
import com.crowdin.platform.Crowdin
import com.crowdin.platform.R
import com.crowdin.platform.data.DistributionInfoCallback
import com.crowdin.platform.data.model.AuthInfo
import com.crowdin.platform.data.model.AuthResponse
import com.crowdin.platform.data.model.TokenRequest
import com.crowdin.platform.data.remote.CrowdinRetrofitService
import com.crowdin.platform.util.ThreadUtils
import kotlinx.android.synthetic.main.auth_layout.*

internal class AuthActivity : AppCompatActivity() {

    private var event: String? = null
    private var authAttemptCounter = 0

    companion object {

        private const val AUTH_ATTEMPT_THRESHOLD = 1
        private const val EVENT_TYPE = "type"
        const val EVENT_REAL_TIME_UPDATES = "realtime_update"
        private const val GRANT_TYPE = "authorization_code"
        private const val REDIRECT_URI = "crowdintest://"

        @JvmStatic
        @JvmOverloads
        fun launchActivity(activity: Activity, type: String? = null) {
            val intent = Intent(activity, AuthActivity::class.java)
            type?.let { intent.putExtra(EVENT_TYPE, type) }
            activity.startActivity(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val data = intent?.data
        val code = data?.getQueryParameter("code") ?: ""
        handleCode(code)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_layout)
        statusTextView.text = getString(R.string.authorizing)
        event = intent.getStringExtra(EVENT_TYPE)

        if (authAttemptCounter != AUTH_ATTEMPT_THRESHOLD) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.URL_CROWDIN_AUTH))
            startActivity(browserIntent)
            authAttemptCounter++
        } else {
            finish()
        }
    }

    private fun handleCode(code: String) {
        if (code.isNotEmpty()) {
            statusTextView.text = getString(R.string.loading)
            ThreadUtils.runInBackgroundPool(Runnable {
                val apiService = CrowdinRetrofitService.instance.getCrowdinAuthApi()
                val response = apiService.getToken(TokenRequest(GRANT_TYPE, BuildConfig.CLIENT_ID,
                        BuildConfig.CLIENT_SECRET, REDIRECT_URI, code)).execute()
                if (response.isSuccessful && response.body() != null) {
                    getDistributionInfo(event, response.body()!!)
                } else {
                    Toast.makeText(this, "Not authenticated.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }, false)

        } else {
            Toast.makeText(this, "Not authorized.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun getDistributionInfo(event: String?, authResponse: AuthResponse) {
        Crowdin.getDistributionInfo(object : DistributionInfoCallback {
            override fun onSuccess() {
                Crowdin.saveAuthInfo(AuthInfo(authResponse))
                if (event == EVENT_REAL_TIME_UPDATES) {
                    Crowdin.createConnection()
                }
                finish()
            }

            override fun onError(throwable: Throwable) {
                finish()
                Log.d(AuthActivity::class.java.simpleName, "Get info, onFailure:${throwable.localizedMessage}")
            }
        })
    }
}
