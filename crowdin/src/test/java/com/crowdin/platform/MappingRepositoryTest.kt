package com.crowdin.platform

import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.ManifestData
import com.crowdin.platform.data.parser.Reader
import com.crowdin.platform.data.remote.MappingRepository
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MappingRepositoryTest {

    private lateinit var mockDistributionApi: CrowdinDistributionApi
    private lateinit var mockReader: Reader
    private lateinit var mockDataManager: DataManager
    private lateinit var mockCallback: LanguageDataCallback

    @Before
    fun setUp() {
        mockDistributionApi = mock(CrowdinDistributionApi::class.java)
        mockReader = mock(Reader::class.java)
        mockDataManager = mock(DataManager::class.java)
        `when`(mockReader.parseInput(any(), eq(null))).thenReturn(LanguageData())
        mockCallback = mock(LanguageDataCallback::class.java)
    }

    @Test
    fun whenFetchData_shouldRequestManifestApiCall() {
        // Given
        val mappingRepository = givenMappingRepository()
        givenMockManifestResponse()

        // When
        mappingRepository.fetchData()

        // Then
        verify(mockDistributionApi).getResourceManifest(any())
    }

    @Test
    fun whenFetchManifestSuccess_shouldTriggerFilePathApiCall() {
        // Given
        val mappingRepository = givenMappingRepository()
        val manifestData = givenManifestData()
        givenMockResponse()

        // When
        mappingRepository.onManifestDataReceived(manifestData, mockCallback)

        // Then
        verify(mockDistributionApi).getMappingFile(any(), any(), any())
    }

    @Test
    fun whenFetchDataSuccess_shouldParseResponseAndCloseReader() {
        // Given
        val mappingRepository = givenMappingRepository()
        val manifestData = givenManifestData()
        givenMockResponse()

        // When
        mappingRepository.onManifestDataReceived(manifestData, mockCallback)

        // Then
        verify(mockReader).parseInput(any(), any())
        verify(mockReader).close()
    }

    @Test
    fun whenFetchDataSuccess_shouldStoreResult() {
        // Given
        val mappingRepository = givenMappingRepository()
        val manifestData = givenManifestData()
        givenMockResponse()

        // When
        mappingRepository.onManifestDataReceived(manifestData, mockCallback)

        // Then
        verify(mockDataManager).saveMapping(any())
    }

    @Test
    fun whenFetchWithCallbackAndResponseFailure_shouldCallFailureMethod() {
        // Given
        val mappingRepository = givenMappingRepository()
        val manifestData = givenManifestData()
        givenMockResponse(false)

        // When
        mappingRepository.onManifestDataReceived(manifestData, mockCallback)

        // Then
        verify(mockCallback).onFailure(any())
    }

    @Test
    fun whenFetchWithCallbackAndResponseNotCode200_shouldCallFailureMethod() {
        // Given
        val mappingRepository = givenMappingRepository()
        val manifestData = givenManifestData()
        givenMockResponse(successCode = 204)

        // When
        mappingRepository.onManifestDataReceived(manifestData, mockCallback)

        // Then
        verify(mockCallback).onFailure(any())
    }

    private fun givenMappingRepository(): MappingRepository =
        MappingRepository(
            mockDistributionApi,
            mockReader,
            mockDataManager,
            "hash",
            "en"
        )

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
        `when`(mockDistributionApi.getMappingFile(any(), any(), any())).thenReturn(mockedCall)

        val response = if (success) {
            Response.success<ResponseBody>(successCode, StubResponseBody())
        } else {
            Response.error<ResponseBody>(403, StubResponseBody())
        }
        `when`(mockedCall.execute()).thenReturn(response)
    }

    private fun givenManifestData(): ManifestData {
        val json = "{\"files\":[\"\\/strings.xml\"]}"
        return Gson().fromJson<ManifestData>(json, ManifestData::class.java)
    }
}
