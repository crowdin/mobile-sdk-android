package com.crowdin.platform.realtimeupdate

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import com.crowdin.platform.data.StringDataManager
import com.crowdin.platform.data.getMappingValueForKey
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.TextMetaData
import com.crowdin.platform.data.remote.api.CrowdinApi
import com.crowdin.platform.data.remote.api.DistributionInfoResponse
import com.crowdin.platform.data.remote.api.EventResponse
import com.crowdin.platform.fromHtml
import com.crowdin.platform.transformer.ViewTransformerManager
import com.crowdin.platform.transformer.ViewsChangeListener
import com.google.gson.Gson
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.ConcurrentHashMap


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
        viewTransformerManager.setOnViewsChangeListener(null)
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

        private var dataHolderMap = ConcurrentHashMap<String, WeakReference<TextView>>()

        override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
            output("onOpen : $response")

            val mappingData = stringDataManager?.getMapping(sourceLanguage) ?: return
            val project = distributionData.project
            val user = distributionData.user

            saveMatchedTextViewWithMappingId(mappingData)
            subscribeViews(webSocket, project, user)

            viewTransformerManager.setOnViewsChangeListener(object : ViewsChangeListener {
                override fun onChange(pair: Pair<TextView, TextMetaData>) {
                    output("onChange")
                    val mappingValue = addOrReplaceMatchedView(pair, mappingData)
                    mappingValue?.let {
                        subscribeView(webSocket, project, user, it)
                    }
                }
            })
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

        private fun saveMatchedTextViewWithMappingId(mappingData: LanguageData) {
            dataHolderMap.clear()
            val viewsWithData = viewTransformerManager.getVisibleViewsWithData()
            for (entry in viewsWithData) {
                val textMetaData = entry.value
                val mappingValue = getMappingValueForKey(textMetaData, mappingData)
                mappingValue?.let { dataHolderMap.put(mappingValue, WeakReference(entry.key)) }
            }
        }

        private fun addOrReplaceMatchedView(pair: Pair<TextView, TextMetaData>, mappingData: LanguageData): String? {
            val textMetaData = pair.second
            val mappingValue = getMappingValueForKey(textMetaData, mappingData)
            val viewWeakRef = WeakReference(pair.first)
            mappingValue?.let { dataHolderMap.put(mappingValue, viewWeakRef) }
            output("Add/Replace:$mappingValue:key:${textMetaData.textAttributeKey}")

            return mappingValue
        }


        private fun subscribeViews(webSocket: WebSocket,
                                   project: DistributionInfoResponse.DistributionData.ProjectData,
                                   user: DistributionInfoResponse.DistributionData.UserData) {
            output("SIZE: ${dataHolderMap.size}")

            for (viewDataHolder in dataHolderMap) {
                val mappingValue = viewDataHolder.key
                subscribeView(webSocket, project, user, mappingValue)
            }
        }

        private fun subscribeView(webSocket: WebSocket,
                                  project: DistributionInfoResponse.DistributionData.ProjectData,
                                  user: DistributionInfoResponse.DistributionData.UserData,
                                  mappingValue: String) {
            output("SIZE: ${dataHolderMap.size}")
            webSocket.send(SubscribeUpdateEvent(project.wsHash,
                    project.id,
                    user.id,
                    Locale.getDefault().language,
                    mappingValue).toString())
        }

        private fun updateViewText(text: String?) {
            text?.let {
                val eventData = parseResponse(it)
                val event = eventData.event
                if (event.contains(UPDATE_DRAFT)) {
                    val mappingId = event.split(":").last()
                    Handler(Looper.getMainLooper()).post {
                        dataHolderMap[mappingId]?.get()?.text = fromHtml(eventData.data.text)
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