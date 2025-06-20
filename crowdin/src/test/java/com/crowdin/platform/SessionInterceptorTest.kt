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
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

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

    @Test
    fun intercept_whenApiTokenIsEmpty_shouldUseSessionAuth() {
        // Given
        initCrowdinWithEmptyApiToken()
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
    fun intercept_whenApiTokenIsBlank_shouldUseSessionAuth() {
        // Given
        initCrowdinWithBlankApiToken()
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
    fun intercept_whenApiTokenIsWhitespace_shouldUseSessionAuth() {
        // Given
        initCrowdinWithWhitespaceApiToken()
        val session = givenMockSession()
        `when`(session.isTokenExpired()).thenReturn(true)
        val sessionInterceptor = SessionInterceptor(session)
        val chain = givenMockChain()

        // When
        sessionInterceptor.intercept(chain)

        // Then
        verify(session).refreshToken(any(), any())
    }

    private fun initCrowdin() {
        if (::mockContext.isInitialized.not()) {
            initMockData()
        }
        val crowdinConfig =
            CrowdinConfig.Builder()
                .withDistributionHash("hash")
                .withSourceLanguage("en")
                .build()
        Crowdin.init(mockContext, crowdinConfig)
    }

    private fun initCrowdinWithEmptyApiToken() {
        if (::mockContext.isInitialized.not()) {
            initMockData()
        }
        val crowdinConfig =
            CrowdinConfig.Builder()
                .withDistributionHash("hash")
                .withSourceLanguage("en")
                .withApiAuthConfig(com.crowdin.platform.data.model.ApiAuthConfig("valid_token"))
                .build()
        
        // Mock the ApiAuthConfig to return empty string
        val mockApiAuthConfig = mock(com.crowdin.platform.data.model.ApiAuthConfig::class.java)
        `when`(mockApiAuthConfig.apiToken).thenReturn("")
        crowdinConfig.apiAuthConfig = mockApiAuthConfig
        
        Crowdin.init(mockContext, crowdinConfig)
    }

    private fun initCrowdinWithBlankApiToken() {
        if (::mockContext.isInitialized.not()) {
            initMockData()
        }
        val crowdinConfig =
            CrowdinConfig.Builder()
                .withDistributionHash("hash")
                .withSourceLanguage("en")
                .withApiAuthConfig(com.crowdin.platform.data.model.ApiAuthConfig("valid_token"))
                .build()
        
        // Mock the ApiAuthConfig to return blank string
        val mockApiAuthConfig = mock(com.crowdin.platform.data.model.ApiAuthConfig::class.java)
        `when`(mockApiAuthConfig.apiToken).thenReturn("   ")
        crowdinConfig.apiAuthConfig = mockApiAuthConfig
        
        Crowdin.init(mockContext, crowdinConfig)
    }

    private fun initCrowdinWithWhitespaceApiToken() {
        if (::mockContext.isInitialized.not()) {
            initMockData()
        }
        val crowdinConfig =
            CrowdinConfig.Builder()
                .withDistributionHash("hash")
                .withSourceLanguage("en")
                .withApiAuthConfig(com.crowdin.platform.data.model.ApiAuthConfig("valid_token"))
                .build()
        
        // Mock the ApiAuthConfig to return whitespace string
        val mockApiAuthConfig = mock(com.crowdin.platform.data.model.ApiAuthConfig::class.java)
        `when`(mockApiAuthConfig.apiToken).thenReturn("\t\n ")
        crowdinConfig.apiAuthConfig = mockApiAuthConfig
        
        Crowdin.init(mockContext, crowdinConfig)
    }

    private lateinit var mockContext: Context
    private lateinit var mockSharedPreferences: SharedPreferences
    private lateinit var mockConnectivityManager: ConnectivityManager

    private fun initMockData() {
        mockContext = mock(Context::class.java)
        mockSharedPreferences = mock(SharedPreferences::class.java)
        mockConnectivityManager = mock(ConnectivityManager::class.java)

        `when`(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences)
        `when`(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager)
        `when`(mockSharedPreferences.edit()).thenReturn(mock(SharedPreferences.Editor::class.java))
    }

    private fun givenMockSession(): Session {
        val session = mock(Session::class.java)
        `when`(session.getAccessToken()).thenReturn("token")
        return session
    }

    private fun givenMockChain(): Interceptor.Chain {
        val chain = mock(Interceptor.Chain::class.java)
        val request = mock(Request::class.java)
        val response = mock(Response::class.java)
        val requestBuilder = mock(Request.Builder::class.java)

        `when`(chain.request()).thenReturn(request)
        `when`(chain.proceed(any())).thenReturn(response)
        `when`(request.newBuilder()).thenReturn(requestBuilder)
        `when`(requestBuilder.header(anyString(), anyString())).thenReturn(requestBuilder)
        `when`(requestBuilder.build()).thenReturn(request)
        `when`(response.code).thenReturn(200)

        return chain
    }

    private fun givenFailResponse(chain: Interceptor.Chain) {
        val response = mock(Response::class.java)
        `when`(chain.proceed(any())).thenReturn(response)
        `when`(response.code).thenReturn(401)
    }

    private fun <T> any(): T = org.mockito.ArgumentMatchers.any<T>()
}
