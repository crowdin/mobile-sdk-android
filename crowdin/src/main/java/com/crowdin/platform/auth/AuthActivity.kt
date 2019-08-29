package com.crowdin.platform.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.crowdin.platform.Crowdin
import com.crowdin.platform.R
import com.crowdin.platform.data.DistributionInfoCallback
import com.crowdin.platform.data.model.AuthInfo
import com.crowdin.platform.data.model.TokenRequest
import com.crowdin.platform.data.remote.CrowdinRetrofitService
import com.crowdin.platform.util.ThreadUtils
import kotlinx.android.synthetic.main.auth_layout.*

internal class AuthActivity : AppCompatActivity() {

    private var event: String? = null
    private var authAttemptCounter = 0
    private lateinit var clientId: String
    private lateinit var clientSecret: String
    private var domain: String? = null

    companion object {

        private const val DOMAIN = "domain"
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

        val authConfig = Crowdin.getAuthConfig()
        clientId = authConfig?.clientId ?: ""
        clientSecret = authConfig?.clientSecret ?: ""
        domain = authConfig?.organizationName

        if (authAttemptCounter != AUTH_ATTEMPT_THRESHOLD) {
            val builder = Uri.Builder()
                    .scheme("https")
                    .authority("accounts.crowdin.com")
                    .appendPath("oauth")
                    .appendPath("authorize")
                    .encodedQuery("client_id=$clientId&response_type=code&scope=project.content.screenshots&redirect_uri=crowdintest://")

            domain?.let { builder.appendQueryParameter(DOMAIN, it) }
            val url = builder.build().toString()
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
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
                val apiService = CrowdinRetrofitService.getCrowdinAuthApi()
                val response = apiService.getToken(TokenRequest(GRANT_TYPE,
                        clientId, clientSecret, REDIRECT_URI, code), domain).execute()
                if (response.isSuccessful && response.body() != null) {
                    Crowdin.saveAuthInfo(AuthInfo(response.body()!!))
                    getDistributionInfo(event)
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Not authenticated.", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            }, false)

        } else {
            Toast.makeText(this, "Not authorized.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun getDistributionInfo(event: String?) {
        Crowdin.getDistributionInfo(object : DistributionInfoCallback {
            override fun onResponse() {
                if (event == EVENT_REAL_TIME_UPDATES) {
                    Crowdin.createConnection()
                }
                finish()
            }

            override fun onError(throwable: Throwable) {
                Crowdin.saveAuthInfo(null)
                finish()
                Log.d(AuthActivity::class.java.simpleName, "Get info, onFailure:${throwable.localizedMessage}")
            }
        })
    }
}
