package com.crowdin.platform.screenshot

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import com.crowdin.platform.Crowdin
import com.crowdin.platform.R
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

/**
 * Creates a content observer.
 */
internal class ScreenshotService(
    private val context: Context,
) : ContentObserver(Handler()) {
    companion object {
        private const val TIME_GAP: Long = 0xA
    }

    private var uploading = false

    private var onErrorListener: ((String) -> Unit)? = null

    fun setOnErrorListener(unit: (String) -> Unit) {
        onErrorListener = unit
    }

    override fun onChange(selfChange: Boolean) {
        val permission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

        Dexter
            .withContext(context)
            .withPermission(permission)
            .withListener(
                object : PermissionListener {
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        searchAndUploadScreenshot()
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        onErrorListener?.invoke(context.getString(R.string.required_permission_read_storage, permission))
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        p1: PermissionToken?,
                    ) {
                        p1?.continuePermissionRequest()
                    }
                },
            ).check()
    }

    private fun searchAndUploadScreenshot() {
        Log.d(ScreenshotService::class.java.simpleName, "Searching screenshot started")

        val resolver = context.contentResolver
        val current = System.currentTimeMillis() / 1000
        val selection =
            String.format(
                "date_added > %s and date_added < %s and ( _data like ? or _data like ? or _data like ? )",
                current - TIME_GAP,
                current + TIME_GAP,
            )

        val selectionArgs = arrayOf("%Screenshot%", "%screenshot%", "%\u622a\u5c4f%")
        try {
            resolver
                .query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    selection,
                    selectionArgs,
                    null,
                ).use { cursor: Cursor? ->
                    if (!cursor?.moveToLast()!!) {
                        return
                    }
                    val mineTypeIdx = cursor.getColumnIndexOrThrow("mime_type")
                    val mineType = cursor.getString(mineTypeIdx)

                    if (TextUtils.isEmpty(mineType)) {
                        return
                    }

                    val idIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)
                    if (idIndex < 0) {
                        Log.d(ScreenshotService::class.java.simpleName, "Error: screenshot not found")
                        return@use
                    }

                    val imageUri: Uri =
                        ContentUris
                            .withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                cursor.getLong(idIndex),
                            )

                    val bitmap = BitmapFactory.decodeStream(resolver.openInputStream(imageUri))
                    sendScreenshot(bitmap)
                }
        } catch (tr: Throwable) {
            Log.d(ScreenshotService::class.java.simpleName, "Error: ${tr.message}")
        }
    }

    private fun sendScreenshot(bitmap: Bitmap) {
        if (uploading) {
            Log.d(ScreenshotService::class.java.simpleName, "Uploading already started. Skipped")
            return
        }

        uploading = true
        Log.d(ScreenshotService::class.java.simpleName, "Screenshot uploading started")

        Crowdin.sendScreenshot(
            bitmap = bitmap,
            screenshotCallback =
                object : ScreenshotCallback {
                    override fun onSuccess() {
                        uploading = false
                        Log.d(ScreenshotService::class.java.simpleName, "Screenshot uploaded")
                    }

                    override fun onFailure(throwable: Throwable) {
                        uploading = false
                        Log.d(
                            ScreenshotService::class.java.simpleName,
                            "Screenshot uploading error: ${throwable.localizedMessage}",
                        )
                    }
                },
        )
    }
}
