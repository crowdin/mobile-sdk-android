package com.crowdin.crowdin_controls

import android.app.Activity
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import java.lang.ref.WeakReference

fun initCrowdinControl(activity: Activity, overlayPermissionActivityResultLauncher: ActivityResultLauncher<String?>) {
    // Check if the application has draw over other apps permission or not?
    // This permission is by default available for API<23. But for API > 23
    // you have to ask for the permission in runtime.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(activity)) {
        overlayPermissionActivityResultLauncher.launch(null)
    } else {
        CrowdinWidgetService.launchService(WeakReference(activity))
    }
}

fun destroyCrowdinControl(activity: Activity) {
    CrowdinWidgetService.destroyService(activity)
}