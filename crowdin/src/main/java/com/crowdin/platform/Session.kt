package com.crowdin.platform

import com.crowdin.platform.data.model.AuthResponse

internal interface Session {

    fun isAuthorized(): Boolean

    fun isTokenExpired(): Boolean

    fun getAccessToken(): String

    fun refreshToken(): Boolean

    /**
     * Executed on background thread.
     */
    fun invalidate()

    fun saveToken(authResponse: AuthResponse)
}
