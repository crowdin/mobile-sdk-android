package com.crowdin.platform

import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.parser.Reader
import com.crowdin.platform.data.remote.StringDataRemoteRepository
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import retrofit2.Call
import retrofit2.Callback
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
        val mappingRepository = givenStringDataRemoteRepository()
        givenMockManifestResponse()

        // When
        mappingRepository.fetchData()

        // Then
        verify(mockDistributionApi).getResourceManifest(any())
    }

    @Test
    fun whenFetchData_shouldTriggerApiCall() {
        // Given
        val stringDataRemoteRepository = givenStringDataRemoteRepository()
        givenMockManifestResponse()
        givenMockResponse()

        // When
        stringDataRemoteRepository.fetchData()

        // Then
        verify(mockDistributionApi).getResourceFile(any(), any(), any())
    }

    @Test
    fun whenFetchDataSuccess_shouldParseResponseAndCloseReader() {
        // Given
        val stringDataRemoteRepository = givenStringDataRemoteRepository()
        givenMockManifestResponse()
        givenMockResponse()

        // When
        stringDataRemoteRepository.fetchData()

        // Then
        verify(mockReader).parseInput(any(), any())
        verify(mockReader).close()
    }

    @Test
    fun whenFetchWithCallbackAndResponseSuccess_shouldCallSuccessMethod() {
        // Given
        val stringDataRemoteRepository = givenStringDataRemoteRepository()
        givenMockManifestResponse()
        givenMockResponse()

        // When
        stringDataRemoteRepository.fetchData(mockCallback)

        // Then
        verify(mockCallback).onDataLoaded(any())
    }

    @Test
    fun whenFetchWithCallbackAndResponseFailure_shouldCallFailureMethod() {
        // Given
        val stringDataRemoteRepository = givenStringDataRemoteRepository()
        givenMockManifestResponse()
        givenMockResponse(false)

        // When
        stringDataRemoteRepository.fetchData(mockCallback)

        // Then
        verify(mockCallback).onFailure(any())
    }

    @Test
    fun whenFetchWithCallbackAndResponseNotCode200_shouldCallFailureMethod() {
        // Given
        val stringDataRemoteRepository = givenStringDataRemoteRepository()
        givenMockManifestResponse()
        givenMockResponse(successCode = 204)

        // When
        stringDataRemoteRepository.fetchData(mockCallback)

        // Then
        verify(mockCallback).onFailure(any())
    }

    private fun givenStringDataRemoteRepository(): StringDataRemoteRepository =
            StringDataRemoteRepository(mockDistributionApi, mockReader, "hash")

    private fun givenMockManifestResponse(success: Boolean = true, successCode: Int = 200) {
        val mockedCall = mock(Call::class.java) as Call<ResponseBody>
        `when`(mockDistributionApi.getResourceManifest(any())).thenReturn(mockedCall)
        val responseBody = mock(StubResponseBody::class.java)
        val json = "{\"files\":[\"\\/strings.xml\"]}"
        `when`(responseBody.string()).thenReturn(json)

        val response = if (success) {
            Response.success<ResponseBody>(successCode, responseBody)
        } else {
            Response.error<ResponseBody>(403, StubResponseBody())
        }

        doAnswer {
            val callback = it.getArgument(0, Callback::class.java) as Callback<ResponseBody>
            callback.onResponse(mockedCall, response)
        }.`when`<Call<ResponseBody>>(mockedCall).enqueue(any())
    }

    private fun givenMockResponse(success: Boolean = true, successCode: Int = 200) {
        val mockedCall = mock(Call::class.java) as Call<ResponseBody>
        `when`(mockDistributionApi.getResourceFile(any(), any(), any())).thenReturn(mockedCall)

        val response = if (success) {
            Response.success<ResponseBody>(successCode, StubResponseBody())
        } else {
            Response.error<ResponseBody>(403, StubResponseBody())
        }

        doAnswer {
            val callback = it.getArgument(0, Callback::class.java) as Callback<ResponseBody>
            callback.onResponse(mockedCall, response)
        }.`when`<Call<ResponseBody>>(mockedCall).enqueue(any())
    }
}