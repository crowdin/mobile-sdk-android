package com.crowdin.platform.data.remote.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

internal interface CrowdinApi {

    @GET("/{distributionHash}/content{filePath}")
    fun getResourceFile(
            @Header("if-none-match") eTag: String,
            @Path("distributionHash") distributionHash: String,
            @Path("filePath") filePath: String): Call<ResponseBody>

    @GET("/{distributionHash}/mapping{filePath}")
    fun getMappingFile(
            @Header("if-none-match") eTag: String,
            @Path("distributionHash") distributionHash: String,
            @Path("filePath") filePath: String): Call<ResponseBody>
}