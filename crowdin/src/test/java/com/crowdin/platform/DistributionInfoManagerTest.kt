package com.crowdin.platform

import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.DistributionInfoCallback
import com.crowdin.platform.data.remote.DistributionInfoManager
import com.crowdin.platform.data.remote.api.CrowdinApi
import com.crowdin.platform.data.remote.api.DistributionInfoResponse
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DistributionInfoManagerTest {

    private lateinit var mockCrowdinApi: CrowdinApi
    private lateinit var mockDataManager: DataManager
    private lateinit var callback: DistributionInfoCallback

    @Before
    fun setUp() {
        mockCrowdinApi = mock(CrowdinApi::class.java)
        mockDataManager = mock(DataManager::class.java)
        callback = mock(DistributionInfoCallback::class.java)
    }

    @Test
    fun whenGetDistributionInfo_shouldCallApi() {
        // Given
        val distributionInfoManager = givenDistributionManager()
        givenMockResponse()

        // When
        distributionInfoManager.getDistributionInfo(callback)

        // Then
        verify(mockCrowdinApi).getInfo("hashTest")
    }

    @Test
    fun whenGetDistributionInfoResponseSuccess_shouldSaveInDataManager() {
        // Given
        val distributionInfoManager = givenDistributionManager()
        givenMockResponse()

        // When
        distributionInfoManager.getDistributionInfo(callback)

        // Then
        verify(mockDataManager).saveData(eq("distribution_data"), any())
    }

    @Test
    fun whenGetDistributionInfoResponseSuccess_shouldCallCallbackSuccess() {
        // Given
        val distributionInfoManager = givenDistributionManager()
        givenMockResponse()

        // When
        distributionInfoManager.getDistributionInfo(callback)

        // Then
        verify(callback).onResponse()
    }

    @Test
    fun whenGetDistributionInfoResponseFailure_shouldCallCallbackError() {
        // Given
        val distributionInfoManager = givenDistributionManager()
        givenMockResponse(false)

        // When
        distributionInfoManager.getDistributionInfo(callback)

        // Then
        verify(callback).onError(any())
    }

    private fun givenDistributionManager(): DistributionInfoManager =
            DistributionInfoManager(mockCrowdinApi, mockDataManager, "hashTest")

    private fun givenMockResponse(success: Boolean = true, successCode: Int = 200) {
        val mockedCall = mock(Call::class.java) as Call<DistributionInfoResponse>
        `when`(mockCrowdinApi.getInfo(any())).thenReturn(mockedCall)
        doAnswer {
            val callback = it.getArgument(0, Callback::class.java) as Callback<DistributionInfoResponse>
            if (success) {
                callback.onResponse(mockedCall, Response.success<DistributionInfoResponse>(successCode, mock(DistributionInfoResponse::class.java)))
            } else {
                callback.onFailure(mockedCall, Throwable())
            }

        }.`when`<Call<DistributionInfoResponse>>(mockedCall).enqueue(any())
    }
}