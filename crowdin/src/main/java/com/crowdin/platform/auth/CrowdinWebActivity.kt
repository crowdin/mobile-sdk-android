package com.crowdin.platform.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.crowdin.platform.Crowdin
import com.crowdin.platform.data.DistributionInfoCallback
import com.crowdin.platform.data.model.AuthInfo
import com.crowdin.platform.realtimeupdate.RealTimeUpdateManager

class CrowdinWebActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var webView: WebView
    private lateinit var userAgent: String

    companion object {

        const val REQUEST_CODE = 0x463
        private const val URL_PROFILE = "https://crowdin.com/profile"
        private const val URL_CROWDIN_AUTH = "https://crowdin.com/login"
        private const val COOKIE_TOKEN = "csrf_token"
        private const val EVENT_TYPE = "type"
        const val EVENT_REAL_TIME_UPDATES = "realtime_update"

        @JvmStatic
        @JvmOverloads
        fun launchActivityForResult(activity: Activity, type: String? = null) {
            val intent = Intent(activity, CrowdinWebActivity::class.java)
            type?.let { intent.putExtra(EVENT_TYPE, type) }
            activity.startActivityForResult(intent, REQUEST_CODE)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createContentView())
        val event = intent.getStringExtra(EVENT_TYPE)

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
                    val authInfo = AuthInfo(userAgent, cookies, csrfToken)
                    Crowdin.saveAuthInfo(authInfo)
                    setResult(Activity.RESULT_OK)

                    progressBar.visibility = View.VISIBLE
                    Crowdin.getDistributionInfo(userAgent, cookies, csrfToken, object : DistributionInfoCallback {
                        override fun onSuccess() {
                            if (event == EVENT_REAL_TIME_UPDATES) {
                                Crowdin.createConnection()
                            }
                            finish()
                        }

                        override fun onError(throwable: Throwable) {
                            finish()
                            Log.d(RealTimeUpdateManager::class.java.simpleName,
                                    "Get info, onFailure:${throwable.localizedMessage}")
                        }
                    })
                }
            }
        }
    }

    private fun createContentView(): FrameLayout {
        val rootView = FrameLayout(this)
        webView = WebView(this)
        rootView.addView(webView)

        progressBar = ProgressBar(applicationContext, null, android.R.attr.progressBarStyle)
        val progressBarParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        progressBarParams.gravity = Gravity.CENTER
        progressBar.layoutParams = progressBarParams
        progressBar.visibility = View.GONE
        rootView.addView(progressBar)

        return rootView
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