package com.crowdin.platform

/**
 * Callback for transferring [com.crowdin.platform.data.model.LanguageData] as a json when received from
 * crowdin platform or empty string on error.
 */
interface ResourcesCallback {
    fun onDataReceived(json: String)
}
