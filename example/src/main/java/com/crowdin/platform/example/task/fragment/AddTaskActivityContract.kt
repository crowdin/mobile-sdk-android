package com.crowdin.platform.example.task.fragment

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.crowdin.platform.example.task.AddTaskActivity

class AddTaskActivityContract : ActivityResultContract<String?, Int>() {
    override fun createIntent(context: Context, input: String?): Intent {
        return Intent(context, AddTaskActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Int {
        return resultCode
    }
}
