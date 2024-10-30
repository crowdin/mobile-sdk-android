package com.crowdin.platform

import com.crowdin.platform.data.model.AuthConfig
import com.crowdin.platform.data.model.AuthResponse

internal interface Session {
    fun isAuthorized(): Boolean

    fun isTokenExpired(): Boolean

    fun getAccessToken(): String?

    fun refreshToken(
        organizationName: String?,
        authConfig: AuthConfig?,
    ): Boolean

    /**
     * Executed on background thread.
     */
    fun invalidate()

    fun saveToken(authResponse: AuthResponse)
}
