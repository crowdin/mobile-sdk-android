package com.crowdin.platform.example

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.View
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.crowdin.platform.Crowdin
import com.crowdin.platform.screenshot.ScreenshotCallback
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@Ignore("This test is for screenshot automation")
@RunWith(AndroidJUnit4::class)
@LargeTest
class ScreenshotAutomationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val activity: Activity get() = composeTestRule.activity

    @Test
    fun screenshotCapture() {
        // Tasks
        captureAndSendScreenshot("dashboard")

        // Add task
        composeTestRule.onNodeWithText(string(R.string.add_task)).performClick()
        composeTestRule.waitForIdle()
        captureAndSendScreenshot("add_task")
        Espresso.pressBack()

        // Category
        openDestination(R.string.category)
        captureAndSendScreenshot("category")

        // History
        openDestination(R.string.history)
        captureAndSendScreenshot("history")

        // Settings
        openDestination(R.string.settings)
        captureAndSendScreenshot("settings")
    }

    private fun openDestination(titleRes: Int) {
        composeTestRule.onNodeWithText(string(titleRes)).performClick()
        composeTestRule.waitForIdle()
    }

    private fun string(resId: Int): String = activity.getString(resId)

    private fun captureAndSendScreenshot(name: String) {
        Log.d(TAG, "Capture and send screenshot")
        val latch = CountDownLatch(1)

        val bitmap = captureBitmap(activity.window.decorView.rootView)

        Log.d(TAG, "bitmap generated")
        Crowdin.sendScreenshot(
            bitmap = bitmap,
            screenshotName = name,
            screenshotCallback = object : ScreenshotCallback {
                override fun onSuccess() {
                    Log.d(TAG, "Screenshot upload onSuccess")
                    latch.countDown()
                }

                override fun onFailure(throwable: Throwable) {
                    Log.e(TAG, "Screenshot upload onFailure: ${throwable.message}")
                    throw AssertionError("Screenshot upload failed: ${throwable.message}")
                }
            }
        )

        latch.await(30, TimeUnit.SECONDS)
    }

    private fun captureBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private companion object {
        const val TAG = "ScreenshotAutomationTest"
    }
}
