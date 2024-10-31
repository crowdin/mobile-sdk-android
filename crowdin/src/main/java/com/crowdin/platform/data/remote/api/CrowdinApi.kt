package com.crowdin.platform.data.remote.api

import com.crowdin.platform.data.model.BuildTranslationRequest
import com.crowdin.platform.data.model.FileResponse
import com.crowdin.platform.data.model.LanguagesInfo
import com.crowdin.platform.data.model.TranslationResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

private const val LANGUAGE_COUNT = 500

internal interface CrowdinApi {
    @POST("api/v2/storages")
    fun uploadScreenshot(
        @Header("Crowdin-API-FileName") fileName: String,
        @Body requestBody: RequestBody,
    ): Call<UploadScreenshotResponse>

    @POST("api/v2/projects/{projectId}/screenshots")
    fun createScreenshot(
        @Path("projectId") projectId: String,
        @Body requestBody: CreateScreenshotRequestBody,
    ): Call<CreateScreenshotResponse>

    @POST("api/v2/projects/{projectId}/screenshots/{screenshotId}/tags")
    fun createTag(
        @Path("projectId") projectId: String,
        @Path("screenshotId") screenshotId: Int,
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

    @GET("/api/v2/languages")
    fun getLanguagesInfo(
        @Query("limit") limit: Int = LANGUAGE_COUNT,
    ): Call<LanguagesInfo>
}
