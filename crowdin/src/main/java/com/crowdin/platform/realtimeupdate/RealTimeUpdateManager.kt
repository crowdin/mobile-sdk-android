package com.crowdin.platform.realtimeupdate

import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.remote.api.DistributionInfoResponse
import com.crowdin.platform.transformer.ViewTransformerManager
import com.crowdin.platform.util.ThreadUtils
import com.crowdin.platform.util.getMatchedCode
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

internal class RealTimeUpdateManager(
    private val sourceLanguage: String,
    private val dataManager: DataManager?,
    private val viewTransformerManager: ViewTransformerManager
) {

    companion object {
        const val NORMAL_CLOSURE_STATUS = 0x3E9
        const val PLURAL_NONE = "none"
    }

    private var socket: WebSocket? = null

    fun openConnection() {
        dataManager ?: return
        val distributionData = dataManager.getData(
            DataManager.DISTRIBUTION_DATA,
            DistributionInfoResponse.DistributionData::class.java
        ) as DistributionInfoResponse.DistributionData?
        distributionData ?: return

        createConnection(distributionData)
    }

    fun closeConnection() {
        socket?.close(NORMAL_CLOSURE_STATUS, null)
        viewTransformerManager.setOnViewsChangeListener(null)
    }

    private fun createConnection(distributionData: DistributionInfoResponse.DistributionData) {
        val mappingData = dataManager?.getMapping(sourceLanguage) ?: return

        ThreadUtils.runInBackgroundPool({
            dataManager.getManifest()?.let {
                val client = OkHttpClient().newBuilder().build()
                val request = Request.Builder()
                    .url(distributionData.wsUrl)
                    .build()

                val languageCode = getMatchedCode(it.languages) ?: return@let
                val listener = EchoWebSocketListener(
                    mappingData,
                    distributionData,
                    viewTransformerManager,
                    languageCode
                )
                socket = client.newWebSocket(request, listener)
                client.dispatcher.executorService.shutdown()
            }
        }, true)
    }
}
