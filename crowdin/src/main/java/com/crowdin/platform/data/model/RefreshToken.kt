package com.crowdin.platform.data.model

internal data class RefreshToken(
        val grant_type: String,
        val client_id: String,
        val client_secret: String,
        val refresh_token: String)