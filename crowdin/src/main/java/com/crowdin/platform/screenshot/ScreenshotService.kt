package com.crowdin.platform.screenshot

import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import com.crowdin.platform.Crowdin

/**
 * Creates a content observer.
 */
internal class ScreenshotService(private val context: Context) : ContentObserver(Handler()) {

    companion object {
        private const val TIME_GAP: Long = 0x4
    }

    private var uploading = false

    override fun onChange(selfChange: Boolean) {
        val resolver = context.contentResolver
        val current = System.currentTimeMillis() / 1000
        val selection = String.format(
            "date_added > %s and date_added < %s and ( _data like ? or _data like ? or _data like ? )",
            current - TIME_GAP,
            current + TIME_GAP
        )

        val selectionArgs = arrayOf("%Screenshot%", "%screenshot%", "%\u622a\u5c4f%")
        try {
            resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                selection,
                selectionArgs, null
            ).use { cursor: Cursor? ->
                if (!cursor?.moveToLast()!!) {
                    return
                }
                val mineTypeIdx = cursor.getColumnIndexOrThrow("mime_type")
                val mineType = cursor.getString(mineTypeIdx)

                if (TextUtils.isEmpty(mineType)) {
                    return
                }

                val imageUri: Uri = ContentUris
                    .withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)).toLong()
                    )
                val bitmap = BitmapFactory.decodeStream(resolver.openInputStream(imageUri))

                if (!uploading) {
                    uploading = true
                    sendScreenshot(bitmap)
                }
            }
        } catch (tr: Throwable) {
            Log.d(ScreenshotService::class.java.simpleName, "Error: ${tr.message}")
        }
    }

    private fun sendScreenshot(bitmap: Bitmap) {
        Crowdin.sendScreenshot(bitmap, object : ScreenshotCallback {
            override fun onSuccess() {
                uploading = false
                Log.d(ScreenshotService::class.java.simpleName, "Screenshot uploaded")
            }

            override fun onFailure(throwable: Throwable) {
                uploading = false
                Log.d(
                    ScreenshotService::class.java.simpleName,
                    "Screenshot uploading error: ${throwable.localizedMessage}"
                )
            }
        })
    }
}
