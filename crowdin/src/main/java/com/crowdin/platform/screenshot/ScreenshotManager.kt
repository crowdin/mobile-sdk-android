package com.crowdin.platform.screenshot

import android.graphics.Bitmap
import com.crowdin.platform.data.StringDataManager
import com.crowdin.platform.data.getMappingValueForKey
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

internal object ScreenshotManager {

    private const val MEDIA_TYPE_IMG = "image/png"
    private const val IMG_QUALITY = 100
    private lateinit var crowdinApi: CrowdinApi
    private lateinit var bitmap: Bitmap
    private lateinit var stringDataManager: StringDataManager
    private lateinit var sourceLanguage: String
    private lateinit var viewDataList: List<ViewData>
    private var screenshotCallback: ScreenshotCallback? = null

    operator fun invoke(crowdinApi: CrowdinApi,
                        bitmap: Bitmap,
                        stringDataManager: StringDataManager,
                        viewDataList: List<ViewData>,
                        sourceLanguage: String,
                        screenshotCallback: ScreenshotCallback?) {
        this.crowdinApi = crowdinApi
        this.bitmap = bitmap
        this.stringDataManager = stringDataManager
        this.viewDataList = viewDataList
        this.sourceLanguage = sourceLanguage
        this.screenshotCallback = screenshotCallback
    }

    fun sendScreenshot() {
        val mappingData = stringDataManager.getMapping(sourceLanguage) ?: return
        val tags = getMappingIds(mappingData, viewDataList)
        uploadScreenshot(bitmap, tags)
    }

    private fun uploadScreenshot(bitmap: Bitmap, tags: MutableList<TagData>) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, IMG_QUALITY, stream)
        val byteArray = stream.toByteArray()
        val requestBody = RequestBody.create(MediaType.parse(MEDIA_TYPE_IMG), byteArray)

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
                screenshotCallback?.onFailure(throwable.localizedMessage)
            }
        })
    }

    private fun createScreenshot(id: Int, tags: MutableList<TagData>) {
        val requestBody = CreateScreenshotRequestBody(id, System.currentTimeMillis().toString())
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
                screenshotCallback?.onFailure(throwable.localizedMessage)
            }
        })
    }

    private fun createTag(screenId: Int, tags: MutableList<TagData>) {
        crowdinApi.createTag(screenId, tags).enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                screenshotCallback?.onSuccess()
            }

            override fun onFailure(call: Call<ResponseBody>, throwable: Throwable) {
                screenshotCallback?.onFailure(throwable.localizedMessage)
            }
        })
    }

    private fun getMappingIds(mappingData: LanguageData, viewDataList: List<ViewData>): MutableList<TagData> {
        val list = mutableListOf<TagData>()

        for (viewData in viewDataList) {
            val mappingValue = getMappingValueForKey(viewData.textMetaData, mappingData)
            mappingValue?.let {
                list.add(TagData(it.toInt(),
                        TagData.Position(viewData.x, viewData.y, viewData.width, viewData.height)))
            }
        }

        return list
    }
}