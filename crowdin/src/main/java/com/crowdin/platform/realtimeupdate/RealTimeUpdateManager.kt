package com.crowdin.platform.realtimeupdate

import android.util.Log
import com.crowdin.platform.data.StringDataManager
import com.crowdin.platform.data.remote.api.CrowdinApi
import com.crowdin.platform.data.remote.api.DistributionData
import com.crowdin.platform.data.remote.api.DistributionInfoResponse
import com.crowdin.platform.transformer.ViewTransformerManager
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

    private fun createConnection(distributionData: DistributionData, cookie: String, xCsrfToken: String) {
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

    private inner class EchoWebSocketListener(var distributionData: DistributionData) : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
            val project = distributionData.project
            val user = distributionData.user

            webSocket.send("{action: \"subscribe\", event: \"update-draft:${project.wsHash}:${project.id}:${user.id}:de:2448\"}")
            output("onOpen : $response")
            // TODO: subscribe to event
//            update-draft:<projectWsHash>:<projectId>:<userId>:<language_code>:<stringId>
//            `update-draft:ud8923u:25:123:uk:12`
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            output("Receiving : " + text!!)
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

    private fun output(txt: String) {
        Log.d("TAG", txt)
    }
}