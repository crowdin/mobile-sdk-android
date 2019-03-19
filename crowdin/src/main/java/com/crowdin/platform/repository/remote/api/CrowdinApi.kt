package com.crowdin.platform.repository.remote.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

internal interface CrowdinApi {

    @GET("/{distributionHash}/{locale}/{filePath}")
    fun getFileUpdates(@Path("distributionHash") distributionHash: String?,
                       @Path("locale") locale: String,
                       @Path("filePath") filePath: String): Call<ResponseBody>
}