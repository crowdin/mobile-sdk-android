package com.crowdin.platform.screenshot

import android.graphics.Bitmap
import android.util.Log
import com.crowdin.platform.data.StringDataManager
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.ViewData
import com.crowdin.platform.data.remote.api.*
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection

// TODO: handle errors and add callback
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
        val tags = getMappingIds(mappingData, viewDataList)
        uploadScreenshot(bitmap, tags)
    }

    private fun uploadScreenshot(bitmap: Bitmap, tags: MutableList<TagData>) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        val requestBody = RequestBody.create(MediaType.parse("image/png"), byteArray)

        crowdinApi.uploadScreenshot(requestBody).enqueue(object : Callback<UploadScreenshotResponse> {

            override fun onResponse(call: Call<UploadScreenshotResponse>, response: Response<UploadScreenshotResponse>) {
                val responseBody = response.body()
                if (response.code() == HttpURLConnection.HTTP_CREATED) {
                    responseBody?.let {
                        it.data?.id?.let { screenId -> createScreenshot(screenId, tags) }
                    }
                }
            }

            override fun onFailure(call: Call<UploadScreenshotResponse>, throwable: Throwable) {
                Log.d("TAG", "uploadScreenshot onFailure")
            }
        })
    }

    private fun createScreenshot(id: Int, tags: MutableList<TagData>) {
        val requestBody = CreateScreenshotRequestBody(id, "${System.currentTimeMillis()}_image")
        crowdinApi.createScreenshot(requestBody).enqueue(object : Callback<CreateScreenshotResponse> {

            override fun onResponse(call: Call<CreateScreenshotResponse>, response: Response<CreateScreenshotResponse>) {
                val responseBody = response.body()
                if (response.code() == HttpURLConnection.HTTP_CREATED) {
                    responseBody?.let {
                        it.data.id?.let { screenId -> createTag(screenId, tags) }
                    }
                }
            }

            override fun onFailure(call: Call<CreateScreenshotResponse>, throwable: Throwable) {
                Log.d("TAG", "createScreenshot onFailure")
            }
        })
    }

    private fun createTag(screenId: Int, tags: MutableList<TagData>) {
        crowdinApi.createTag(screenId, tags).enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                // success
            }

            override fun onFailure(call: Call<ResponseBody>, throwable: Throwable) {
                Log.d("TAG", "createTag onFailure")
            }
        })
    }

    private fun getMappingIds(mappingData: LanguageData, viewDataList: List<ViewData>): MutableList<TagData> {
        val list = mutableListOf<TagData>()

        for (viewData in viewDataList) {
            val resKey = viewData.resourceKey
            val mappingValue = getMappingValueForKey(resKey, mappingData)
            mappingValue?.let {
                list.add(TagData(it.toInt(),
                        Position(viewData.x, viewData.y, viewData.width, viewData.height)))
            }
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