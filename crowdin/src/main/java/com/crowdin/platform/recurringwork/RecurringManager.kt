package com.crowdin.platform.recurringwork

import android.content.Context
import androidx.work.*
import com.crowdin.platform.CrowdinConfig
import com.google.gson.Gson
import java.util.*
import java.util.concurrent.TimeUnit

internal object RecurringManager {


    const val MIN_PERIODIC_INTERVAL_MILLIS = 15 * 60 * 1000L // 15 minutes.
    private const val SHARED_PREF_NAME = "com.crowdin.platform.config"
    private const val CROWDIN_CONFIG = "crowdin_config"
    private const val WORKER_UUID = "worker_uuid"
    private const val WORK_STATE = "work_key"
    private const val WORK_STARTED = "started"
    private const val WORK_CANCELED = "canceled"

    lateinit var downloadRequest: PeriodicWorkRequest

    fun setPeriodicUpdates(context: Context, config: CrowdinConfig) {
        if (getRecurringState(context) == WORK_STARTED) return

        saveConfig(context, config)

        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        downloadRequest = PeriodicWorkRequestBuilder<DownloadWorker>(config.updateInterval, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance().enqueue(downloadRequest)
        saveRecurringState(context, WORK_STARTED)
        saveJobId(context, downloadRequest.id)
    }

    fun cancel(context: Context) {
        val jobId = getJobId(context)
        if (jobId != null) {
            WorkManager.getInstance().cancelWorkById(jobId)
            saveRecurringState(context, WORK_CANCELED)
        }
    }

    private fun saveJobId(context: Context, id: UUID) {
        val json = Gson().toJson(id)
        val sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(WORKER_UUID, json).apply()
    }

    private fun getJobId(context: Context): UUID? {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val value = sharedPreferences.getString(WORKER_UUID, null)

        return if (value == null) {
            null
        } else {
            Gson().fromJson(value, UUID::class.java)
        }
    }

    private fun saveConfig(context: Context, config: CrowdinConfig) {
        val json = Gson().toJson(config)
        val sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(CROWDIN_CONFIG, json).apply()
    }

    internal fun getConfig(context: Context): CrowdinConfig {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return Gson().fromJson(sharedPreferences.getString(CROWDIN_CONFIG, ""), CrowdinConfig::class.java)
    }

    private fun saveRecurringState(context: Context, state: String) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(WORK_STATE, state).apply()
    }

    private fun getRecurringState(context: Context): String? {
        return context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).getString(WORK_STATE, "")
    }
}