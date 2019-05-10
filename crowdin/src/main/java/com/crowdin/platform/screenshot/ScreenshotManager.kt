package com.crowdin.platform.screenshot

import android.graphics.Bitmap
import android.util.Log
import com.crowdin.platform.data.StringDataManager
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.ViewData
import com.crowdin.platform.data.remote.api.CrowdinApi
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection


internal object ScreenshotManager {

    private lateinit var crowdinApi: CrowdinApi
    private lateinit var bitmap: Bitmap
    private lateinit var stringDataManager: StringDataManager
    private lateinit var viewDataList: List<ViewData>

    operator fun invoke(crowdinApi: CrowdinApi, bitmap: Bitmap, stringDataManager: StringDataManager, viewDataList: List<ViewData>) {
        this.crowdinApi = crowdinApi
        this.bitmap = bitmap
        this.stringDataManager = stringDataManager
        this.viewDataList = viewDataList
    }

    fun sendScreenshot() {
        val mappingData = stringDataManager.getMapping() ?: return
        val csrfToken = stringDataManager.getCookies() ?: return
        val mappingIds = getMappingIDs(mappingData, viewDataList)

        uploadScreenshot(csrfToken, bitmap)
    }

    private fun uploadScreenshot(csrfToken: String, bitmap: Bitmap) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        crowdinApi.uploadScreenshot(csrfToken, byteArray).enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val body = response.body()
                when {
                    response.code() == HttpURLConnection.HTTP_OK && body != null -> {
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, throwable: Throwable) {
                Log.d("TAG", "onFailure")
            }
        })
    }

    private fun getMappingIDs(mappingData: LanguageData, viewDataList: List<ViewData>): Any {
        val list = mutableListOf<String>()

        for (viewData in viewDataList) {
            val resKey = viewData.resourceKey
            val mappingValue = getMappingValueForKey(resKey, mappingData)
            mappingValue?.let { list.add(it) }
        }

        return list
    }

    private fun getMappingValueForKey(resKey: String, mappingData: LanguageData): String? {
        val resources = mappingData.resources
        val arrays = mappingData.arrays
        val plurals = mappingData.plurals

        for (resource in resources) {
            if (resource.stringKey == resKey) {
                return resource.stringValue
            }
        }

        // TODO: array plural, nothing to compare. Check ViewData to extend
        for (array in arrays) {
        }
        for (plural in plurals) {
        }

        return null
    }
}