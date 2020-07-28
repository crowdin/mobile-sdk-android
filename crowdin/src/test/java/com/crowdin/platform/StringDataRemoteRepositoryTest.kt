package com.crowdin.platform

import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.model.LanguageInfo
import com.crowdin.platform.data.model.LanguageInfoResponse
import com.crowdin.platform.data.parser.Reader
import com.crowdin.platform.data.remote.StringDataRemoteRepository
import com.crowdin.platform.data.remote.api.CrowdinApi
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
    private lateinit var mockCrowdinApi: CrowdinApi
    private lateinit var mockReader: Reader
    private lateinit var mockCallback: LanguageDataCallback

    @Before
    fun setUp() {
        mockDistributionApi = mock(CrowdinDistributionApi::class.java)
        mockCrowdinApi = mock(CrowdinApi::class.java)
        mockReader = mock(Reader::class.java)
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
        givenMockLanguageResponse()
        givenMockResponse()

        // When
        repository.onManifestDataReceived(manifestData, mockCallback)

        // Then
        verify(mockDistributionApi).getResourceFile(any(), any(), any(), ArgumentMatchers.anyLong())
    }

    @Test
    fun whenFetchWithCallbackAndResponseFailure_shouldCallFailureMethod() {
        // Given
        val repository = givenStringDataRemoteRepository()
        val manifestData = givenManifestData()
        givenMockLanguageResponse()
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
        givenMockLanguageResponse()
        givenMockResponse(successCode = 204)

        // When
        repository.onManifestDataReceived(manifestData, mockCallback)

        // Then
        verify(mockCallback).onFailure(any())
    }

    private fun givenStringDataRemoteRepository(): StringDataRemoteRepository {
        val repository = StringDataRemoteRepository(mockDistributionApi, "hash")
        repository.crowdinApi = mockCrowdinApi
        return repository
    }

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

        val stubResponse = StubResponseBody()
        val response = if (success) {
            Response.success<ResponseBody>(successCode, stubResponse)
        } else {
            Response.error(403, stubResponse)
        }
        `when`(mockedCall.execute()).thenReturn(response)
    }

    private fun givenMockLanguageResponse() {
        val mockedCall = mock(Call::class.java) as Call<LanguageInfoResponse>
        `when`(mockCrowdinApi.getLanguageInfo(any())).thenReturn(mockedCall)
        val response = Response.success(
            200,
            LanguageInfoResponse(LanguageInfo("en", "English", "en", "eng", "en-US", "en-rUS"))
        )
        `when`(mockedCall.execute()).thenReturn(response)
    }
}
