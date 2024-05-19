package com.crowdin.platform

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.google.gson.Gson
import java.lang.reflect.Type

private const val SHARED_PREF_NAME = "com.crowdin.platform.string.preferences"
private const val LAST_UPDATE = "com.crowdin.platform.string.preferences.last_update"

internal class CrowdinPreferences(context: Context) : Preferences {

    private lateinit var sharedPreferences: SharedPreferences

    init {
        initSharedPreferences(context)
    }

    private fun initSharedPreferences(context: Context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    override fun setString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun getString(key: String): String? = sharedPreferences.getString(key, "")

    override fun setLastUpdate(lastUpdate: Long) {
        sharedPreferences.edit { putLong(LAST_UPDATE, lastUpdate) }
    }

    override fun getLastUpdate(): Long = sharedPreferences.getLong(LAST_UPDATE, 0)

    override fun saveData(type: String, data: Any?) {
        try {
            if (data == null) {
                sharedPreferences.edit().remove(type).apply()
            }

            val json = Gson().toJson(data)
            sharedPreferences.edit().putString(type, json).apply()
        } catch (ex: Exception) {
            Log.d(CrowdinPreferences::class.java.simpleName, ex.message ?: "Error saving data")
        }
    }

    override fun <T> getData(type: String, classType: Type): T? {
        val json = sharedPreferences.getString(type, null)
        json?.let {
            return Gson().fromJson(json, classType)
        }

        return null
    }
}

internal interface Preferences {

    fun setString(key: String, value: String)

    fun getString(key: String): String?
    fun setLastUpdate(lastUpdate: Long)
    fun getLastUpdate(): Long
    fun saveData(type: String, data: Any?)
    fun <T> getData(type: String, classType: Type): T?
}
