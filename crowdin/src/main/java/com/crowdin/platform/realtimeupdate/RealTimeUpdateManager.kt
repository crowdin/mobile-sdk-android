package com.crowdin.platform.realtimeupdate

import com.crowdin.platform.data.StringDataManager
import com.crowdin.platform.data.model.AuthInfo
import com.crowdin.platform.data.remote.api.DistributionInfoResponse
import com.crowdin.platform.transformer.ViewTransformerManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

internal class RealTimeUpdateManager(
        private val distributionKey: String?,
        private val sourceLanguage: String,
        private val stringDataManager: StringDataManager?,
        private val viewTransformerManager: ViewTransformerManager) {

    companion object {
        const val NORMAL_CLOSURE_STATUS = 0x3E9
        const val PLURAL_NONE = "none"
        private const val BASE_WS_URL = "wss://ws-lb.crowdin.com/"
        private const val COOKIE = "Cookie"
        private const val X_CSRF_TOKEN = "x-csrf-token"
    }

    private var socket: WebSocket? = null

    fun openConnection() {
        distributionKey ?: return
        stringDataManager ?: return

        val authInfo = stringDataManager.getData(StringDataManager.AUTH_INFO, AuthInfo::class.java)
        authInfo ?: return
        authInfo as AuthInfo

        val distributionData = stringDataManager.getData(StringDataManager.DISTRIBUTION_DATA,
                DistributionInfoResponse.DistributionData::class.java)
        distributionData ?: return
        distributionData as DistributionInfoResponse.DistributionData

        createConnection(distributionData, authInfo.cookies, authInfo.xCsrfToken)
    }

    fun closeConnection() {
        socket?.close(NORMAL_CLOSURE_STATUS, null)
        viewTransformerManager.setOnViewsChangeListener(null)
    }

    private fun createConnection(distributionData: DistributionInfoResponse.DistributionData, cookie: String, xCsrfToken: String) {
        val mappingData = stringDataManager?.getMapping(sourceLanguage) ?: return
        val client = OkHttpClient().newBuilder()
                .addInterceptor { chain -> addHeaders(chain, cookie, xCsrfToken) }
                .build()
        val request = Request.Builder()
                .url(BASE_WS_URL)
                .build()

        val listener = EchoWebSocketListener(mappingData, distributionData, viewTransformerManager)
        socket = client.newWebSocket(request, listener)
        client.dispatcher().executorService().shutdown()
    }

    private fun addHeaders(chain: Interceptor.Chain, cookie: String, xCsrfToken: String): okhttp3.Response {
        val original = chain.request()
        val authorized = original.newBuilder()
                .addHeader(COOKIE, cookie)
                .addHeader(X_CSRF_TOKEN, xCsrfToken)
                .build()
        return chain.proceed(authorized)
    }
}