package com.crowdin.platform.auth

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.crowdin.platform.Crowdin
import com.crowdin.platform.R
import com.crowdin.platform.data.DistributionInfoCallback
import com.crowdin.platform.data.model.AuthInfo
import com.crowdin.platform.data.model.AuthResponse
import com.crowdin.platform.data.model.TokenRequest
import com.crowdin.platform.data.remote.CrowdinRetrofitService
import com.crowdin.platform.util.ThreadUtils
import com.crowdin.platform.util.executeIO

internal class AuthActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressView: LinearLayout
    private lateinit var clientId: String
    private lateinit var clientSecret: String
    private var domain: String? = null

    companion object {

        private const val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1330
        private const val DOMAIN = "domain"
        private const val GRANT_TYPE = "authorization_code"
        private const val REDIRECT_URI = "crowdintest://"

        @JvmStatic
        fun launchActivity(context: Context) {
            val intent = Intent(context, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_layout)

        webView = findViewById(R.id.webView)
        progressView = findViewById(R.id.progressView)

        if (Crowdin.isAuthorized()) {
            requestPermission()
        } else {
            requestAuthorization()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun requestAuthorization() {
        val authConfig = Crowdin.getAuthConfig()
        clientId = authConfig?.clientId ?: ""
        clientSecret = authConfig?.clientSecret ?: ""
        domain = authConfig?.organizationName

        val builder = Uri.Builder()
            .scheme("https")
            .authority("accounts.crowdin.com")
            .appendPath("oauth")
            .appendPath("authorize")
            .encodedQuery("client_id=$clientId&response_type=code&scope=project&redirect_uri=$REDIRECT_URI")

        domain?.let { builder.appendQueryParameter(DOMAIN, it) }
        val url = builder.build().toString()

        webView.settings.userAgentString = System.getProperty("http.agent")
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.startsWith(REDIRECT_URI)) {
                    val uri = Uri.parse(url)
                    val code = uri.getQueryParameter("code") ?: ""
                    handleCode(code)
                }

                return false
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                progressView.visibility = View.VISIBLE
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView, url: String) {
                if (!url.startsWith(REDIRECT_URI)) {
                    progressView.visibility = View.GONE
                }
                super.onPageFinished(view, url)
            }
        }
        webView.loadUrl(url)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> finish()
            else -> finish()
        }
    }

    private fun handleCode(code: String) {
        if (code.isNotEmpty()) {
            progressView.visibility = View.VISIBLE
            ThreadUtils.runInBackgroundPool({
                val apiService = CrowdinRetrofitService.getCrowdinAuthApi()
                executeIO {
                    val response = apiService.getToken(
                        TokenRequest(
                            GRANT_TYPE,
                            clientId, clientSecret, REDIRECT_URI, code
                        ), domain
                    ).execute()
                    if (response.isSuccessful) {
                        response.body()?.let {
                            saveAuthInfo(it)
                            getDistributionInfo()
                            initRealtimePreview()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, "Not authenticated.", Toast.LENGTH_LONG).show()
                            requestPermission()
                        }
                    }
                }
            }, false)
        } else {
            Toast.makeText(this, "Not authorized.", Toast.LENGTH_LONG).show()
            requestPermission()
        }
    }

    private fun saveAuthInfo(authResponse: AuthResponse) {
        Crowdin.saveAuthInfo(AuthInfo(authResponse))
    }

    private fun initRealtimePreview() {
        Crowdin.createRealTimeConnection()
    }

    private fun getDistributionInfo() {
        Crowdin.getDistributionInfo(object : DistributionInfoCallback {
            override fun onResponse() {
                requestPermission()
                Crowdin.downloadTranslation()
            }

            override fun onError(throwable: Throwable) {
                Crowdin.saveAuthInfo(null)
                requestPermission()
                Log.d(
                    AuthActivity::class.java.simpleName,
                    "Get info, onFailure:${throwable.localizedMessage}"
                )
            }
        })
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )
        } else {
            finish()
        }
    }
}
