package com.crowdin.platform

import android.util.Log
import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.model.AuthInfo
import com.crowdin.platform.data.model.AuthResponse
import com.crowdin.platform.data.model.RefreshToken
import com.crowdin.platform.data.remote.api.AuthApi

internal class SessionImpl(private val dataManager: DataManager,
                           private val authApi: AuthApi) : Session {

    override fun isAuthorized(): Boolean = dataManager.isAuthorized()

    override fun isTokenExpired(): Boolean = dataManager.isTokenExpired()

    override fun getAccessToken(): String = dataManager.getAccessToken()

    override fun refreshToken(): Boolean {
        val refreshToken = dataManager.getRefreshToken()
        val response = authApi.getToken(RefreshToken("refresh_token", BuildConfig.CLIENT_ID,
                BuildConfig.CLIENT_SECRET, refreshToken)).execute()
        val authResponse = response.body()
        authResponse?.let { saveToken(it) }

        return authResponse != null
    }

    override fun invalidate() {
        Log.e(SessionImpl::class.java.simpleName, "Token expired. Refresh silent failed.")
    }

    override fun saveToken(authResponse: AuthResponse) {
        Crowdin.saveAuthInfo(AuthInfo(authResponse))
    }
}