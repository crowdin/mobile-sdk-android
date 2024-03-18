package com.crowdin.platform.data.remote.api

import com.crowdin.platform.data.model.ManifestData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

internal interface CrowdinDistributionApi {

    @GET("/{distributionHash}{filePath}")
    fun getResourceFile(
        @Header("if-none-match") eTag: String,
        @Path("distributionHash") distributionHash: String,
        @Path("filePath", encoded = true) filePath: String,
        @Query("timestamp") timeStamp: Long
    ): Call<ResponseBody>

    @GET("/{distributionHash}/manifest.json")
    fun getResourceManifest(
        @Path("distributionHash") distributionHash: String
    ): Call<ManifestData>

    @GET("/{distributionHash}{filePath}")
    fun getMappingFile(
        @Header("if-none-match") eTag: String,
        @Path("distributionHash") distributionHash: String,
        @Path("filePath", encoded = true) filePath: String
    ): Call<ResponseBody>
}
