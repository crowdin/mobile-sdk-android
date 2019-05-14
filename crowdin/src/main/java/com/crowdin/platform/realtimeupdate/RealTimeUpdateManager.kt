package com.crowdin.platform.realtimeupdate

import android.util.Log
import com.crowdin.platform.data.StringDataManager
import com.crowdin.platform.data.remote.api.CrowdinApi
import com.crowdin.platform.transformer.ViewTransformerManager
import okhttp3.*
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
        private const val BASE_URL = "https://crowdin.com/backend/distributions/get_info?distribution_hash="
        private const val COOKIE = "Cookie"
    }

    private var ws: WebSocket? = null

    fun openConnection(agent: String?) {
        distributionKey ?: return
        stringDataManager ?: return

        val cookie = stringDataManager.getCookies()
        cookie ?: return
        getInfo(agent!!, distributionKey, cookie)

        // TODO: update
//        createConnection(cookie)
    }

    private fun getInfo(agent: String, distributionKey: String, cookie: String) {
        crowdinApi.getInfo(agent, cookie, distributionKey).enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("TAG", "TEST")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("TAG", "TEST")
            }
        })
    }

    private fun createConnection(cookie: String) {
        val client = OkHttpClient().newBuilder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    val authorized = original.newBuilder()
                            .addHeader(COOKIE, cookie)
                            .build()
                    chain.proceed(authorized)
                }
                .build()

        val request = Request.Builder()
                .url("$BASE_URL$distributionKey")
                .build()
        val listener = EchoWebSocketListener()
        ws = client.newWebSocket(request, listener)
        client.dispatcher().executorService().shutdown()
    }

    fun closeConnection() {
        ws?.close(NORMAL_CLOSURE_STATUS, "Goodbye !")
    }

    private inner class EchoWebSocketListener : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
            output("onOpen : $response")
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