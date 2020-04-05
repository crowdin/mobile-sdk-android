package com.crowdin.platform

import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.model.BuildTranslationRequest
import com.crowdin.platform.data.model.File
import com.crowdin.platform.data.model.FileData
import com.crowdin.platform.data.model.FileResponse
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.Translation
import com.crowdin.platform.data.model.TranslationResponse
import com.crowdin.platform.data.parser.Reader
import com.crowdin.platform.data.remote.TranslationDataRepository
import com.crowdin.platform.data.remote.api.CrowdinApi
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import com.crowdin.platform.data.remote.api.CrowdinTranslationApi
import com.crowdin.platform.data.remote.api.DistributionInfoResponse
import com.crowdin.platform.util.getFormattedCode
import java.util.Locale
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import retrofit2.Call
import retrofit2.Response

class TranslationDataRepositoryTest {

    private lateinit var mockDistributionApi: CrowdinDistributionApi
    private lateinit var mockCrowdinApi: CrowdinApi
    private lateinit var mockCrowdinTranslationApi: CrowdinTranslationApi
    private lateinit var mockReader: Reader
    private lateinit var mockDataManager: DataManager
    private lateinit var mockCallback: LanguageDataCallback

    @Before
    fun setUp() {
        mockDistributionApi = mock(CrowdinDistributionApi::class.java)
        mockCrowdinApi = mock(CrowdinApi::class.java)
        mockCrowdinTranslationApi = mock(CrowdinTranslationApi::class.java)
        mockReader = mock(Reader::class.java)
        mockDataManager = mock(DataManager::class.java)
        `when`(mockReader.parseInput(any(), eq(null))).thenReturn(LanguageData())
        mockCallback = mock(LanguageDataCallback::class.java)
    }

    @Test
    fun whenFetchData_shouldRequestManifestApiCall() {
        // Given
        val repository = givenTranslationDataRepository()
        givenMockManifestResponse(mockDistributionApi)

        // When
        repository.fetchData()

        // Then
        verify(mockDistributionApi).getResourceManifest(any())
    }

    @Test
    fun whenFetchManifestSuccess_shouldGetDistributionData() {
        // Given
        val repository = givenTranslationDataRepository()
        val manifestData = givenManifestData()

        // When
        repository.onManifestDataReceived(manifestData, mockCallback)

        // Then
        verify(mockDataManager).getData<DistributionInfoResponse.DistributionData>(
            "distribution_data",
            DistributionInfoResponse.DistributionData::class.java
        )
    }

    @Test
    fun whenDistributionDataHasProjectId_shouldRequestProjectFiles() {
        // Given
        val repository = givenTranslationDataRepository()
        val manifestData = givenManifestData()
        givenMockMappingFileResponse(mockDistributionApi)
        givenMockDistributionData()
        givenMockEmptyFileResponse()

        // When
        repository.onManifestDataReceived(manifestData, mockCallback)

        // Then
        verify(mockCrowdinApi).getFiles("testId")
    }

    @Test
    fun whenFilesReceived_shouldRequestBuildTranslations() {
        // Given
        val repository = givenTranslationDataRepository()
        val manifestData = givenManifestData()
        givenMockDistributionData()
        givenMockFilesResponse()
        givenMockTranslationResponse()

        // When
        repository.onManifestDataReceived(manifestData, mockCallback)

        // Then
        verify(mockCrowdinApi).getTranslation(
            "",
            "testId",
            0,
            BuildTranslationRequest(Locale.getDefault().getFormattedCode())
        )
    }

    @Test
    fun whenTranslationReady_shouldRequestTranslationResource() {
        // Given
        val repository = givenTranslationDataRepository()
        val manifestData = givenManifestData()
        givenMockDistributionData()
        givenMockFilesResponse()
        givenMockTranslationResponse(true)
        givenMockTranslationResource()

        // When
        repository.onManifestDataReceived(manifestData, mockCallback)

        // Then
        verify(mockCrowdinTranslationApi).getTranslationResource("test_url")
        verify(mockReader).parseInput(any(), any())
        verify(mockReader).close()
    }

    private fun givenMockDistributionData() {
        val projectData = DistributionInfoResponse.DistributionData.ProjectData(
            "testId",
            "hash"
        )
        val userData = DistributionInfoResponse.DistributionData.UserData("userId")
        val distributionData = DistributionInfoResponse.DistributionData(
            projectData,
            userData,
            "url"
        )
        `when`(
            mockDataManager.getData<DistributionInfoResponse.DistributionData>(
                "distribution_data",
                DistributionInfoResponse.DistributionData::class.java
            )
        ).thenReturn(distributionData)
    }

    private fun givenTranslationDataRepository(): TranslationDataRepository =
        TranslationDataRepository(
            mockDistributionApi,
            mockCrowdinApi,
            mockCrowdinTranslationApi,
            mockReader,
            mockDataManager,
            "hash"
        )

    private fun givenMockEmptyFileResponse() {
        val mockedCall = mock(Call::class.java) as Call<FileResponse>
        `when`(mockCrowdinApi.getFiles(any())).thenReturn(mockedCall)
        val fileResponse = FileResponse(listOf())
        val response = Response.success<FileResponse>(200, fileResponse)
        `when`(mockedCall.execute()).thenReturn(response)
    }

    private fun givenMockFilesResponse() {
        val mockedCall = mock(Call::class.java) as Call<FileResponse>
        `when`(mockCrowdinApi.getFiles(any())).thenReturn(mockedCall)
        val fileResponse = FileResponse(
            listOf(FileData(File(0, 0, "strings.xml", "title0")))
        )
        val response = Response.success<FileResponse>(200, fileResponse)
        `when`(mockedCall.execute()).thenReturn(response)
    }

    private fun givenMockTranslationResponse(includeBody: Boolean = false) {
        val mockedCall = mock(Call::class.java) as Call<TranslationResponse>
        `when`(
            mockCrowdinApi.getTranslation(
                "",
                "testId",
                0,
                BuildTranslationRequest(Locale.getDefault().getFormattedCode())
            )
        ).thenReturn(mockedCall)

        val translation = if (includeBody) {
            TranslationResponse(Translation("test_url", "etag"))
        } else {
            null
        }
        val response = Response.success<TranslationResponse>(200, translation)
        `when`(mockedCall.execute()).thenReturn(response)
    }

    private fun givenMockTranslationResource() {
        val mockedCall = mock(Call::class.java) as Call<ResponseBody>
        `when`(mockCrowdinTranslationApi.getTranslationResource("test_url")).thenReturn(mockedCall)
        val response = Response.success<ResponseBody>(200, StubResponseBody())
        `when`(mockedCall.execute()).thenReturn(response)
    }
}
