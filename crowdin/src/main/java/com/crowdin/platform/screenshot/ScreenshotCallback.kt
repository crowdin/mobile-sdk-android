package com.crowdin.platform.screenshot

interface ScreenshotCallback {

    fun onSuccess()
    fun onFailure(error: String)
}