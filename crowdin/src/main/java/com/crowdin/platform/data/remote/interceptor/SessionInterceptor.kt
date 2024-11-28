package com.crowdin.platform.data.remote.interceptor

import com.crowdin.platform.Crowdin
import com.crowdin.platform.Session
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.HttpURLConnection

internal class SessionInterceptor(
    private val session: Session,
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        val apiToken = Crowdin.getApiAuthConfig()?.apiToken

        // Update expired token
        if (apiToken == null && session.isTokenExpired()) {
            val isRefreshed = refreshToken()
            if (!isRefreshed) {
                session.invalidate()
            }
        }

        // Original request.
        val request = addHeaderToRequest(original, getAccessToken())
        var mainResponse = chain.proceed(request)

        // Token can be revoked remotely. Should refresh token and retry silently.
        if (apiToken == null && isAuthErrorCode(mainResponse)) {
            val isRefreshed = refreshToken()
            if (!isRefreshed) {
                session.invalidate()
            } else {
                // retry original request
                val requestUpdated = addHeaderToRequest(original, getAccessToken())
                mainResponse.close()
                mainResponse = chain.proceed(requestUpdated)
                if (isAuthErrorCode(mainResponse)) {
                    session.invalidate()
                }
            }
        }
        return mainResponse
    }

    private fun refreshToken(): Boolean =
        try {
            session.refreshToken(Crowdin.getOrganizationName(), Crowdin.getAuthConfig())
        } catch (th: Throwable) {
            false
        }

    private fun addHeaderToRequest(
        original: Request,
        accessToken: String?,
    ): Request {
        val requestBuilder = original.newBuilder()
        accessToken ?: return requestBuilder.build()
        requestBuilder.header("Authorization", "Bearer $accessToken")
        return requestBuilder.build()
    }

    private fun getAccessToken(): String? = Crowdin.getApiAuthConfig()?.apiToken ?: session.getAccessToken()

    private fun isAuthErrorCode(response: Response): Boolean =
        response.code == HttpURLConnection.HTTP_UNAUTHORIZED || response.code == HttpURLConnection.HTTP_FORBIDDEN
}
