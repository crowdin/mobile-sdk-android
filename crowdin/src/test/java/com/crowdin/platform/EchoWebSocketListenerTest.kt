package com.crowdin.platform

import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.remote.api.DistributionInfoResponse
import com.crowdin.platform.realtimeupdate.EchoWebSocketListener
import com.crowdin.platform.transformer.ViewTransformerManager
import okhttp3.Response
import okhttp3.WebSocket
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class EchoWebSocketListenerTest {
    @Test
    fun subscribeView_shouldUseUpdatedEventNames() {
        // Given
        val mockMappingData = spy(LanguageData::class.java)
        val mockDistributionData = mock(DistributionInfoResponse.DistributionData::class.java)
        val mockDataManager = mock(DataManager::class.java)
        val mockViewTransformerManager = spy(ViewTransformerManager::class.java)
        val mockWebSocket = mock(WebSocket::class.java)
        val listener =
            EchoWebSocketListener(
                mockDataManager,
                mockMappingData,
                mockDistributionData,
                mockViewTransformerManager,
                null,
                "en",
            )
        val project = DistributionInfoResponse.DistributionData.ProjectData("1", "wsHash")
        val user = DistributionInfoResponse.DistributionData.UserData("2")
        val expectedUpdateEvent = "update-draft:wsHash:pr{1}:us{2}:en:tr{3}"
        val expectedSuggestionEvent = "top-suggestion:wsHash:pr{1}:en:tr{3}"

        `when`(mockDataManager.getTicket(expectedUpdateEvent)).thenReturn("update-ticket")
        `when`(mockDataManager.getTicket(expectedSuggestionEvent)).thenReturn("suggestion-ticket")

        val subscribeViewMethod =
            EchoWebSocketListener::class.java.getDeclaredMethod(
                "subscribeView",
                WebSocket::class.java,
                DistributionInfoResponse.DistributionData.ProjectData::class.java,
                DistributionInfoResponse.DistributionData.UserData::class.java,
                String::class.java,
            )
        subscribeViewMethod.isAccessible = true

        // When
        subscribeViewMethod.invoke(listener, mockWebSocket, project, user, "3")

        // Then
        verify(mockDataManager).getTicket(expectedUpdateEvent)
        verify(mockDataManager).getTicket(expectedSuggestionEvent)
        verify(mockWebSocket).send("{\"action\":\"subscribe\", \"event\":\"$expectedUpdateEvent\", \"ticket\": \"update-ticket\"}")
        verify(mockWebSocket).send("{\"action\":\"subscribe\", \"event\":\"$expectedSuggestionEvent\", \"ticket\": \"suggestion-ticket\"}")
    }

    @Test
    fun whenOnOpen_shouldRegisterViewChangeListener() {
        // Given
        val mockMappingData = spy(LanguageData::class.java)
        val mockDistributionData = mock(DistributionInfoResponse.DistributionData::class.java)
        val mockDataManager = mock(DataManager::class.java)
        val mockViewTransformerManager = spy(ViewTransformerManager::class.java)
        val echoWebSocketListener =
            EchoWebSocketListener(
                mockDataManager,
                mockMappingData,
                mockDistributionData,
                mockViewTransformerManager,
                null,
                "en",
            )

        // When
        echoWebSocketListener.onOpen(mock(WebSocket::class.java), mock(Response::class.java))

        // Then
        verify(mockViewTransformerManager).setOnViewsChangeListener(any())
    }

    @Test
    fun whenOnClosing_shouldCloseSocket() {
        // Given
        val mockMappingData = spy(LanguageData::class.java)
        val mockDistributionData = mock(DistributionInfoResponse.DistributionData::class.java)
        val mockDataManager = mock(DataManager::class.java)
        val mockViewTransformerManager = spy(ViewTransformerManager::class.java)
        val echoWebSocketListener =
            EchoWebSocketListener(
                mockDataManager,
                mockMappingData,
                mockDistributionData,
                mockViewTransformerManager,
                null,
                "en",
            )
        val mockSocket = mock(WebSocket::class.java)
        val expectedReason = "test"

        // When
        echoWebSocketListener.onClosing(mockSocket, 0, expectedReason)

        // Then
        verify(mockSocket).close(0x3E9, expectedReason)
    }
}
