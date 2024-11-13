package com.crowdin.platform.screenshot

import android.content.Context
import android.database.ContentObserver
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.crowdin.platform.Crowdin.CROWDIN_TAG
import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.getMappingValueForKey
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.ListScreenshotsResponse
import com.crowdin.platform.data.model.Screenshot
import com.crowdin.platform.data.model.ViewData
import com.crowdin.platform.data.remote.api.CreateScreenshotRequestBody
import com.crowdin.platform.data.remote.api.CreateScreenshotResponse
import com.crowdin.platform.data.remote.api.CrowdinApi
import com.crowdin.platform.data.remote.api.Data
import com.crowdin.platform.data.remote.api.DistributionInfoResponse
import com.crowdin.platform.data.remote.api.TagData
import com.crowdin.platform.data.remote.api.UploadScreenshotResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

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

        // TODO: replace with names
        val name = "test1Screenshot$IMAGE_EXTENSION"
        // 1. Get List Screenshots by search query
        getScreenshotsList(
            projectId = projectId,
            name = name,
            onResult = { screenshot ->
                Log.d(CROWDIN_TAG, "Screenshot search result:  $screenshot")
                // 2. Add to storage
                addToStorage(
                    bitmap = bitmap,
                    tags = tags,
                    projectId = projectId,
                    name = name,
                    screenshot = screenshot,
                    onFailure = { screenshotCallback?.onFailure(it) },
                )
            },
        )
    }

    private fun getScreenshotsList(
        projectId: String,
        name: String,
        onResult: (Screenshot?) -> Unit,
    ) {
        crowdinApi
            .getScreenshotsList(projectId, name)
            .enqueue(
                object : Callback<ListScreenshotsResponse> {
                    override fun onResponse(
                        call: Call<ListScreenshotsResponse>,
                        response: Response<ListScreenshotsResponse>,
                    ) {
                        val responseBody = response.body()
                        val list = responseBody?.data

                        if (list != null && list.size > 1) {
                            Log.v(
                                CROWDIN_TAG,
                                "Encountered multiple screenshots with the same name; only one will be updated.",
                            )
                        }
                        onResult(list?.lastOrNull()?.data)
                    }

                    override fun onFailure(
                        call: Call<ListScreenshotsResponse>,
                        throwable: Throwable,
                    ) {
                        Log.d(CROWDIN_TAG, "List screenshots onFailure: $throwable")
                        onResult(null)
                    }
                },
            )
    }

    private fun addToStorage(
        bitmap: Bitmap,
        tags: MutableList<TagData>,
        projectId: String,
        name: String,
        screenshot: Screenshot?,
        onFailure: (Throwable) -> Unit,
    ) {
        addScreenshotToStorage(
            bitmap = bitmap,
            activityName = name,
            onResult = {
                it?.let {
                    // 3. Update or create screenshot
                    onScreenshotAddedToStorage(projectId, it, tags, screenshot, onFailure)
                } ?: onFailure(Throwable("Could not upload screenshot"))
            },
            onFailure = { onFailure(it) },
        )
    }

    private fun onScreenshotAddedToStorage(
        projectId: String,
        data: Data,
        tags: MutableList<TagData>,
        screenshot: Screenshot?,
        onFailure: (Throwable) -> Unit,
    ) {
        if (data.id == null || data.fileName.isEmpty()) {
            onFailure(Throwable("Could not create screenshot storage"))
            return
        }

        if (screenshot != null) {
            updateScreenshot(
                projectId = projectId,
                storageId = data.id!!,
                screenshotId = screenshot.id.toString(),
                fileName = screenshot.name,
                onSuccess = { replaceTags() }, // TODO: add replace tags
                onFailure = onFailure,
            )
        } else {
            createScreenshot(
                projectId = projectId,
                storageId = data.id!!,
                fileName = data.fileName,
                onSuccess = { createTag(it, tags, projectId) },
                onFailure = onFailure,
            )
        }
    }

    private fun replaceTags() {
        //  4. після апдейту, викликаємо метод Replace Tags і передаємо нові координати текстів на тому скріні
//        createTag(screenshotId, tags, projectId)
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

    private fun addScreenshotToStorage(
        bitmap: Bitmap,
        activityName: String,
        onResult: (Data?) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, IMG_QUALITY, stream)
        val byteArray = stream.toByteArray()
        val requestBody =
            byteArray.toRequestBody(MEDIA_TYPE_IMG.toMediaTypeOrNull(), 0, byteArray.size)
        bitmap.recycle()

        val prefix = activityName?.let { it + "_" } ?: ""
        val fileName = activityName // + IMAGE_EXTENSION
//            "$prefix${System.currentTimeMillis().parseToDateTimeFormat()}$IMAGE_EXTENSION"
        crowdinApi
            .addToStorage(fileName, requestBody)
            .enqueue(
                object : Callback<UploadScreenshotResponse> {
                    override fun onResponse(
                        call: Call<UploadScreenshotResponse>,
                        response: Response<UploadScreenshotResponse>,
                    ) {
                        val responseBody = response.body()
                        onResult(responseBody?.data)
                    }

                    override fun onFailure(
                        call: Call<UploadScreenshotResponse>,
                        throwable: Throwable,
                    ) {
                        onFailure(throwable)
                    }
                },
            )
    }

    private fun updateScreenshot(
        projectId: String,
        storageId: Long,
        screenshotId: String,
        fileName: String,
        onSuccess: (Long) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        val requestBody = CreateScreenshotRequestBody(storageId, fileName)
        crowdinApi
            .updateScreenshot(projectId, screenshotId, requestBody)
            .enqueue(
                object : Callback<CreateScreenshotResponse> {
                    override fun onResponse(
                        call: Call<CreateScreenshotResponse>,
                        response: Response<CreateScreenshotResponse>,
                    ) {
                        val responseBody = response.body()
                        responseBody?.data?.id?.let { onSuccess(it) } ?: onFailure(Throwable("Could not update screenshot"))
                    }

                    override fun onFailure(
                        call: Call<CreateScreenshotResponse>,
                        throwable: Throwable,
                    ) {
                        onFailure(throwable)
                    }
                },
            )
    }

    private fun createScreenshot(
        storageId: Long,
        projectId: String,
        fileName: String,
        onSuccess: (Long) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        val requestBody = CreateScreenshotRequestBody(storageId, fileName)
        crowdinApi
            .createScreenshot(projectId, requestBody)
            .enqueue(
                object : Callback<CreateScreenshotResponse> {
                    override fun onResponse(
                        call: Call<CreateScreenshotResponse>,
                        response: Response<CreateScreenshotResponse>,
                    ) {
                        val responseBody = response.body()
                        responseBody?.data?.id?.let { onSuccess(it) } ?: onFailure(Throwable("Could not create screenshot"))
                    }

                    override fun onFailure(
                        call: Call<CreateScreenshotResponse>,
                        throwable: Throwable,
                    ) {
                        onFailure(throwable)
                    }
                },
            )
    }

    private fun createTag(
        screenshotId: Long,
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
