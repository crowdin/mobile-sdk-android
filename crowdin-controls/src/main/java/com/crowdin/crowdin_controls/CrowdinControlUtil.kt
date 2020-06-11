package com.crowdin.crowdin_controls

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import java.lang.ref.WeakReference

const val DRAW_OVER_OTHER_APP_PERMISSION = 103

fun onActivityResult(activity: Activity, requestCode: Int) {
    if (requestCode == DRAW_OVER_OTHER_APP_PERMISSION) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(activity)) {
                CrowdinWidgetService.launchService(WeakReference(activity))
            }
        }
    }
}

fun initCrowdinControl(activity: Activity) {
    // Check if the application has draw over other apps permission or not?
    // This permission is by default available for API<23. But for API > 23
    // you have to ask for the permission in runtime.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(activity)) {
        // If the draw over permission is not available open the settings screen to grant the permission.
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + activity.packageName)
        )
        activity.startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION)
    } else {
        CrowdinWidgetService.launchService(WeakReference(activity))
    }
}

fun destroyCrowdinControl(activity: Activity) {
    CrowdinWidgetService.destroyService(activity)
}