package com.crowdin.platform.screenshot

import android.graphics.Bitmap
import android.util.Log
import com.crowdin.platform.data.StringDataManager
import com.crowdin.platform.data.model.*
import com.crowdin.platform.data.remote.api.CrowdinApi
import okhttp3.MediaType
import okhttp3.RequestBody
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
        val mappingIds = getMappingIDs(mappingData, viewDataList)

        uploadScreenshot(bitmap)
    }

    private fun uploadScreenshot(bitmap: Bitmap) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        val requestBody = RequestBody.create(MediaType.parse("image/png"), byteArray)

        crowdinApi.uploadScreenshot(requestBody).enqueue(object : Callback<UploadScreenshotResponse> {

            override fun onResponse(call: Call<UploadScreenshotResponse>, response: Response<UploadScreenshotResponse>) {
                val responseBody = response.body()
                when {
                    response.code() == HttpURLConnection.HTTP_CREATED -> {
                        responseBody?.let {
                            it.data?.id?.let { screenId -> createScreenshot(screenId) }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<UploadScreenshotResponse>, throwable: Throwable) {
                Log.d("TAG", "uploadScreenshot onFailure")
            }
        })
    }

    private fun createScreenshot(id: Int) {
        val requestBody = CreateScreenshotRequestBody(id, "image")
        crowdinApi.createScreenshot(requestBody).enqueue(object : Callback<CreateScreenshotResponse> {

            override fun onResponse(call: Call<CreateScreenshotResponse>, response: Response<CreateScreenshotResponse>) {
                val body = response.body().toString()
                when {
                    response.code() == HttpURLConnection.HTTP_CREATED -> {
                        // created screenshot
                    }
                }
            }

            override fun onFailure(call: Call<CreateScreenshotResponse>, throwable: Throwable) {
                Log.d("TAG", "createScreenshot onFailure")
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