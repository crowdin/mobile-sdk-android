package com.crowdin.platform.realtimeupdate

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import com.crowdin.platform.data.StringDataManager
import com.crowdin.platform.data.getMappingValueForKey
import com.crowdin.platform.data.remote.api.CrowdinApi
import com.crowdin.platform.data.remote.api.DistributionInfoResponse
import com.crowdin.platform.data.remote.api.EventResponse
import com.crowdin.platform.fromHtml
import com.crowdin.platform.transformer.ViewTransformerManager
import com.google.gson.Gson
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


internal class RealTimeUpdateManager(
        private val crowdinApi: CrowdinApi,
        private val distributionKey: String?,
        private val sourceLanguage: String,
        private val stringDataManager: StringDataManager?,
        private val viewTransformerManager: ViewTransformerManager) {

    companion object {
        private var TAG = RealTimeUpdateManager::class.java.simpleName
        private const val NORMAL_CLOSURE_STATUS = 0x3E9
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
    }

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
        val client = OkHttpClient().newBuilder()
                .addInterceptor { chain -> addHeaders(chain, cookie, xCsrfToken) }
                .build()
        val request = Request.Builder()
                .url(BASE_WS_URL)
                .build()
        val listener = EchoWebSocketListener(distributionData)
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

    private inner class EchoWebSocketListener(var distributionData: DistributionInfoResponse.DistributionData) : WebSocketListener() {

        private var dataHolderMap = mutableMapOf<String, TextView>()

        override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
            output("onOpen : $response")

            matchTextKeyWithMappingId()
            subscribeViews(webSocket)
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            output("Receiving : $text")

            updateViewText(text)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String?) {
            dataHolderMap.clear()
            webSocket.close(NORMAL_CLOSURE_STATUS, null)
            output("Closing : $code / $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
            output("Error : " + t.message)
        }

        private fun matchTextKeyWithMappingId() {
            val mappingData = stringDataManager?.getMapping(sourceLanguage) ?: return
            val viewsWithData = viewTransformerManager.getVisibleViewsWithData()
            for (entry in viewsWithData) {
                val textMetaData = entry.value
                val mappingValue = getMappingValueForKey(textMetaData, mappingData)
                mappingValue?.let { dataHolderMap.put(mappingValue, entry.key) }
            }
        }

        private fun subscribeViews(webSocket: WebSocket) {
            val project = distributionData.project
            val user = distributionData.user

            for (viewDataHolder in dataHolderMap) {
                webSocket.send(
                        SubscribeUpdateEvent(project.wsHash,
                                project.id,
                                user.id,
                                Locale.getDefault().language,
                                viewDataHolder.key).toString())
            }
        }

        private fun updateViewText(text: String?) {
            text?.let {
                val eventData = parseResponse(it)
                val event = eventData.event
                if (event.contains(UPDATE_DRAFT)) {
                    val mappingId = event.split(":").last()
                    Handler(Looper.getMainLooper()).post {
                        dataHolderMap[mappingId]?.text = fromHtml(eventData.data.text)
                    }
                }
            }
        }

        private fun parseResponse(response: String): EventResponse {
            return Gson().fromJson(response, EventResponse::class.java)
        }

        private fun output(txt: String) {
            Log.d(TAG, txt)
        }
    }
}