package com.crowdin.platform.data.remote.api

import com.crowdin.platform.data.model.AuthResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

internal interface AuthApi {

    @POST("oauth/token")
    fun getToken(@Body tokenRequest: Any,
                 @Query("domain") domain: String?): Call<AuthResponse>
}