package com.crowdin.platform.realtimeupdate

import android.icu.text.PluralRules
import android.os.Build
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.crowdin.platform.Crowdin
import com.crowdin.platform.compose.ComposeStringRepository
import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.getMappingValueForKey
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.TextMetaData
import com.crowdin.platform.data.remote.api.DistributionInfoResponse
import com.crowdin.platform.data.remote.api.EventResponse
import com.crowdin.platform.realtimeupdate.RealTimeUpdateManager.Companion.NORMAL_CLOSURE_STATUS
import com.crowdin.platform.realtimeupdate.RealTimeUpdateManager.Companion.PLURAL_NONE
import com.crowdin.platform.transformer.ViewTransformerManager
import com.crowdin.platform.transformer.ViewsChangeListener
import com.crowdin.platform.util.ThreadUtils
import com.crowdin.platform.util.fromHtml
import com.crowdin.platform.util.getLocale
import com.crowdin.platform.util.unEscapeQuotes
import com.google.gson.Gson
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.Collections
import java.util.WeakHashMap

private const val UPDATE_DRAFT = "update-draft"
private const val TOP_SUGGESTION = "top-suggestion"

internal class EchoWebSocketListener(
    private val dataManager: DataManager,
    private var mappingData: LanguageData,
    private var distributionData: DistributionInfoResponse.DistributionData,
    private var viewTransformerManager: ViewTransformerManager,
    private var composeRepository: ComposeStringRepository?,
    private var languageCode: String,
) : WebSocketListener() {
    private var dataHolderMap = Collections.synchronizedMap(WeakHashMap<TextView, TextMetaData>())
    private val composeDataHolderMap = Collections.synchronizedMap(mutableMapOf<String, TextMetaData>())

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOpen(
        webSocket: WebSocket,
        response: okhttp3.Response,
    ) {
        Log.d(Crowdin.CROWDIN_TAG, "WebSocket Opened")

        val project = distributionData.project
        val user = distributionData.user

        saveMatchedTextViewWithMappingId(mappingData)
        subscribeExistingComposeWatchers(webSocket, project, user)

        ThreadUtils.runInBackgroundPool({
            subscribeViews(webSocket, project, user)
        }, false)

        viewTransformerManager.setOnViewsChangeListener(
            object : ViewsChangeListener {
                override fun onChange(pair: Pair<TextView, TextMetaData>) {
                    ThreadUtils.runInBackgroundPool({
                        resubscribeView(pair, webSocket, project, user)
                    }, false)
                }
            },
        )

        composeRepository?.setWebSocketCallbacks(
            onWatcherRegistered = { watcher ->
                subscribeComposeWatcher(
                    webSocket,
                    watcher,
                    distributionData.project,
                    distributionData.user
                )
            },
            onWatcherDeregistered = { watcher ->
                removeComposeWatcher(watcher)
            },
        )
    }

    private fun resubscribeView(
        pair: Pair<TextView, TextMetaData>,
        webSocket: WebSocket,
        project: DistributionInfoResponse.DistributionData.ProjectData,
        user: DistributionInfoResponse.DistributionData.UserData,
    ) {
        dataHolderMap.clear()
        saveMatchedTextViewWithMappingId(mappingData)
        val mappingValue = getMappingValueForKey(pair.second, mappingData)
        mappingValue.value?.let {
            subscribeView(webSocket, project, user, it)
        }
    }

    override fun onMessage(
        webSocket: WebSocket,
        text: String,
    ) {
        Log.d(Crowdin.CROWDIN_TAG, "EchoWebSocket. onMessage: $text")
        handleMessage(text)
    }

    override fun onClosing(
        webSocket: WebSocket,
        code: Int,
        reason: String,
    ) {
        try {
            dataManager.clearSocketData()
            dataHolderMap.clear()
            composeDataHolderMap.clear()
            webSocket.close(NORMAL_CLOSURE_STATUS, reason)
        } catch (e: Exception) {
            Log.w(Crowdin.CROWDIN_TAG, "Error during WebSocket cleanup", e)
        }
        output("Closing : $code / $reason")
    }

    override fun onFailure(
        webSocket: WebSocket,
        throwable: Throwable,
        response: okhttp3.Response?,
    ) {
        output("Error : " + throwable.message)
    }

    private fun saveMatchedTextViewWithMappingId(mappingData: LanguageData) {
        val viewsWithData = viewTransformerManager.getVisibleViewsWithData()
        for (entry in viewsWithData) {
            val textMetaData = entry.value
            val mapping = getMappingValueForKey(textMetaData, mappingData)
            mapping.value?.let {
                textMetaData.mappingValue = it
                textMetaData.isHint = mapping.isHint
                dataHolderMap.put(entry.key, textMetaData)
            }
        }
    }

    private fun subscribeViews(
        webSocket: WebSocket,
        project: DistributionInfoResponse.DistributionData.ProjectData,
        user: DistributionInfoResponse.DistributionData.UserData,
    ) {
        try {
            for (viewDataHolder in dataHolderMap) {
                val mappingValue = viewDataHolder.value.mappingValue
                subscribeView(webSocket, project, user, mappingValue)
            }
        } catch (exception: Exception) {
            Log.e(Crowdin.CROWDIN_TAG, "EchoWebSocketListener Exception", exception)
        }
    }

    private fun subscribeView(
        webSocket: WebSocket,
        project: DistributionInfoResponse.DistributionData.ProjectData,
        user: DistributionInfoResponse.DistributionData.UserData,
        mappingValue: String,
    ) {
        try {
            val updateEvent = "$UPDATE_DRAFT:${project.wsHash}:${project.id}:${user.id}:$languageCode:$mappingValue"
            dataManager.getTicket(updateEvent)?.let {
                webSocket.send(getSubscribeEventJson(updateEvent, it))
            }
        } catch (throwable: Throwable) {
            Log.e(Crowdin.CROWDIN_TAG, "Get ticket for update event failed", throwable)
        }

        try {
            val suggestionEvent = "$TOP_SUGGESTION:${project.wsHash}:${project.id}:$languageCode:$mappingValue"
            dataManager.getTicket(suggestionEvent)?.let {
                webSocket.send(getSubscribeEventJson(suggestionEvent, it))
            }
        } catch (throwable: Throwable) {
            Log.e(Crowdin.CROWDIN_TAG, "Get ticket for suggestion event failed", throwable)
        }
    }

    private fun getSubscribeEventJson(
        eventType: String,
        ticket: String,
    ): String =
        "{" +
            "\"action\":\"subscribe\", " +
            "\"event\":\"$eventType\", " +
            "\"ticket\": \"$ticket\"" +
            "}"

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
                updateMatchedComposeComponents(mappingId, eventData)
            }
        }
    }

    private fun updateMatchedView(
        eventData: EventResponse.EventData,
        mutableEntry: MutableMap.MutableEntry<TextView, TextMetaData>,
        textMetaData: TextMetaData,
    ) {
        val text = eventData.text
        val view = mutableEntry.key

        if (eventData.pluralForm == null || eventData.pluralForm == PLURAL_NONE) {
            updateViewText(view, text, textMetaData.isHint)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val locale = view.resources.configuration.getLocale()
                val quantity = textMetaData.pluralQuantity
                val rule = PluralRules.forLocale(locale)
                val ruleName = rule.select(quantity.toDouble())
                if (eventData.pluralForm == ruleName) {
                    updateViewText(view, text, textMetaData.isHint)
                }
            }
        }
    }

    private fun updateViewText(
        view: TextView?,
        text: String,
        isHint: Boolean,
    ) {
        view?.post {
            val textFormatted = text.unEscapeQuotes().fromHtml()
            if (isHint) {
                view.hint = textFormatted
            } else {
                view.text = textFormatted
            }
        }
    }

    private fun parseResponse(response: String): EventResponse = Gson().fromJson(response, EventResponse::class.java)

    private fun output(message: String) {
        Log.d(EchoWebSocketListener::class.java.simpleName, message)
    }

    /**
     * Subscribe existing Compose watchers when WebSocket connection opens.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun subscribeExistingComposeWatchers(
        webSocket: WebSocket,
        project: DistributionInfoResponse.DistributionData.ProjectData,
        user: DistributionInfoResponse.DistributionData.UserData,
    ) {
        Log.d(Crowdin.CROWDIN_TAG, "Subscribing existing Compose watchers")
        composeRepository?.getActiveWatchers()?.forEach { watcher ->
            subscribeComposeWatcher(webSocket, watcher, project, user)
        }
    }

    /**
     * Add a Compose watcher to the tracking system.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun addComposeWatcher(textMetaData: TextMetaData) {
        getMappingValueForKey(textMetaData, mappingData).value?.let { mappingValue ->
            composeDataHolderMap.putIfAbsent(mappingValue, textMetaData)
        }
    }

    /**
     * Subscribe a new Compose watcher to WebSocket events.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun subscribeComposeWatcher(
        webSocket: WebSocket,
        watcher: TextMetaData,
        project: DistributionInfoResponse.DistributionData.ProjectData,
        user: DistributionInfoResponse.DistributionData.UserData,
    ) {
        val logMessage = "Subscribing new Compose watcher: ${
            getMappingValueForKey(
                watcher,
                mappingData
            ).value
        }"
        Log.d(Crowdin.CROWDIN_TAG, logMessage)
        addComposeWatcher(watcher)

        // Get the current WebSocket and subscribe if connection is active
        ThreadUtils.runInBackgroundPool({
            try {
                subscribeView(
                    webSocket = webSocket,
                    project = project,
                    user = user,
                    mappingValue = getMappingValueForKey(watcher, mappingData).value ?: "",
                )
            } catch (e: Exception) {
                Log.w(
                    Crowdin.CROWDIN_TAG,
                    "Failed to subscribe new Compose watcher: ${e.message}",
                    e
                )
            }
        }, false)
    }

    /**
     * Remove a Compose watcher from the tracking system.
     */
    fun removeComposeWatcher(watcher: TextMetaData) {
        getMappingValueForKey(watcher, mappingData).value?.let { mappingValue ->
            composeDataHolderMap.remove(mappingValue)
        }
    }

    /**
     * Update matched Compose components when WebSocket message is received.
     */
    private fun updateMatchedComposeComponents(
        mappingId: String,
        eventData: EventResponse.EventData,
    ) {
        Log.d(Crowdin.CROWDIN_TAG, "Updating Compose components for mapping ID: $mappingId")

        val textData = composeDataHolderMap[mappingId]
        if (textData == null) {
            Log.d(Crowdin.CROWDIN_TAG, "No Compose watchers found for mapping ID: $mappingId")
            return
        }

        if (composeRepository == null) {
            Log.w(Crowdin.CROWDIN_TAG, "Compose repository not available for update")
            return
        }

        if (eventData.pluralForm == null || eventData.pluralForm == PLURAL_NONE) {
            // Ensure update is called on main thread since Compose state updates require it
            ThreadUtils.executeOnMain {
                composeRepository?.updateStringFromWebSocket(textData.resourceId, eventData.text)
            }
        }
    }
}
