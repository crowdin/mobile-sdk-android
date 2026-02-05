package com.crowdin.platform.data.remote.api

import com.crowdin.platform.data.model.BuildTranslationRequest
import com.crowdin.platform.data.model.FileResponse
import com.crowdin.platform.data.model.ListScreenshotsResponse
import com.crowdin.platform.data.model.TicketRequestBody
import com.crowdin.platform.data.model.TicketResponseBody
import com.crowdin.platform.data.model.TranslationResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

internal interface CrowdinApi {
    @GET("/api/v2/projects/{projectId}/screenshots")
    fun getScreenshotsList(
        @Path("projectId") projectId: String,
        @Query("search") search: String,
    ): Call<ListScreenshotsResponse>

    @POST("api/v2/storages")
    fun addToStorage(
        @Header("Crowdin-API-FileName") fileName: String,
        @Body requestBody: RequestBody,
    ): Call<UploadScreenshotResponse>

    @POST("api/v2/projects/{projectId}/screenshots")
    fun createScreenshot(
        @Path("projectId") projectId: String,
        @Body requestBody: CreateScreenshotRequestBody,
    ): Call<CreateScreenshotResponse>

    @PUT("api/v2/projects/{projectId}/screenshots/{screenshotId}")
    fun updateScreenshot(
        @Path("projectId") projectId: String,
        @Path("screenshotId") screenshotId: String,
        @Body requestBody: CreateScreenshotRequestBody,
    ): Call<CreateScreenshotResponse>

    @POST("api/v2/projects/{projectId}/screenshots/{screenshotId}/tags")
    fun createTag(
        @Path("projectId") projectId: String,
        @Path("screenshotId") screenshotId: Long,
        @Body tags: MutableList<TagData>,
    ): Call<ResponseBody>

    @PUT("api/v2/projects/{projectId}/screenshots/{screenshotId}/tags")
    fun replaceTag(
        @Path("projectId") projectId: String,
        @Path("screenshotId") screenshotId: Long,
        @Body tags: MutableList<TagData>,
    ): Call<ResponseBody>

    @GET("/api/v2/distributions/metadata")
    fun getInfo(
        @Query("hash") distributionHash: String,
    ): Call<DistributionInfoResponse>

    @POST("/api/v2/projects/{projectId}/translations/builds/files/{fileId}")
    fun getTranslation(
        @Header("if-none-match") eTag: String,
        @Path("projectId") projectId: String,
        @Path("fileId") fileId: Long,
        @Body body: BuildTranslationRequest,
    ): Call<TranslationResponse>

    @GET("/api/v2/projects/{projectId}/files")
    fun getFiles(
        @Path("projectId") projectId: String,
    ): Call<FileResponse>

    @POST("/api/v2/user/websocket-ticket")
    fun getTicket(
        @Body body: TicketRequestBody,
    ): Call<TicketResponseBody>
}
