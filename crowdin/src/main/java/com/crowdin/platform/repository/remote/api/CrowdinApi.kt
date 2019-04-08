package com.crowdin.platform.repository.remote.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

internal interface CrowdinApi {

    @GET("/{distributionHash}{filePath}")
    fun getFileUpdates(
            @Header("if-none-match") eTag: String,
            @Path("distributionHash") distributionHash: String,
            @Path("filePath") filePath: String): Call<ResponseBody>
}