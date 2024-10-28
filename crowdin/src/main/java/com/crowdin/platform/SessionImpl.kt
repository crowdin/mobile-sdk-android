package com.crowdin.platform

import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.model.AuthConfig
import com.crowdin.platform.data.model.AuthInfo
import com.crowdin.platform.data.model.AuthResponse
import com.crowdin.platform.data.model.RefreshToken
import com.crowdin.platform.data.remote.api.AuthApi
import com.crowdin.platform.util.executeIO
import retrofit2.Response

internal class SessionImpl(
    private val dataManager: DataManager,
    private val authApi: AuthApi
) : Session {

    override fun isAuthorized(): Boolean = dataManager.isAuthorized()

    override fun isTokenExpired(): Boolean = dataManager.isTokenExpired()

    override fun getAccessToken(): String? = dataManager.getAccessToken()

    override fun refreshToken(organizationName: String?, authConfig: AuthConfig?): Boolean {
        val refreshToken = dataManager.getRefreshToken()
        refreshToken ?: return false

        val clientId = authConfig?.clientId ?: ""
        val clientSecret = authConfig?.clientSecret ?: ""

        var response: Response<AuthResponse>? = null
        executeIO {
            response = authApi.getToken(
                RefreshToken(
                    "refresh_token",
                    clientId,
                    clientSecret,
                    refreshToken
                ),
                organizationName
            ).execute()
        }

        val authResponse = response?.body()
        authResponse?.let { saveToken(it) }

        return authResponse != null
    }

    override fun invalidate() {
        dataManager.saveData(DataManager.AUTH_INFO, null)
    }

    override fun saveToken(authResponse: AuthResponse) {
        dataManager.saveData(DataManager.AUTH_INFO, AuthInfo(authResponse))
    }
}
