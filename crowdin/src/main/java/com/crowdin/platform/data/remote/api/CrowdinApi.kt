package com.crowdin.platform.data.remote.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

internal interface CrowdinApi {

    @Headers("Content-Type: application/json")
    @POST("/storages?login=MykhailoNN&account-key=58f81c7c7abc50cec98bfcb7bf030279")
    fun uploadScreenshot(@Header("csrf_token") csrfToken: String,
                         @Body byteArray: ByteArray): Call<ResponseBody>
}