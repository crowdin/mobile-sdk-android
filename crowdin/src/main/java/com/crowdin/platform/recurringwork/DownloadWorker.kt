package com.crowdin.platform.recurringwork

import android.content.Context
import android.support.annotation.NonNull
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.crowdin.platform.Crowdin

internal class DownloadWorker(@NonNull val context: Context,
                              @NonNull val params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        Crowdin.initForUpdate(context)
        return Result.success()
    }
}