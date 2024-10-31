package com.crowdin.platform.data.model

internal class AuthInfo(
    authResponse: AuthResponse,
) {
    var accessToken = authResponse.accessToken
    var refreshToken = authResponse.refreshToken
    private var expiresIn = System.currentTimeMillis() + (authResponse.expiresIn * 60)

    fun isExpired(): Boolean = System.currentTimeMillis() > expiresIn

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AuthInfo

        if (accessToken != other.accessToken) return false
        if (refreshToken != other.refreshToken) return false

        return true
    }

    override fun hashCode(): Int {
        var result = accessToken.hashCode()
        result = 31 * result + refreshToken.hashCode()
        return result
    }
}
