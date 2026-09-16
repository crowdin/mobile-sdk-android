package com.crowdin.crowdin_controls

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract

class OverlayActivityContract : ActivityResultContract<String?, Int>() {
    override fun createIntent(context: Context, input: String?): Intent {
        return Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + context.packageName)
        )
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Int {
        return resultCode
    }

}