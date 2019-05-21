package com.crowdin.platform.screenshot

import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.text.TextUtils
import com.crowdin.platform.screenshot.ScreenshotHandler.Companion.MSG_SCREENSHOT

/**
 * Creates a content observer.
 *
 * @param handler The handler to run [.onChange] on, or null if none.
 */
internal class ScreenshotService(private val context: Context,
                                 private val handler: Handler) : ContentObserver(handler) {

    companion object {
        private const val TIME_GAP: Long = 0x4
    }

    override fun onChange(selfChange: Boolean) {
        val resolver = context.contentResolver

        val current = System.currentTimeMillis() / 1000
        val selection = String.format("date_added > %s and date_added < %s and ( _data like ? or _data like ? or _data like ? )",
                current - TIME_GAP,
                current + TIME_GAP)

        val selectionArgs = arrayOf("%Screenshot%", "%screenshot%", "%\u622a\u5c4f%")
        try {
            resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    selection,
                    selectionArgs, null)
                    .use { cursor: Cursor? ->
                        if (!cursor?.moveToLast()!!) {
                            return
                        }
                        val dataIdx = cursor.getColumnIndexOrThrow("_data")
                        val data = cursor.getString(dataIdx)
                        val mineTypeIdx = cursor.getColumnIndexOrThrow("mime_type")
                        val mineType = cursor.getString(mineTypeIdx)

                        if (TextUtils.isEmpty(mineType)) {
                            return
                        }
                        sendMessage(data)
                    }

        } catch (tr: Throwable) {
            sendMessage(String.format("Error: %s", tr.message))
        }
    }

    private fun sendMessage(msg: String) {
        val message = Message()
        message.what = MSG_SCREENSHOT
        message.obj = msg
        handler.sendMessage(message)
    }
}