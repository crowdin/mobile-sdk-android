package com.crowdin.platform.compose

import android.content.Context
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.crowdin.platform.Crowdin
import com.crowdin.platform.CrowdinResources
import com.crowdin.platform.data.model.TextMetaData
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Repository for managing stateful strings in Jetpack Compose.
 * This class is only instantiated and used if compose is enabled.
 */
internal class ComposeStringRepository(
    private val context: Context,
    private val crowdinResources: CrowdinResources,
) {

    private data class WatchedState<T>(
        val state: MutableState<T>,
        var watcherCount: AtomicInteger = AtomicInteger(0),
    )

    private val resourceIDStringStateMap = ConcurrentHashMap<Int, WatchedState<String>>()

    // Track active watchers for WebSocket integration
    private val activeWatchers = ConcurrentHashMap<Int, TextMetaData>()

    // Callback for notifying WebSocket system about new watchers
    private var onWatcherRegistered: ((TextMetaData) -> Unit)? = null
    private var onWatcherDeregistered: ((TextMetaData) -> Unit)? = null

    /**
     * Get a state for a string resource ID.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun getStringState(resourceId: Int): State<String> {
        val watchedState =
            resourceIDStringStateMap.computeIfAbsent(resourceId) {
                val state = mutableStateOf(crowdinResources.getString(resourceId))
                WatchedState(state)
            }
        return watchedState.state
    }

    /**
     * Register a watcher for a resource ID.
     * getStringState must be called before this to ensure the state is created.
     */
    fun registerWatcher(resourceId: Int) {
        Log.d(Crowdin.CROWDIN_TAG, "Registering watcher for resource ID: $resourceId")
        resourceIDStringStateMap[resourceId]?.watcherCount?.incrementAndGet() ?: run {
            Log.w(
                Crowdin.CROWDIN_TAG,
                "registerWatcher called before getStringState for resource ID: $resourceId"
            )
        }

        // Register with WebSocket system if this is the first watcher for this resource
        if (!activeWatchers.containsKey(resourceId)) {
            try {
                val resourceKey = context.resources.getResourceEntryName(resourceId)

                val textMetaData =
                    TextMetaData().apply {
                        textAttributeKey = resourceKey
                        stringDefault = context.resources.getString(resourceId)
                        this.resourceId = resourceId
                    }

                val existingWatcher = activeWatchers.putIfAbsent(resourceId, textMetaData)
                if (existingWatcher == null) {
                    onWatcherRegistered?.invoke(textMetaData)
                }
            } catch (e: Exception) {
                Log.w(
                    Crowdin.CROWDIN_TAG,
                    "Failed to register WebSocket watcher for resource $resourceId",
                    e
                )
            }
        }
    }

    /**
     * Deregister a watcher for a resource ID.
     */
    fun deRegisterWatcher(resourceId: Int) {
        Log.d(Crowdin.CROWDIN_TAG, "Deregister watcher for resource ID: $resourceId")

        var shouldRemoveWatcher = false

        resourceIDStringStateMap[resourceId]?.let { watchedState ->
            val newWatcherCount = watchedState.watcherCount.decrementAndGet()
            if (newWatcherCount <= 0) {
                resourceIDStringStateMap.remove(resourceId)
                shouldRemoveWatcher = true
            }
        }

        // Deregister from WebSocket system if no more watchers for this resource
        if (shouldRemoveWatcher ||
            (!resourceIDStringStateMap.containsKey(resourceId))
        ) {
            activeWatchers[resourceId]?.let { watcher ->
                activeWatchers.remove(resourceId)
                onWatcherDeregistered?.invoke(watcher)
            }
        }
    }

    fun setWebSocketCallbacks(
        onWatcherRegistered: ((TextMetaData) -> Unit)?,
        onWatcherDeregistered: ((TextMetaData) -> Unit)?,
    ) {
        this.onWatcherRegistered = onWatcherRegistered
        this.onWatcherDeregistered = onWatcherDeregistered
    }

    fun updateStringFromWebSocket(
        resourceId: Int,
        newValue: String,
    ) {
        try {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                throw IllegalStateException("updateStringFromWebSocket must be called on main thread")
            }

            // Update caches directly (caller ensures we're on main thread)
            resourceIDStringStateMap[resourceId]?.state?.value = newValue

            Log.d(
                Crowdin.CROWDIN_TAG,
                "Updated WebSocket string (internal) for resource $resourceId"
            )
        } catch (e: Exception) {
            Log.e(
                Crowdin.CROWDIN_TAG,
                "Failed to update string from WebSocket (internal) for resource $resourceId",
                e
            )
        }
    }

    /**
     * Get active watchers for WebSocket integration.
     */
    fun getActiveWatchers(): Collection<TextMetaData> = activeWatchers.values

    fun forceUpdate() {
        // Force update all strings by clearing the cache
        resourceIDStringStateMap.forEach { (resourceId, watchedState) ->
            watchedState.state.value = crowdinResources.getString(resourceId)
        }
        Log.d(Crowdin.CROWDIN_TAG, "Force updated all strings in ComposeStringRepository")
    }
}
