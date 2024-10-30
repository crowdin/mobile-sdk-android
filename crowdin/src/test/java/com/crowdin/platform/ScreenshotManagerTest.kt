package com.crowdin.platform

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.remote.api.CreateScreenshotResponse
import com.crowdin.platform.data.remote.api.CrowdinApi
import com.crowdin.platform.data.remote.api.Data
import com.crowdin.platform.data.remote.api.DistributionInfoResponse
import com.crowdin.platform.data.remote.api.UploadScreenshotResponse
import com.crowdin.platform.screenshot.ScreenshotCallback
import com.crowdin.platform.screenshot.ScreenshotManager
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScreenshotManagerTest {
    private lateinit var mockCrowdinApi: CrowdinApi
    private lateinit var mockDataManager: DataManager

    @Before
    fun setUp() {
        mockCrowdinApi = mock(CrowdinApi::class.java)
        mockDataManager = mock(DataManager::class.java)
    }

    @Test
    fun sendScreenshot_whenMappingNullForLanguage_shouldReturn() {
        // Given
        val sourceLanguage = "EN"
        `when`(mockDataManager.getMapping(sourceLanguage)).thenReturn(null)
        val screenshotManager = ScreenshotManager(mockCrowdinApi, mockDataManager, sourceLanguage)

        // When
        screenshotManager.sendScreenshot(mock(Bitmap::class.java), mutableListOf())

        // Then
        verify(mockDataManager).getMapping(sourceLanguage)
        verifyNoInteractions(mockCrowdinApi)
    }

    @Test
    fun sendScreenshot_whenDistributionNullForLanguage_shouldReturn() {
        // Given
        val sourceLanguage = "EN"
        `when`(mockDataManager.getMapping(sourceLanguage)).thenReturn(mock(LanguageData::class.java))
        val screenshotManager = ScreenshotManager(mockCrowdinApi, mockDataManager, sourceLanguage)

        // When
        screenshotManager.sendScreenshot(mock(Bitmap::class.java), mutableListOf())

        // Then
        verify(mockDataManager).getData<DistributionInfoResponse.DistributionData>(
            "distribution_data",
            DistributionInfoResponse.DistributionData::class.java,
        )
        verifyNoInteractions(mockCrowdinApi)
    }

    @Test
    fun sendScreenshot_shouldCallScreenshotApi() {
        // Given
        val sourceLanguage = "EN"
        `when`(mockDataManager.getMapping(sourceLanguage)).thenReturn(LanguageData())
        val distributionData = givenDistributionData()
        `when`(
            mockDataManager.getData<DistributionInfoResponse.DistributionData>(
                any(),
                any(),
            ),
        ).thenReturn(distributionData)
        val screenshotManager = ScreenshotManager(mockCrowdinApi, mockDataManager, sourceLanguage)
        givenUploadScreenshotMockResponse()
        givenCreateScreenshotMockResponse()
        givenCreateTagMockResponse()

        // When
        screenshotManager.sendScreenshot(mock(Bitmap::class.java), mutableListOf())

        // Then
        val inOrder = inOrder(mockCrowdinApi)
        inOrder.verify(mockCrowdinApi).uploadScreenshot(any(), any())
        inOrder.verify(mockCrowdinApi).createScreenshot(any(), any())
        inOrder.verify(mockCrowdinApi).createTag("projectIdTest", 10, mutableListOf())
    }

    @Test
    fun sendScreenshot_whenCallbackAdded_shouldCallSuccessInOnResponse() {
        // Given
        val sourceLanguage = "EN"
        `when`(mockDataManager.getMapping(sourceLanguage)).thenReturn(LanguageData())
        val distributionData = givenDistributionData()
        `when`(
            mockDataManager.getData<DistributionInfoResponse.DistributionData>(
                any(),
                any(),
            ),
        ).thenReturn(distributionData)
        val screenshotManager = ScreenshotManager(mockCrowdinApi, mockDataManager, sourceLanguage)
        givenUploadScreenshotMockResponse()
        givenCreateScreenshotMockResponse()
        givenCreateTagMockResponse()
        val mockCallback = mock(ScreenshotCallback::class.java)

        // When
        screenshotManager.setScreenshotCallback(mockCallback)
        screenshotManager.sendScreenshot(mock(Bitmap::class.java), mutableListOf())

        // Then
        verify(mockCallback).onSuccess()
    }

    @Test
    fun sendScreenshot_whenCallbackAdded_shouldCallFailureInOnFailure() {
        // Given
        val sourceLanguage = "EN"
        `when`(mockDataManager.getMapping(sourceLanguage)).thenReturn(LanguageData())
        val distributionData = givenDistributionData()
        `when`(
            mockDataManager.getData<DistributionInfoResponse.DistributionData>(
                any(),
                any(),
            ),
        ).thenReturn(distributionData)
        val screenshotManager = ScreenshotManager(mockCrowdinApi, mockDataManager, sourceLanguage)
        givenUploadScreenshotMockResponse(false)
        val mockCallback = mock(ScreenshotCallback::class.java)

        // When
        screenshotManager.setScreenshotCallback(mockCallback)
        screenshotManager.sendScreenshot(mock(Bitmap::class.java), mutableListOf())

        // Then
        verify(mockCallback).onFailure(any())
    }

    @Test
    fun registerScreenshotObserverTest() {
        // Given
        val sourceLanguage = "EN"
        val screenshotManager = ScreenshotManager(mockCrowdinApi, mockDataManager, sourceLanguage)
        val mockContext = mock(Context::class.java)
        val mockContextResolver = mock(ContentResolver::class.java)
        `when`(mockContext.contentResolver).thenReturn(mockContextResolver)

        // When
        screenshotManager.registerScreenShotContentObserver(mockContext)

        // Then
        verify(mockContext).contentResolver
        verify(mockContextResolver).registerContentObserver(
            eq(MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
            eq(true),
            any(),
        )
    }

    @Test
    fun unregisterScreenshotObserver() {
        // Given
        val sourceLanguage = "EN"
        val screenshotManager = ScreenshotManager(mockCrowdinApi, mockDataManager, sourceLanguage)
        val mockContext = mock(Context::class.java)
        val mockContextResolver = mock(ContentResolver::class.java)
        `when`(mockContext.contentResolver).thenReturn(mockContextResolver)
        screenshotManager.registerScreenShotContentObserver(mockContext)

        // When
        screenshotManager.unregisterScreenShotContentObserver(mockContext)

        // Then
        verify(mockContext, times(2)).contentResolver
        verify(mockContextResolver).unregisterContentObserver(any())
    }

    private fun givenDistributionData() =
        DistributionInfoResponse.DistributionData(
            DistributionInfoResponse.DistributionData.ProjectData("projectIdTest", "testWsHash"),
            DistributionInfoResponse.DistributionData.UserData("userIdTest"),
            "wsUrlTest",
        )

    private fun givenUploadScreenshotMockResponse(
        success: Boolean = true,
        successCode: Int = 201,
    ) {
        val mockedCall = mock(Call::class.java) as Call<UploadScreenshotResponse>
        `when`(mockCrowdinApi.uploadScreenshot(any(), any())).thenReturn(mockedCall)
        doAnswer {
            val callback =
                it.getArgument(0, Callback::class.java) as Callback<UploadScreenshotResponse>
            if (success) {
                callback.onResponse(
                    mockedCall,
                    Response.success(
                        successCode,
                        UploadScreenshotResponse(Data(10, "test")),
                    ),
                )
            } else {
                callback.onFailure(mockedCall, Throwable())
            }
        }.`when`(mockedCall).enqueue(any())
    }

    private fun givenCreateScreenshotMockResponse(
        success: Boolean = true,
        successCode: Int = 201,
    ) {
        val mockedCall = mock(Call::class.java) as Call<CreateScreenshotResponse>
        `when`(mockCrowdinApi.createScreenshot(any(), any())).thenReturn(mockedCall)
        doAnswer {
            val callback =
                it.getArgument(0, Callback::class.java) as Callback<CreateScreenshotResponse>
            if (success) {
                callback.onResponse(
                    mockedCall,
                    Response.success(
                        successCode,
                        CreateScreenshotResponse(Data(10, "test")),
                    ),
                )
            } else {
                callback.onFailure(mockedCall, Throwable())
            }
        }.`when`(mockedCall).enqueue(any())
    }

    private fun givenCreateTagMockResponse(
        success: Boolean = true,
        successCode: Int = 201,
    ) {
        val mockedCall = mock(Call::class.java) as Call<ResponseBody>
        `when`(
            mockCrowdinApi.createTag(
                "projectIdTest",
                10,
                mutableListOf(),
            ),
        ).thenReturn(mockedCall)
        doAnswer {
            val callback = it.getArgument(0, Callback::class.java) as Callback<ResponseBody>
            if (success) {
                callback.onResponse(
                    mockedCall,
                    Response.success(successCode, mock(ResponseBody::class.java)),
                )
            } else {
                callback.onFailure(mockedCall, Throwable())
            }
        }.`when`(mockedCall).enqueue(any())
    }
}
