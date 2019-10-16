package com.crowdin.platform

import com.crowdin.platform.data.remote.SessionInterceptor
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.Test
import org.mockito.Mockito.*

class SessionInterceptorTest {

    // TODO: update
    @Test
    fun intercept_whenTokenExpired_shouldRefreshToken() {
        // Given
        val session = mock(Session::class.java)
        val sessionInterceptor = SessionInterceptor(session)
        val chain = mock(Interceptor.Chain::class.java)
        `when`(session.isTokenExpired()).thenReturn(false)
//        `when`(session.refreshToken(any())).thenReturn(true)
        val request = mock(Request::class.java)
        `when`(chain.request()).thenReturn(request)
        `when`(session.getAccessToken()).thenReturn("testAccessToken")
        val builder = mock(Request.Builder::class.java)
        `when`(request.newBuilder()).thenReturn(builder)
        `when`(builder.build()).thenReturn(request)
        val response = mock(Response::class.java)
        `when`(chain.proceed(any())).thenReturn(response)

        // When
        sessionInterceptor.intercept(chain)

        // Then
        verify(session).isTokenExpired()
        verify(session).getAccessToken()
    }
}