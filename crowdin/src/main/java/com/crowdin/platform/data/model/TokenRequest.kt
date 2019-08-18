package com.crowdin.platform.data.model

internal data class TokenRequest(
        val grant_type: String,
        val client_id: String,
        val client_secret: String,
        val redirect_uri: String,
        val code: String)