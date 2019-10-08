package com.crowdin.platform.data.model

open class AuthConfig(val clientId: String,
                               val clientSecret: String,
                               val organizationName: String? = null)