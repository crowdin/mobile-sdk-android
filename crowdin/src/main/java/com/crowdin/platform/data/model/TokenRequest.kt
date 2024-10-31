package com.crowdin.platform.data.model

import com.google.gson.annotations.SerializedName

internal data class TokenRequest(
    @SerializedName("grant_type")
    val grantType: String,
    @SerializedName("client_id")
    val clientId: String,
    @SerializedName("client_secret")
    val clientSecret: String,
    @SerializedName("redirect_uri")
    val redirectUri: String,
    val code: String,
)
