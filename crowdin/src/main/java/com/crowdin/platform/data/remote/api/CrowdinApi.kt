package com.crowdin.platform.data.remote.api

import com.crowdin.platform.data.model.AuthResponse
import com.crowdin.platform.data.model.TokenRequest
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

internal interface CrowdinApi {

    @Headers("Content-Type: image/png")
    @POST("api/v2/storages")
    fun uploadScreenshot(@Header("Authorization") accessToken: String,
                         @Body requestBody: RequestBody): Call<UploadScreenshotResponse>

    @POST("api/v2/projects/{projectId}/screenshots")
    fun createScreenshot(@Header("Authorization") accessToken: String,
                         @Path("projectId") projectId: String,
                         @Body requestBody: CreateScreenshotRequestBody): Call<CreateScreenshotResponse>

    @POST("api/v2/projects/{projectId}/screenshots/{screenshotId}/tags")
    fun createTag(@Header("Authorization") accessToken: String,
                  @Path("projectId") projectId: String,
                  @Path("screenshotId") screenshotId: Int,
                  @Body tags: MutableList<TagData>): Call<ResponseBody>

    @GET("/api/v2/distributions/metadata")
    fun getInfo(@Header("Authorization") accessToken: String,
                @Query("hash") distributionHash: String): Call<DistributionInfoResponse>

    @POST("oauth/token")
    fun getToken(@Body tokenRequest: TokenRequest): Call<AuthResponse>
}
