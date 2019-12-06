package com.crowdin.platform

import android.content.Context
import android.content.SharedPreferences

private const val SHARED_PREF_NAME = "com.crowdin.platform.string.preferences"

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
}

internal interface Preferences {

    fun setString(key: String, value: String)

    fun getString(key: String): String?
}
