package com.crowdin.platform.realtimeupdate

import android.icu.text.PluralRules
import android.os.Build
import android.util.Log
import android.widget.TextView
import com.crowdin.platform.data.getMappingValueForKey
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.TextMetaData
import com.crowdin.platform.data.remote.api.DistributionInfoResponse
import com.crowdin.platform.data.remote.api.EventResponse
import com.crowdin.platform.fromHtml
import com.crowdin.platform.realtimeupdate.RealTimeUpdateManager.Companion.NORMAL_CLOSURE_STATUS
import com.crowdin.platform.realtimeupdate.RealTimeUpdateManager.Companion.PLURAL_NONE
import com.crowdin.platform.transformer.ViewTransformerManager
import com.crowdin.platform.transformer.ViewsChangeListener
import com.crowdin.platform.util.ThreadUtils
import com.google.gson.Gson
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.lang.ref.WeakReference
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

internal class EchoWebSocketListener(
    private var mappingData: LanguageData,
    private var distributionData: DistributionInfoResponse.DistributionData,
    private var viewTransformerManager: ViewTransformerManager,
    private var languageCode: String
) : WebSocketListener() {

    private var dataHolderMap = ConcurrentHashMap<WeakReference<TextView>, TextMetaData>()

    override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
        output("onOpen")

        val project = distributionData.project
        val user = distributionData.user

        saveMatchedTextViewWithMappingId(mappingData)
        subscribeViews(webSocket, project, user)

        viewTransformerManager.setOnViewsChangeListener(object : ViewsChangeListener {
            override fun onChange(pair: Pair<TextView, TextMetaData>) {
                ThreadUtils.runInBackgroundPool(Runnable {
                    resubscribeView(pair, webSocket, project, user)
                }, false)
            }
        })
    }

    private fun resubscribeView(
        pair: Pair<TextView, TextMetaData>,
        webSocket: WebSocket,
        project: DistributionInfoResponse.DistributionData.ProjectData,
        user: DistributionInfoResponse.DistributionData.UserData
    ) {
        dataHolderMap.clear()
        saveMatchedTextViewWithMappingId(mappingData)
        val mappingValue = getMappingValueForKey(pair.second, mappingData)
        mappingValue?.let {
            subscribeView(webSocket, project, user, it)
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        handleMessage(text)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        dataHolderMap.clear()
        webSocket.close(NORMAL_CLOSURE_STATUS, reason)
        output("Closing : $code / $reason")
    }

    override fun onFailure(
        webSocket: WebSocket,
        throwable: Throwable,
        response: okhttp3.Response?
    ) {
        output("Error : " + throwable.message)
    }

    private fun saveMatchedTextViewWithMappingId(mappingData: LanguageData) {
        val viewsWithData = viewTransformerManager.getVisibleViewsWithData()
        for (entry in viewsWithData) {
            val textMetaData = entry.value
            val mappingValue = getMappingValueForKey(textMetaData, mappingData)
            mappingValue?.let {
                textMetaData.mappingValue = mappingValue
                dataHolderMap.put(WeakReference(entry.key), textMetaData)
            }
        }
    }

    private fun subscribeViews(
        webSocket: WebSocket,
        project: DistributionInfoResponse.DistributionData.ProjectData,
        user: DistributionInfoResponse.DistributionData.UserData
    ) {
        for (viewDataHolder in dataHolderMap) {
            val mappingValue = viewDataHolder.value.mappingValue
            subscribeView(webSocket, project, user, mappingValue)
        }
    }

    private fun subscribeView(
        webSocket: WebSocket,
        project: DistributionInfoResponse.DistributionData.ProjectData,
        user: DistributionInfoResponse.DistributionData.UserData,
        mappingValue: String
    ) {
        webSocket.send(
            SubscribeUpdateEvent(
                project.wsHash,
                project.id,
                user.id,
                languageCode,
                mappingValue
            )
                .toString()
        )

        webSocket.send(
            SubscribeSuggestionEvent(
                project.wsHash,
                project.id,
                languageCode,
                mappingValue
            )
                .toString()
        )
    }

    private fun handleMessage(message: String?) {
        message?.let {
            val eventResponse = parseResponse(it)
            val event = eventResponse.event
            val eventData = eventResponse.data

            if (event.contains(UPDATE_DRAFT) || event.contains(TOP_SUGGESTION)) {
                val mappingId = event.split(":").last()
                for (mutableEntry in dataHolderMap) {
                    val textMetaData = mutableEntry.value
                    if (textMetaData.mappingValue == mappingId) {
                        updateMatchedView(eventData, mutableEntry, textMetaData)
                    }
                }
            }
        }
    }

    private fun updateMatchedView(
        eventData: EventResponse.EventData,
        mutableEntry: MutableMap.MutableEntry<WeakReference<TextView>, TextMetaData>,
        textMetaData: TextMetaData
    ) {
        val text = eventData.text
        val view = mutableEntry.key.get()

        if (eventData.pluralForm == null || eventData.pluralForm == PLURAL_NONE) {
            updateViewText(view, text)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val quantity = textMetaData.pluralQuantity
                val rule = PluralRules.forLocale(Locale.getDefault())
                val ruleName = rule.select(quantity.toDouble())
                if (eventData.pluralForm == ruleName) {
                    updateViewText(view, text)
                }
            }
        }
    }

    private fun updateViewText(view: TextView?, text: String) {
        view?.post { view.text = text.fromHtml() }
    }

    private fun parseResponse(response: String): EventResponse {
        return Gson().fromJson(response, EventResponse::class.java)
    }

    private fun output(message: String) {
        Log.d(EchoWebSocketListener::class.java.simpleName, message)
    }
}
