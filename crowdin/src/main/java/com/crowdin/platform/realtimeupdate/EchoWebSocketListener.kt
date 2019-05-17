package com.crowdin.platform.realtimeupdate

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import com.crowdin.platform.data.getMappingValueForKey
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.TextMetaData
import com.crowdin.platform.data.remote.api.DistributionInfoResponse
import com.crowdin.platform.data.remote.api.EventResponse
import com.crowdin.platform.fromHtml
import com.crowdin.platform.realtimeupdate.RealTimeUpdateManager.Companion.NORMAL_CLOSURE_STATUS
import com.crowdin.platform.transformer.ViewTransformerManager
import com.crowdin.platform.transformer.ViewsChangeListener
import com.google.gson.Gson
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.ConcurrentHashMap

internal class EchoWebSocketListener(var mappingData: LanguageData,
                                     var distributionData: DistributionInfoResponse.DistributionData,
                                     var viewTransformerManager: ViewTransformerManager) : WebSocketListener() {

    private var TAG = EchoWebSocketListener::class.java.simpleName
    private var dataHolderMap = ConcurrentHashMap<WeakReference<TextView>, String>()

    override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
        output("onOpen : $response")

        val project = distributionData.project
        val user = distributionData.user

        saveMatchedTextViewWithMappingId(mappingData)
        subscribeViews(webSocket, project, user)

        viewTransformerManager.setOnViewsChangeListener(object : ViewsChangeListener {
            override fun onChange(pair: Pair<TextView, TextMetaData>) {
                removeNullable(dataHolderMap)

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
        val viewsWithData = viewTransformerManager.getVisibleViewsWithData()
        for (entry in viewsWithData) {
            val textMetaData = entry.value
            val mappingValue = getMappingValueForKey(textMetaData, mappingData)
            mappingValue?.let { dataHolderMap.put(WeakReference(entry.key), mappingValue) }
        }
    }

    private fun addOrReplaceMatchedView(pair: Pair<TextView, TextMetaData>, mappingData: LanguageData): String? {
        val textMetaData = pair.second
        val mappingValue = getMappingValueForKey(textMetaData, mappingData)
        val viewWeakRef = WeakReference(pair.first)
        mappingValue?.let { dataHolderMap.put(viewWeakRef, mappingValue) }

        return mappingValue
    }


    private fun subscribeViews(webSocket: WebSocket,
                               project: DistributionInfoResponse.DistributionData.ProjectData,
                               user: DistributionInfoResponse.DistributionData.UserData) {
        for (viewDataHolder in dataHolderMap) {
            val mappingValue = viewDataHolder.value
            subscribeView(webSocket, project, user, mappingValue)
        }
    }

    private fun subscribeView(webSocket: WebSocket,
                              project: DistributionInfoResponse.DistributionData.ProjectData,
                              user: DistributionInfoResponse.DistributionData.UserData,
                              mappingValue: String) {
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
                    for (mutableEntry in dataHolderMap) {
                        if (mutableEntry.value == mappingId) {
                            mutableEntry.key.get()?.text = fromHtml(eventData.data.text)
                        }
                    }
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

    private fun removeNullable(dataHolderMap: ConcurrentHashMap<WeakReference<TextView>, String>) {
        for (key in dataHolderMap.keys) {
            if (key.get() == null) {
                dataHolderMap.remove(key)
            }
        }
    }
}
