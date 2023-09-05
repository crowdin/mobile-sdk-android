package com.crowdin.platform

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.crowdin.platform.data.remote.interceptor.SessionInterceptor
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class SessionInterceptorTest {

    @Before
    fun setUp() {
        initCrowdin()
    }

    @Test
    fun intercept_whenIntercept_shouldCheckTokenExpiration() {
        // Given
        val session = givenMockSession()
        val sessionInterceptor = SessionInterceptor(session)
        val chain = givenMockChain()

        // When
        sessionInterceptor.intercept(chain)

        // Then
        verify(session).isTokenExpired()
    }

    @Test
    fun intercept_whenTokenExpired_shouldTryRefresh() {
        // Given
        val session = givenMockSession()
        `when`(session.isTokenExpired()).thenReturn(true)
        val sessionInterceptor = SessionInterceptor(session)
        val chain = givenMockChain()

        // When
        sessionInterceptor.intercept(chain)

        // Then
        verify(session).refreshToken(any(), any())
    }

    @Test
    fun intercept_whenTokenExpiredAndRefreshFailed_shouldInvalidate() {
        // Given
        val session = givenMockSession()
        `when`(session.isTokenExpired()).thenReturn(true)
        `when`(session.refreshToken(any(), any())).thenReturn(false)
        val sessionInterceptor = SessionInterceptor(session)
        val chain = givenMockChain()

        // When
        sessionInterceptor.intercept(chain)

        // Then
        verify(session).invalidate()
    }

    @Test
    fun intercept_whenAuthError_shouldTryToRefreshToken() {
        // Given
        initCrowdin()
        val session = givenMockSession()
        `when`(session.isTokenExpired()).thenReturn(false)
        `when`(session.refreshToken(any(), any())).thenReturn(false)
        val sessionInterceptor = SessionInterceptor(session)
        val chain = givenMockChain()
        givenFailResponse(chain)

        // When
        sessionInterceptor.intercept(chain)

        // Then
        verify(session).refreshToken(any(), any())
    }

    @Test
    fun intercept_whenAuthErrorAndRefreshFailed_shouldInvalidate() {
        // Given
        initCrowdin()
        val session = givenMockSession()
        `when`(session.isTokenExpired()).thenReturn(false)
        `when`(session.refreshToken(any(), any())).thenReturn(false)
        val sessionInterceptor = SessionInterceptor(session)
        val chain = givenMockChain()
        givenFailResponse(chain)

        // When
        sessionInterceptor.intercept(chain)

        // Then
        verify(session).invalidate()
    }

    @Test
    fun intercept_whenAuthErrorAndRefreshSuccess_shouldRetryRequestSilently() {
        // Given
        initCrowdin()
        val session = givenMockSession()
        `when`(session.isTokenExpired()).thenReturn(false)
        `when`(session.refreshToken(any(), any())).thenReturn(true)
        val sessionInterceptor = SessionInterceptor(session)
        val chain = givenMockChain()
        givenFailResponse(chain)

        // When
        sessionInterceptor.intercept(chain)

        // Then
        verify(session, times(2)).getAccessToken()
    }

    private fun initCrowdin() {
        val config = CrowdinConfig.Builder()
            .withDistributionHash("test")
            .build()
        val sharedPrefs = mock(SharedPreferences::class.java)!!
        val context = mock(Context::class.java)
        `when`(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs)
        val connectivityManager = mock(ConnectivityManager::class.java)
        `when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(
            connectivityManager
        )

        Crowdin.init(context, config)
    }

    private fun givenMockChain(): Interceptor.Chain {
        val chain = mock(Interceptor.Chain::class.java)
        val request = mock(Request::class.java)
        `when`(chain.request()).thenReturn(request)
        val builder = mock(Request.Builder::class.java)
        `when`(request.newBuilder()).thenReturn(builder)
        `when`(builder.build()).thenReturn(request)
        val response = mock(Response::class.java)
        `when`(chain.proceed(any())).thenReturn(response)

        return chain
    }

    private fun givenMockSession(): Session {
        val session = mock(Session::class.java)
        `when`(session.getAccessToken()).thenReturn("testAccessToken")
        return session
    }

    private fun givenFailResponse(chain: Interceptor.Chain) {
        val response = mock(Response::class.java)
        `when`(chain.proceed(any())).thenReturn(response)
        `when`(response.code).thenReturn(401)
    }
}
