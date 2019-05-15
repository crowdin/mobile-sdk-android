package com.crowdin.platform.data.model

internal data class AuthInfo(var userAgent: String,
                             var cookies: String,
                             var xCsrfToken: String)