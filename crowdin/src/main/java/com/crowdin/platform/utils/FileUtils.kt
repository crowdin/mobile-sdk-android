package com.crowdin.platform.utils

import android.content.Context
import com.crowdin.platform.api.ResourcesResponse
import com.google.gson.Gson

internal object FileUtils {

    fun getJsonFromApi(context: Context?): ResourcesResponse? {
        if (context == null) return null

        val json: String
        val data: ResourcesResponse? = null
        return try {
            val inputStream = context.assets.open("crowdin_api.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer)

            Gson().fromJson(json, ResourcesResponse::class.java)

        } catch (e: Throwable) {
            data
        }
    }
}
