package com.crowdin.platform.screenshot

/**
 * Provides information about screenshot creating status.
 */
interface ScreenshotCallback {

    /**
     * Screenshot with tags successfully added to `crowdin` project.
     */
    fun onSuccess()

    /**
     * Error happened during uploading screenshot process.
     *
     * @param error message.
     */
    fun onFailure(error: String)
}