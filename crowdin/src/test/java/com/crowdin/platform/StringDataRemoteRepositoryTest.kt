package com.crowdin.platform

import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.parser.Reader
import com.crowdin.platform.data.remote.StringDataRemoteRepository
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import retrofit2.Call
import retrofit2.Response

class StringDataRemoteRepositoryTest {

    private lateinit var mockDistributionApi: CrowdinDistributionApi
    private lateinit var mockReader: Reader
    private lateinit var mockCallback: LanguageDataCallback

    @Before
    fun setUp() {
        mockDistributionApi = mock(CrowdinDistributionApi::class.java)
        mockReader = mock(Reader::class.java)
        `when`(mockReader.parseInput(any(), eq(null))).thenReturn(LanguageData())
        mockCallback = mock(LanguageDataCallback::class.java)
    }

    @Test
    fun whenFetchData_shouldRequestManifestApiCall() {
        // Given
        val repository = givenStringDataRemoteRepository()
        givenMockManifestResponse(mockDistributionApi)

        // When
        repository.fetchData()

        // Then
        verify(mockDistributionApi).getResourceManifest(any())
    }

    @Test
    fun whenFetchData_shouldTriggerApiCall() {
        // Given
        val repository = givenStringDataRemoteRepository()
        val manifestData = givenManifestData()
        givenMockResponse()

        // When
        repository.onManifestDataReceived(manifestData, mockCallback)

        // Then
        verify(mockDistributionApi).getResourceFile(any(), any(), any(), ArgumentMatchers.anyLong())
    }

    @Test
    fun whenFetchDataSuccess_shouldParseResponseAndCloseReader() {
        // Given
        val repository = givenStringDataRemoteRepository()
        val manifestData = givenManifestData()
        givenMockResponse()

        // When
        repository.onManifestDataReceived(manifestData, mockCallback)

        // Then
        verify(mockReader).parseInput(any(), any())
        verify(mockReader).close()
    }

    @Test
    fun whenFetchWithCallbackAndResponseFailure_shouldCallFailureMethod() {
        // Given
        val repository = givenStringDataRemoteRepository()
        val manifestData = givenManifestData()
        givenMockResponse(false)

        // When
        repository.onManifestDataReceived(manifestData, mockCallback)

        // Then
        verify(mockCallback).onFailure(any())
    }

    @Test
    fun whenFetchWithCallbackAndResponseNotCode200_shouldCallFailureMethod() {
        // Given
        val repository = givenStringDataRemoteRepository()
        val manifestData = givenManifestData()
        givenMockResponse(successCode = 204)

        // When
        repository.onManifestDataReceived(manifestData, mockCallback)

        // Then
        verify(mockCallback).onFailure(any())
    }

    private fun givenStringDataRemoteRepository(): StringDataRemoteRepository =
        StringDataRemoteRepository(mockDistributionApi, mockReader, "hash")

    private fun givenMockResponse(success: Boolean = true, successCode: Int = 200) {
        val mockedCall = mock(Call::class.java) as Call<ResponseBody>
        `when`(
            mockDistributionApi.getResourceFile(
                any(),
                any(),
                any(),
                ArgumentMatchers.anyLong()
            )
        ).thenReturn(
            mockedCall
        )

        val response = if (success) {
            Response.success<ResponseBody>(successCode, StubResponseBody())
        } else {
            Response.error(403, StubResponseBody())
        }
        `when`(mockedCall.execute()).thenReturn(response)
    }
}
