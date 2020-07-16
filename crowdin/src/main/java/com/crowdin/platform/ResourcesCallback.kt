package com.crowdin.platform

/**
 * Callback for transferring [LanguageData] as a json when received from
 * crowdin platform or empty string on error.
 */
interface ResourcesCallback {

    fun onDataReceived(json: String)
}
