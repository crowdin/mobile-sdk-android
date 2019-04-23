package com.crowdin.platform.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.view.PixelCopy
import android.view.View

object ScreenshotUtils {

    @JvmStatic
    fun getBitmapFromView(view: View, activity: Activity, callback: (Bitmap) -> Unit) {
        val root = view.rootView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.window?.let { window ->
                val bitmap = Bitmap.createBitmap(root.width, root.height, Bitmap.Config.ARGB_8888)
                val locationOfViewInWindow = IntArray(2)
                root.getLocationInWindow(locationOfViewInWindow)
                try {
                    PixelCopy.request(window, Rect(locationOfViewInWindow[0],
                            locationOfViewInWindow[1],
                            locationOfViewInWindow[0] + root.width,
                            locationOfViewInWindow[1] + root.height),
                            bitmap,
                            { copyResult ->
                                if (copyResult == PixelCopy.SUCCESS) {
                                    callback(bitmap)
                                }
                            }, Handler())
                } catch (e: IllegalArgumentException) {
                    callback(takeScreenshot(root))
                }
            }
        } else {
            callback(takeScreenshot(root))
        }
    }

    private fun takeScreenshot(v: View): Bitmap {
        v.isDrawingCacheEnabled = true
        v.buildDrawingCache(true)
        val bitmap = Bitmap.createBitmap(v.drawingCache)
        v.isDrawingCacheEnabled = false
        return bitmap
    }
}