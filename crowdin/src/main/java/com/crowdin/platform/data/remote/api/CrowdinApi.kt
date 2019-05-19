package com.crowdin.platform.data.remote.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

internal interface CrowdinApi {

    @Headers("Content-Type: image/png",
            "Authorization: Basic YXBpLXRlc3RlcjpWbXBGcVR5WFBxM2ViQXlOa3NVeEh3aEM=")
    @POST("api/v2/storages?login=MykhailoNN&account-key=58f81c7c7abc50cec98bfcb7bf030279")
    fun uploadScreenshot(
            @Body requestBody: RequestBody): Call<UploadScreenshotResponse>

    @Headers("Authorization: Basic YXBpLXRlc3RlcjpWbXBGcVR5WFBxM2ViQXlOa3NVeEh3aEM=")
    @POST("api/v2/projects/{projectId}/screenshots?login=MykhailoNN&account-key=58f81c7c7abc50cec98bfcb7bf030279")
    fun createScreenshot(
            @Path("projectId") projectId: String,
            @Body requestBody: CreateScreenshotRequestBody): Call<CreateScreenshotResponse>

    @Headers("Authorization: Basic YXBpLXRlc3RlcjpWbXBGcVR5WFBxM2ViQXlOa3NVeEh3aEM=")
    @POST("api/v2/projects/{projectId}/screenshots/{screenshotId}/tags?login=MykhailoNN&account-key=58f81c7c7abc50cec98bfcb7bf030279")
    fun createTag(
            @Path("projectId") projectId: String,
            @Path("screenshotId") screenshotId: Int,
            @Body tags: MutableList<TagData>): Call<ResponseBody>


    @GET("backend/distributions/get_info")
    fun getInfo(
            @Header("User-Agent") agent: String,
            @Header("Cookie") cookie: String,
            @Header("x-csrf-token") xCsrfToken: String,
            @Query("distribution_hash") distributionKey: String?): Call<DistributionInfoResponse>
}