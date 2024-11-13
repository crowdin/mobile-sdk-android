package com.crowdin.platform.example

import android.app.Activity
import android.graphics.Bitmap
import androidx.test.core.app.ActivityScenario
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.crowdin.crowdin_controls.showToast
import com.crowdin.platform.Crowdin
import com.crowdin.platform.screenshot.ScreenshotCallback
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4ClassRunner::class)
class ScreenshotAutomationTest {

    @Test
    fun automateScreenshotCaptureAndUpload() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        val latch = CountDownLatch(1)

        activityScenario.onActivity { activity ->
            captureAndSendScreenshot(activity, latch)
        }

        // Wait for the latch to be decremented, or timeout after 5 seconds
        latch.await(5, TimeUnit.SECONDS)
        activityScenario.close()
    }

    private fun captureAndSendScreenshot(activity: Activity, latch: CountDownLatch) {
        // Capture the screenshot as a Bitmap
        val rootView = activity.window.decorView.rootView
        rootView.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(rootView.drawingCache)
        rootView.isDrawingCacheEnabled = false

        // Upload the screenshot to Crowdin using Crowdin SDK
        Crowdin.sendScreenshot(bitmap, object : ScreenshotCallback {
            override fun onSuccess() {
                activity.showToast("Screenshot uploaded")
                latch.countDown()
            }

            override fun onFailure(throwable: Throwable) {
                activity.showToast("Screenshot upload failed")
                latch.countDown()
            }
        })
    }
}