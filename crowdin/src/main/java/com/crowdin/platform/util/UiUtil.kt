package com.crowdin.platform.util

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import com.crowdin.platform.R

object UiUtil {

    @SuppressLint("InflateParams")
    fun createAuthDialog(context: Context, positiveAction: () -> Unit) {
        try {
            createAuthDialogViaAlertDialog(context, positiveAction)
        } catch (ex: Exception) {
            ex.printStackTrace()

            try {
                createAuthDialogViaOverlayWindow(context, positiveAction)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun createAuthDialogViaOverlayWindow(context: Context, positiveAction: (() -> Unit)) {
        val floatingView = LayoutInflater.from(context).inflate(R.layout.auth_dialog, null)

        val layoutFlag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        // Add the view to the window.
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        // Specify the view position
        // Initially view will be added to top-left corner
        params.gravity = Gravity.CENTER

        // Add the view to the window
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(floatingView, params)

        floatingView.findViewById<View>(R.id.auth_dialog_root).setOnClickListener {
            windowManager.removeView(floatingView)
        }
        floatingView.findViewById<Button>(R.id.cancelBtn).setOnClickListener {
            windowManager.removeView(floatingView)
        }
        floatingView.findViewById<Button>(R.id.okBtn).setOnClickListener {
            windowManager.removeView(floatingView)
            positiveAction.invoke()
        }
    }

    private fun createAuthDialogViaAlertDialog(context: Context, positiveAction: () -> Unit) {
        AlertDialog.Builder(context.applicationContext)
            .setTitle(context.getString(R.string.auth_dialog_title))
            .setMessage(context.getString(R.string.auth_dialog_desc))
            .setNegativeButton(context.getString(R.string.auth_dialog_cancel)) { dialog, _ ->
                dialog?.dismiss()
            }
            .setPositiveButton(context.getString(R.string.auth_dialog_ok)) { dialog, _ ->
                dialog?.dismiss()
                positiveAction.invoke()
            }
            .create()
            .show()
    }
}
