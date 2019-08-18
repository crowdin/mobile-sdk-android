package com.crowdin.platform.data.remote

import com.crowdin.platform.data.model.AuthResponse

interface Session {

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
