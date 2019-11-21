package com.crowdin.platform.data.model

data class AuthConfig(
    val clientId: String,
    val clientSecret: String,
    val organizationName: String? = null
)
