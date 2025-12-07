package com.crowdin.platform

import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.remote.api.DistributionInfoResponse
import com.crowdin.platform.realtimeupdate.RealTimeUpdateManager
import com.crowdin.platform.transformer.ViewTransformerManager
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`

class RealTimeUpdateManagerTest {
    private lateinit var mockDataManager: DataManager
    private lateinit var mockViewTransformer: ViewTransformerManager

    @Before
    fun setUp() {
        mockDataManager = mock(DataManager::class.java)
        mockViewTransformer = mock(ViewTransformerManager::class.java)
    }

    @Test
    fun openConnection_whenDataManagerNull_shouldReturn() {
        // Given
        val realTimeUpdateManager = RealTimeUpdateManager("EN", null, mockViewTransformer, null)

        // When
        realTimeUpdateManager.openConnection(null)

        // Then
        verifyNoInteractions(mockViewTransformer)
    }

    @Test
    fun openConnection_whenDistributionDataNull_shouldReturn() {
        // Given
        val realTimeUpdateManager =
            RealTimeUpdateManager("EN", mockDataManager, mockViewTransformer, null)

        // When
        realTimeUpdateManager.openConnection(null)

        // Then
        verify(mockDataManager).getData<DistributionInfoResponse.DistributionData>(
            "distribution_data",
            DistributionInfoResponse.DistributionData::class.java,
        )
        verifyNoInteractions(mockViewTransformer)
    }

    @Test
    fun openConnection_whenMappingNotNull_shouldCreateConnection() {
        // Given
        val realTimeUpdateManager =
            RealTimeUpdateManager("EN", mockDataManager, mockViewTransformer, null)
        val distributionData = givenDistributionData()
        `when`(
            mockDataManager.getData<DistributionInfoResponse.DistributionData>(
                any(),
                any(),
            ),
        ).thenReturn(distributionData)
        `when`(mockDataManager.getMapping("EN")).thenReturn(LanguageData())

        // When
        realTimeUpdateManager.openConnection(null)

        // Then
        verify(mockDataManager).getMapping("EN")
    }

    @Test
    fun closeConnection_shouldRemoveTransformerListener() {
        // Given
        val realTimeUpdateManager =
            RealTimeUpdateManager("EN", mockDataManager, mockViewTransformer, null)

        // When
        realTimeUpdateManager.closeConnection()

        // Then
        verify(mockViewTransformer).setOnViewsChangeListener(null)
    }

    private fun givenDistributionData() =
        DistributionInfoResponse.DistributionData(
            DistributionInfoResponse.DistributionData.ProjectData("projectIdTest", "testWsHash"),
            DistributionInfoResponse.DistributionData.UserData("userIdTest"),
            "wss://ws-test",
        )
}
