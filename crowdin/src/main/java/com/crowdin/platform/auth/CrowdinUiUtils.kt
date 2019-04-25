package com.crowdin.platform.auth

import android.content.Context
import android.support.v7.app.AlertDialog
import android.webkit.WebView
import android.webkit.WebViewClient


object CrowdinUiUtils {

    private const val CROWDIN_AUTH = "https:\\www.crowdin.com/login"

    @JvmStatic
    fun showCrowdinAuthDialog(context: Context) {
        val alert = AlertDialog.Builder(context)
        val webView = WebView(context)
        webView.loadUrl(CROWDIN_AUTH)
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }

        alert.setView(webView)
        alert.show()
    }
}