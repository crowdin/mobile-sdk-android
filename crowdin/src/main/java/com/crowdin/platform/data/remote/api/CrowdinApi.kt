package com.crowdin.platform.data.remote.api

import com.crowdin.platform.data.model.CreateScreenshotRequestBody
import com.crowdin.platform.data.model.CreateScreenshotResponse
import com.crowdin.platform.data.model.UploadScreenshotResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

internal interface CrowdinApi {

    @Headers("Content-Type: image/png",
            "Authorization: Basic YXBpLXRlc3RlcjpWbXBGcVR5WFBxM2ViQXlOa3NVeEh3aEM=")
    @POST("storages?login=MykhailoNN&account-key=58f81c7c7abc50cec98bfcb7bf030279")
    fun uploadScreenshot(
            @Body requestBody: RequestBody): Call<UploadScreenshotResponse>

    @Headers("Authorization: Basic YXBpLXRlc3RlcjpWbXBGcVR5WFBxM2ViQXlOa3NVeEh3aEM=")
    @POST("projects/352187/screenshots?login=MykhailoNN&account-key=58f81c7c7abc50cec98bfcb7bf030279")
    fun createScreenshot(
            @Body requestBody: CreateScreenshotRequestBody): Call<CreateScreenshotResponse>
}