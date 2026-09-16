package com.crowdin.crowdin_controls

import android.app.Activity
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import java.lang.ref.WeakReference

fun initCrowdinControl(activity: Activity, overlayPermissionActivityResultLauncher: ActivityResultLauncher<String?>) {
    if (!Settings.canDrawOverlays(activity)) {
        overlayPermissionActivityResultLauncher.launch(null)
    } else {
        CrowdinWidgetService.launchService(WeakReference(activity))
    }
}

fun destroyCrowdinControl(activity: Activity) {
    CrowdinWidgetService.destroyService(activity)
}