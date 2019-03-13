package com.crowdin.platform.repository.remote.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

internal interface CrowdinApi {

    @get:GET("/test")
    val value: Call<ResponseBody>

    @POST("/api/auth")
    fun authorization(@Body body: AuthRequestBody): ResponseBody
}