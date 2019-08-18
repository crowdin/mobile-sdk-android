package com.crowdin.platform.data.remote

import com.crowdin.platform.Session
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.HttpURLConnection

internal class SessionInterceptor(private val session: Session) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        // Update expired token
        if (session.isTokenExpired()) {
            val isRefreshed = refreshToken()
            if (!isRefreshed) {
                session.invalidate()
            }
        }

        // Original request.
        val request = addHeaderToRequest(original)
        var mainResponse = chain.proceed(request)

        // Token can be revoked remotely. Should refresh token and retry silently.
        if (isAuthErrorCode(mainResponse)) {
            val isRefreshed = refreshToken()
            if (!isRefreshed) {
                session.invalidate()
            } else {
                // retry original request
                val requestUpdated = addHeaderToRequest(original)
                mainResponse = chain.proceed(requestUpdated)
                if (isAuthErrorCode(mainResponse)) {
                    session.invalidate()
                }
            }
        }
        return mainResponse
    }

    private fun refreshToken(): Boolean {
        return try {
            session.refreshToken()
        } catch (th: Throwable) {
            false
        }
    }

    private fun addHeaderToRequest(original: Request): Request {
        val accessToken = session.getAccessToken()
        val requestBuilder = original.newBuilder().header("Authorization", "Bearer $accessToken")
        return requestBuilder.build()
    }

    private fun isAuthErrorCode(response: Response): Boolean {
        return response.code == HttpURLConnection.HTTP_UNAUTHORIZED ||
                response.code == HttpURLConnection.HTTP_FORBIDDEN
    }
}