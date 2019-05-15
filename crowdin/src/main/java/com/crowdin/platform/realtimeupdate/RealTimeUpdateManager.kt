package com.crowdin.platform.realtimeupdate

import android.util.Log
import com.crowdin.platform.data.StringDataManager
import com.crowdin.platform.data.remote.api.CrowdinApi
import com.crowdin.platform.data.remote.api.DistributionInfoResponse
import com.crowdin.platform.data.remote.api.EventResponse
import com.crowdin.platform.transformer.ViewTransformerManager
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


internal class RealTimeUpdateManager(
        private val crowdinApi: CrowdinApi,
        private val distributionKey: String?,
        private val stringDataManager: StringDataManager?,
        private val viewTransformerManager: ViewTransformerManager) {

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
        private const val BASE_WS_URL = "wss://ws-lb.crowdin.com/"
        private const val COOKIE = "Cookie"
    }

    private var ws: WebSocket? = null

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
                .addInterceptor { chain ->
                    val original = chain.request()
                    val authorized = original.newBuilder()
                            .addHeader(COOKIE, cookie)
                            .addHeader("x-csrf-token", xCsrfToken)
                            .build()
                    chain.proceed(authorized)
                }
                .build()

        val request = Request.Builder()
                .url(BASE_WS_URL)
                .build()
        val listener = EchoWebSocketListener(distributionData)
        ws = client.newWebSocket(request, listener)
        client.dispatcher().executorService().shutdown()
    }

    fun closeConnection() {
        ws?.close(NORMAL_CLOSURE_STATUS, "Goodbye !")
    }

    private inner class EchoWebSocketListener(var distributionData: DistributionInfoResponse.DistributionData) : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
            val viewDataList = viewTransformerManager.getViewData()
            val project = distributionData.project
            val user = distributionData.user

            webSocket.send("{\"action\": \"subscribe\", \"event\": \"update-draft:${project.wsHash}:${project.id}:${user.id}:de:2448\"}")
            webSocket.send("{\"action\": \"subscribe\", \"event\": \"top-suggestion:${project.wsHash}:${project.id}:de:2448\"}")

            output("onOpen : $response")
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            output("Receiving : $text")
            text?.let {
                val eventData = parseResponse(it)
                val event = eventData.event
                if (event.contains("update-draft")) {
                    val mappingId = event.split(":").last()

                }
            }
        }

        override fun onMessage(webSocket: WebSocket?, bytes: ByteString) {
            output("Receiving bytes : " + bytes.hex())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String?) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null)
            output("Closing : $code / $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
            output("Error : " + t.message)
        }
    }

    private fun parseResponse(response: String): EventResponse {
        return Gson().fromJson(response, EventResponse::class.java)
    }

    private fun output(txt: String) {
        Log.d("TAG", txt)
    }
}