package com.crowdin.platform

import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.model.AuthConfig
import com.crowdin.platform.data.model.AuthInfo
import com.crowdin.platform.data.model.AuthResponse
import com.crowdin.platform.data.remote.api.AuthApi
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import retrofit2.Call
import retrofit2.Response

class SessionTest {
    private lateinit var session: Session
    private lateinit var mockCallResponse: Call<AuthResponse>
    private lateinit var mockAuthConfig: AuthConfig
    private lateinit var mockDataManager: DataManager
    private lateinit var mockAuthApi: AuthApi

    @Before
    fun setUp() {
        mockCallResponse = mock(Call::class.java) as Call<AuthResponse>
        mockAuthConfig = mock(AuthConfig::class.java)
        mockDataManager = mock(DataManager::class.java)
        mockAuthApi = mock(AuthApi::class.java)
        session = SessionImpl(mockDataManager, mockAuthApi)
    }

    @Test
    fun whenIsAuthorizedCalled_shouldReturnLocalStorageResult() {
        // Given
        `when`(mockDataManager.isAuthorized()).thenReturn(true)

        // When
        val result = session.isAuthorized()

        // Then
        assertThat(result, `is`(true))
        verify(mockDataManager).isAuthorized()
    }

    @Test
    fun whenIsTokenExpiredCalled_shouldReturnLocalStorageResult() {
        // Given
        `when`(mockDataManager.isTokenExpired()).thenReturn(true)

        // When
        val result = session.isTokenExpired()

        // Then
        assertThat(result, `is`(true))
        verify(mockDataManager).isTokenExpired()
    }

    @Test
    fun whenGetAccessTokenCalled_shouldReturnLocalStorageResult() {
        // Given
        val expectedAccessToken = "access_token_test"
        `when`(mockDataManager.getAccessToken()).thenReturn(expectedAccessToken)

        // When
        val result = session.getAccessToken()

        // Then
        assertThat(result, `is`(expectedAccessToken))
        verify(mockDataManager).getAccessToken()
    }

    @Test
    fun whenRefreshTokenNull_shouldSkipRefresh() {
        // Given
        `when`(mockDataManager.getRefreshToken()).thenReturn(null)

        // When
        val result = session.refreshToken(null, mockAuthConfig)

        // Then
        assertThat(result, `is`(false))
        verify(mockDataManager).getRefreshToken()
    }

    @Test
    fun whenRefreshTokenNotNull_shouldRequestNewFromApi() {
        // Given
        val refreshToken = "refresh_token_test"
        `when`(mockDataManager.getRefreshToken()).thenReturn(refreshToken)
        `when`(mockAuthApi.getToken(any(), any())).thenReturn(mockCallResponse)
        `when`(mockCallResponse.execute()).thenReturn(Response.success(provideAuthResponse()))

        // When
        session.refreshToken(null, mockAuthConfig)

        // Then
        verify(mockDataManager).getRefreshToken()
        verify(mockAuthApi).getToken(any(), any())
    }

    @Test
    fun whenRefreshTokenSuccess_shouldSaveTokenData() {
        // Given
        val refreshToken = "refresh_token_test"
        `when`(mockDataManager.getRefreshToken()).thenReturn(refreshToken)
        `when`(mockAuthApi.getToken(any(), any())).thenReturn(mockCallResponse)
        val authResponse = provideAuthResponse()
        `when`(mockCallResponse.execute()).thenReturn(Response.success(authResponse))
        val expectedAuthInfo = AuthInfo(authResponse)

        // When
        val result = session.refreshToken(null, mockAuthConfig)

        // Then
        assertThat(result, `is`(true))
        verify(mockDataManager).saveData(eq("auth_info"), eq(expectedAuthInfo))
    }

    @Test
    fun whenInvalidate_shouldClearAuthInfo() {
        // When
        session.invalidate()

        // Then
        verify(mockDataManager).saveData(eq("auth_info"), eq(null))
    }

    private fun provideAuthResponse(): AuthResponse =
        AuthResponse(
            "token",
            11,
            "access_token",
            "refresh_token",
        )
}
