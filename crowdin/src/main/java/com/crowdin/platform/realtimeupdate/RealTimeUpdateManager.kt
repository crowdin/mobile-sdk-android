package com.crowdin.platform.realtimeupdate

import android.util.Log
import com.crowdin.platform.data.StringDataManager
import com.crowdin.platform.data.remote.api.CrowdinApi
import com.crowdin.platform.data.remote.api.DistributionInfoResponse
import com.crowdin.platform.transformer.ViewTransformerManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class RealTimeUpdateManager(
        private val crowdinApi: CrowdinApi,
        private val distributionKey: String?,
        private val sourceLanguage: String,
        private val stringDataManager: StringDataManager?,
        private val viewTransformerManager: ViewTransformerManager) {

    companion object {
        const val NORMAL_CLOSURE_STATUS = 0x3E9
        private const val BASE_WS_URL = "wss://ws-lb.crowdin.com/"
        private const val COOKIE = "Cookie"
        private const val X_CSRF_TOKEN = "x-csrf-token"
    }

    private var socket: WebSocket? = null

    fun openConnection() {
        distributionKey ?: return
        stringDataManager ?: return

        val authInfo = stringDataManager.getAuthInfo()
        authInfo ?: return

        getDistributionInfo(
                authInfo.userAgent,
                authInfo.cookies,
                authInfo.xCsrfToken,
                distributionKey)
    }

    fun closeConnection() {
        socket?.close(NORMAL_CLOSURE_STATUS, null)
        viewTransformerManager.setOnViewsChangeListener(null)
    }

    // TODO: remove after DistributionManager implementation
    private fun getDistributionInfo(userAgent: String, cookies: String,
                                    xCsrfToken: String, distributionKey: String) {
        crowdinApi.getInfo(userAgent, cookies, xCsrfToken, distributionKey)
                .enqueue(object : Callback<DistributionInfoResponse> {

                    override fun onResponse(call: Call<DistributionInfoResponse>, response: Response<DistributionInfoResponse>) {
                        val distributionInfo = response.body()
                        distributionInfo?.let {
                            if (it.success) {
                                createConnection(it.data, cookies, xCsrfToken)
                            }
                        }
                    }

                    override fun onFailure(call: Call<DistributionInfoResponse>, throwable: Throwable) {
                        Log.d(RealTimeUpdateManager::class.java.simpleName,
                                "Get info, onFailure:${throwable.localizedMessage}")
                    }
                })
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