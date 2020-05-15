package com.crowdin.platform.util

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import com.crowdin.platform.R

fun createAuthDialog(
    activity: Activity,
    positiveAction: (() -> Unit)
) {
    val builder = AlertDialog.Builder(activity)
    builder.setTitle(activity.getString(R.string.auth_dialog_title))
    builder.setMessage(activity.getString(R.string.auth_dialog_desc))
    builder.setPositiveButton(activity.getString(R.string.ok)) { _: DialogInterface, _: Int ->
        positiveAction.invoke()
    }
    builder.setNegativeButton(activity.getString(R.string.cancel), null)
    builder.create().show()
}
