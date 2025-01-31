package com.crowdin.platform.realtimeupdate

import android.util.Log
import com.crowdin.platform.Crowdin
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
    private val viewTransformerManager: ViewTransformerManager,
) {
    companion object {
        const val NORMAL_CLOSURE_STATUS = 0x3E9
        const val PLURAL_NONE = "none"
    }

    private var socket: WebSocket? = null
    var isConnectionCreated = false

    fun openConnection() {
        dataManager ?: return
        val distributionData =
            dataManager.getData(
                DataManager.DISTRIBUTION_DATA,
                DistributionInfoResponse.DistributionData::class.java,
            ) as DistributionInfoResponse.DistributionData?
        distributionData ?: return

        createConnection(distributionData)
    }

    fun closeConnection() {
        socket?.close(NORMAL_CLOSURE_STATUS, null)
        viewTransformerManager.setOnViewsChangeListener(null)
        isConnectionCreated = false

        Log.v(Crowdin.CROWDIN_TAG, "Realtime connection closed")
    }

    private fun createConnection(distributionData: DistributionInfoResponse.DistributionData) {
        val mappingData = dataManager?.getMapping(sourceLanguage) ?: return

        ThreadUtils.runInBackgroundPool({
            dataManager.getManifest()?.let {
                val client = OkHttpClient().newBuilder().build()
                val request =
                    Request
                        .Builder()
                        .url(distributionData.wsUrl)
                        .build()

                val languageCode = getMatchedCode(it.languages, it.customLanguages) ?: return@let
                val listener =
                    EchoWebSocketListener(
                        dataManager,
                        mappingData,
                        distributionData,
                        viewTransformerManager,
                        languageCode,
                    )
                socket = client.newWebSocket(request, listener)
                client.dispatcher.executorService.shutdown()
                isConnectionCreated = true

                Log.v(Crowdin.CROWDIN_TAG, "Realtime connection opened")
            }
        }, true)
    }
}
