package com.crowdin.platform

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

class EchoWebSocketListenerTest {

    @Test
    fun whenOnOpen_shouldRegisterViewChangeListener() {
        // Given
        val mockMappingData = spy(LanguageData::class.java)
        val mockDistributionData = mock(DistributionInfoResponse.DistributionData::class.java)
        val mockViewTransformerManager = spy(ViewTransformerManager::class.java)
        val echoWebSocketListener = EchoWebSocketListener(
            mockMappingData,
            mockDistributionData,
            mockViewTransformerManager,
            "en"
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
        val mockViewTransformerManager = spy(ViewTransformerManager::class.java)
        val echoWebSocketListener = EchoWebSocketListener(
            mockMappingData,
            mockDistributionData,
            mockViewTransformerManager,
            "en"
        )
        val mockSocket = mock(WebSocket::class.java)
        val expectedReason = "test"

        // When
        echoWebSocketListener.onClosing(mockSocket, 0, expectedReason)

        // Then
        verify(mockSocket).close(0x3E9, expectedReason)
    }
}
