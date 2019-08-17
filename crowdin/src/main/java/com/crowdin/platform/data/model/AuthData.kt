package com.crowdin.platform.data.model

internal class AuthInfo(authResponse: AuthResponse) {

    var accessToken = authResponse.accessToken
    var refreshToken = authResponse.refreshToken
    var expiresIn = authResponse.expiresIn
}
