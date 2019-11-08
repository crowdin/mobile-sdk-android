package com.crowdin.platform.data.model

import com.google.gson.annotations.SerializedName

internal data class RefreshToken(
    @SerializedName("grant_type")
    val grantType: String,
    @SerializedName("client_id")
    val clientId: String,
    @SerializedName("client_secret")
    val clientSecret: String,
    @SerializedName("refresh_token")
    val refreshToken: String
)