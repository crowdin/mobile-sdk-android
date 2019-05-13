package com.crowdin.platform.screenshot

/**
 * Provides information about screenshot creating state.
 */
interface ScreenshotCallback {

    /**
     * Screenshot with tags successfully added to `crowdin` project.
     */
    fun onSuccess()

    /**
     * Error happened during uploading screenshot process.
     */
    fun onFailure(error: String)
}