package com.crowdin.platform.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.crowdin.platform.Crowdin
import com.crowdin.platform.data.model.AuthInfo

class CrowdinWebActivity : AppCompatActivity() {

    private lateinit var userAgent: String

    companion object {

        private const val URL_PROFILE = "https://crowdin.com/profile"
        private const val URL_CROWDIN_AUTH = "https://crowdin.com/login"
        private const val COOKIE_TOKEN = "csrf_token"

        fun launchActivity(activity: Activity) {
            activity.startActivity(Intent(activity, CrowdinWebActivity::class.java))
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val webView = WebView(this)
        setContentView(webView)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(URL_CROWDIN_AUTH)
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                userAgent = view.settings.userAgentString
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                val cookies = CookieManager.getInstance().getCookie(url)
                val csrfToken = getToken(cookies)
                if (url == URL_PROFILE && csrfToken.isNotEmpty()) {
                    Crowdin.saveAuthInfo(AuthInfo(userAgent, cookies, csrfToken))

                    // TODO: remove
                    Crowdin.startRealTimeUpdates()
                    finish()
                }
            }
        }
    }

    private fun getToken(cookies: String): String {
        var csrfToken = ""
        val list = cookies.split(";")
        for (item in list) {
            if (item.contains(COOKIE_TOKEN)) {
                val temp = item.split("=".toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                csrfToken = temp[1]
            }
        }
        return csrfToken
    }
}