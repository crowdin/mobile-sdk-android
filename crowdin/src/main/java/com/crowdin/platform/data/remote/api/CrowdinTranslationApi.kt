package com.crowdin.platform.data.remote.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

internal interface CrowdinTranslationApi {
    @GET
    fun getTranslationResource(
        @Url url: String,
    ): Call<ResponseBody>
}
