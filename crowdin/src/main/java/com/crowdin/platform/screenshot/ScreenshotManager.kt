package com.crowdin.platform.screenshot

import android.content.Context
import android.database.ContentObserver
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Toast
import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.getMappingValueForKey
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.ViewData
import com.crowdin.platform.data.remote.api.CreateScreenshotRequestBody
import com.crowdin.platform.data.remote.api.CreateScreenshotResponse
import com.crowdin.platform.data.remote.api.CrowdinApi
import com.crowdin.platform.data.remote.api.DistributionInfoResponse
import com.crowdin.platform.data.remote.api.TagData
import com.crowdin.platform.data.remote.api.UploadScreenshotResponse
import com.crowdin.platform.util.parseToDateTimeFormat
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection

internal class ScreenshotManager(
    private var crowdinApi: CrowdinApi,
    private var dataManager: DataManager,
    private var sourceLanguage: String,
) {
    companion object {
        private const val MEDIA_TYPE_IMG = "image/png"
        private const val IMG_QUALITY = 100
        private const val IMAGE_EXTENSION = ".png"
    }

    private var screenshotCallback: ScreenshotCallback? = null
    private var contentObserver: ContentObserver? = null

    fun sendScreenshot(
        bitmap: Bitmap,
        viewDataList: MutableList<ViewData>,
        activityName: String? = null,
    ) {
        val mappingData = dataManager.getMapping(sourceLanguage) ?: return
        val distributionData =
            dataManager.getData<DistributionInfoResponse.DistributionData>(
                DataManager.DISTRIBUTION_DATA,
                DistributionInfoResponse.DistributionData::class.java,
            )

        if (distributionData == null) {
            screenshotCallback?.onFailure(Throwable("Could not send screenshot: not authorized"))
            return
        }

        val projectId = distributionData.project.id
        val tags = getMappingIds(mappingData, viewDataList)
        uploadScreenshot(bitmap, tags, projectId, activityName)
    }

    fun registerScreenShotContentObserver(context: Context) {
        val screenshotService = ScreenshotService(context)
        screenshotService.setOnErrorListener {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }

        contentObserver = screenshotService
        contentObserver?.let {
            context.contentResolver.registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true,
                it,
            )
        }
    }

    fun unregisterScreenShotContentObserver(context: Context) {
        contentObserver?.let { context.contentResolver.unregisterContentObserver(it) }
    }

    fun setScreenshotCallback(screenshotCallback: ScreenshotCallback?) {
        this.screenshotCallback = screenshotCallback
    }

    private fun uploadScreenshot(
        bitmap: Bitmap,
        tags: MutableList<TagData>,
        projectId: String,
        activityName: String?,
    ) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, IMG_QUALITY, stream)
        val byteArray = stream.toByteArray()
        val requestBody =
            byteArray.toRequestBody(MEDIA_TYPE_IMG.toMediaTypeOrNull(), 0, byteArray.size)
        bitmap.recycle()

        val prefix = activityName?.let { it + "_" } ?: ""
        val fileName =
            "$prefix${System.currentTimeMillis().parseToDateTimeFormat()}$IMAGE_EXTENSION"
        crowdinApi
            .uploadScreenshot(fileName, requestBody)
            .enqueue(
                object : Callback<UploadScreenshotResponse> {
                    override fun onResponse(
                        call: Call<UploadScreenshotResponse>,
                        response: Response<UploadScreenshotResponse>,
                    ) {
                        val responseBody = response.body()
                        if (response.code() == HttpURLConnection.HTTP_CREATED) {
                            responseBody?.data?.let { data ->
                                data.id?.let { createScreenshot(it, tags, projectId, data.fileName) }
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<UploadScreenshotResponse>,
                        throwable: Throwable,
                    ) {
                        screenshotCallback?.onFailure(throwable)
                    }
                },
            )
    }

    private fun createScreenshot(
        screenshotId: Int,
        tags: MutableList<TagData>,
        projectId: String,
        fileName: String,
    ) {
        val requestBody = CreateScreenshotRequestBody(screenshotId, fileName)
        crowdinApi
            .createScreenshot(projectId, requestBody)
            .enqueue(
                object : Callback<CreateScreenshotResponse> {
                    override fun onResponse(
                        call: Call<CreateScreenshotResponse>,
                        response: Response<CreateScreenshotResponse>,
                    ) {
                        val responseBody = response.body()
                        if (response.code() == HttpURLConnection.HTTP_CREATED) {
                            responseBody?.data?.id?.let { screenshotId ->
                                createTag(screenshotId, tags, projectId)
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<CreateScreenshotResponse>,
                        throwable: Throwable,
                    ) {
                        screenshotCallback?.onFailure(throwable)
                    }
                },
            )
    }

    private fun createTag(
        screenshotId: Int,
        tags: MutableList<TagData>,
        projectId: String,
    ) {
        crowdinApi
            .createTag(projectId, screenshotId, tags)
            .enqueue(
                object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>,
                    ) {
                        screenshotCallback?.onSuccess()
                    }

                    override fun onFailure(
                        call: Call<ResponseBody>,
                        throwable: Throwable,
                    ) {
                        screenshotCallback?.onFailure(throwable)
                    }
                },
            )
    }

    private fun getMappingIds(
        mappingData: LanguageData,
        viewDataList: List<ViewData>,
    ): MutableList<TagData> {
        val list = mutableListOf<TagData>()
        for (viewData in viewDataList) {
            val mapping = getMappingValueForKey(viewData.textMetaData, mappingData)
            mapping.value?.let {
                list.add(
                    TagData(
                        it.toInt(),
                        TagData.Position(viewData.x, viewData.y, viewData.width, viewData.height),
                    ),
                )
            }
        }

        return list
    }
}
