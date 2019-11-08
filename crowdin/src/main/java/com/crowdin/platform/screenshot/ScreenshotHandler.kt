package com.crowdin.platform.screenshot

import android.os.Handler
import android.os.Message
import android.util.Log
import com.crowdin.platform.Crowdin

internal class ScreenshotHandler : Handler() {

    companion object {
        const val MSG_SCREENSHOT = 1
    }

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            MSG_SCREENSHOT -> {
                val filePath = msg.obj.toString()

                Crowdin.sendScreenshot(filePath, object : ScreenshotCallback {
                    override fun onSuccess() {
                        Log.d(ScreenshotHandler::class.java.simpleName, "Screenshot uploaded")
                    }

                    override fun onFailure(throwable: Throwable) {
                        Log.d(
                            ScreenshotHandler::class.java.simpleName,
                            "Screenshot uploading error: ${throwable.localizedMessage}"
                        )
                    }
                })
            }
        }
    }
}