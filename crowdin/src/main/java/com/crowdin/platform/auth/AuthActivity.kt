package com.crowdin.platform.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import androidx.core.net.toUri
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
        private const val DOMAIN = "domain"
        private const val GRANT_TYPE = "authorization_code"
        private const val EXTRA_REDIRECT_URI = "redirectURI"

        @JvmStatic
        fun launchActivity(
            context: Context,
            redirectURI: String,
        ) {
            val intent = Intent(context, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(EXTRA_REDIRECT_URI, redirectURI)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_layout)

        webView = findViewById(R.id.webView)
        progressView = findViewById(R.id.progressView)

        val redirectUri = intent.getStringExtra(EXTRA_REDIRECT_URI) ?: ""
        if (Crowdin.isAuthorized() || redirectUri.isEmpty()) {
            close()
        } else {
            requestAuthorization(redirectUri)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun requestAuthorization(redirectUri: String) {
        val authConfig = Crowdin.getAuthConfig()
        domain = Crowdin.getOrganizationName()
        clientId = authConfig?.clientId ?: ""
        clientSecret = authConfig?.clientSecret ?: ""

        val builder =
            Uri
                .Builder()
                .scheme("https")
                .authority("accounts.crowdin.com")
                .appendPath("oauth")
                .appendPath("authorize")
                .encodedQuery("client_id=$clientId&response_type=code&scope=project&redirect_uri=$redirectUri")

        domain?.let { builder.appendQueryParameter(DOMAIN, it) }
        val url = builder.build().toString()

        webView.settings.userAgentString = System.getProperty("http.agent")
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient =
            object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    url: String,
                ): Boolean {
                    if (url.startsWith(redirectUri)) {
                        val uri = url.toUri()
                        val code = uri.getQueryParameter("code") ?: ""
                        handleCode(code, redirectUri)
                    }

                    return false
                }

                override fun onPageStarted(
                    view: WebView,
                    url: String,
                    favicon: Bitmap?,
                ) {
                    progressView.visibility = View.VISIBLE
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(
                    view: WebView,
                    url: String,
                ) {
                    if (!url.startsWith(redirectUri)) {
                        progressView.visibility = View.GONE
                    }
                    super.onPageFinished(view, url)
                }
            }
        webView.loadUrl(url)
    }

    private fun handleCode(
        code: String,
        redirectUri: String,
    ) {
        if (code.isNotEmpty()) {
            progressView.visibility = View.VISIBLE
            ThreadUtils.runInBackgroundPool({
                val apiService = CrowdinRetrofitService.getCrowdinAuthApi()
                executeIO {
                    val response =
                        apiService
                            .getToken(
                                TokenRequest(
                                    GRANT_TYPE,
                                    clientId,
                                    clientSecret,
                                    redirectUri,
                                    code,
                                ),
                                domain,
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
                            close()
                        }
                    }
                }
            }, false)
        } else {
            Toast.makeText(this, "Not authorized.", Toast.LENGTH_LONG).show()
            close()
        }
    }

    private fun saveAuthInfo(authResponse: AuthResponse) {
        Crowdin.saveAuthInfo(AuthInfo(authResponse))
    }

    private fun initRealtimePreview() {
        Crowdin.createRealTimeConnection(this)
    }

    private fun getDistributionInfo() {
        Crowdin.getDistributionInfo(
            object : DistributionInfoCallback {
                override fun onResponse() {
                    close()
                    Crowdin.downloadTranslation()
                }

                override fun onError(throwable: Throwable) {
                    Crowdin.saveAuthInfo(null)
                    close()
                    Log.d(
                        AuthActivity::class.java.simpleName,
                        "Get info, onFailure:${throwable.localizedMessage}",
                    )
                }
            },
        )
    }

    private fun close() {
        finish()
    }
}
