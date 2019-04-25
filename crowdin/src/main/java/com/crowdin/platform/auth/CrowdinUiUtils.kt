package com.crowdin.platform.auth

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AlertDialog
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient


object CrowdinUiUtils {

    private const val URL_PROFILE = "https://crowdin.com/profile"
    private const val URL_CROWDIN_AUTH = "https:\\www.crowdin.com/login"
    private const val COOKIE_TOKEN = "csrf_token"
    private var isCookieAvailable = false

    @SuppressLint("SetJavaScriptEnabled")
    @JvmStatic
    fun showCrowdinAuthDialog(context: Context) {
        val webView = WebView(context)
        webView.loadUrl(URL_CROWDIN_AUTH)
        webView.settings.javaScriptEnabled = true

        val dialog = AlertDialog.Builder(context)
                .setView(webView)
                .create()
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)

                if (!isCookieAvailable) {
                    dialog.show()
                }
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                var value = ""
                val cookies = CookieManager.getInstance().getCookie(url)
                if (cookies != null) {
                    val list = cookies.split(";")
                    for (item in list) {
                        if (item.contains(COOKIE_TOKEN)) {
                            val temp = item.split("=".toRegex())
                                    .dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            value = temp[1]
                        }
                    }
                }

                if (url == URL_PROFILE && value.isNotEmpty()) {
                    dialog.dismiss()
                    isCookieAvailable = true
                }
            }
        }
    }
}