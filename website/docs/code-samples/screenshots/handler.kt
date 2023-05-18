Crowdin.sendScreenshot(activity!!, object : ScreenshotCallback {
    override fun onSuccess() {
        Log.d(TAG, "Screenshot uploaded")
    }

    override fun onFailure(throwable: Throwable) {
        Log.d(TAG, throwable.localizedMessage)
    }
})