package com.crowdin.platform.data.model

internal class AuthInfo(authResponse: AuthResponse) {

    var accessToken = authResponse.accessToken
    var refreshToken = authResponse.refreshToken
    var expiresIn = System.currentTimeMillis() + (authResponse.expiresIn * 60)

    fun isExpired(): Boolean {
        return System.currentTimeMillis() > expiresIn
    }
}
