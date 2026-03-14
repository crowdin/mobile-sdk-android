package com.crowdin.platform.compose

import android.content.Context
import android.icu.text.PluralRules
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
import com.crowdin.platform.util.getLocale
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

    private data class PluralResourceKey(
        val resourceId: Int,
        val pluralForm: String,
    )

    private data class PluralWatchedState(
        val state: MutableState<String>,
        @Volatile var quantityHint: Int,
    )

    private val resourceIDStringStateMap = ConcurrentHashMap<Int, WatchedState<String>>()
    private val pluralStringStateMap = ConcurrentHashMap<PluralResourceKey, PluralWatchedState>()
    private val pluralResourceWatcherCountMap = ConcurrentHashMap<Int, AtomicInteger>()

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
     * Get a state for a plural resource ID and quantity.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun getPluralState(
        resourceId: Int,
        quantity: Int,
        pluralForm: String = resolvePluralForm(quantity),
    ): State<String> {
        val key = PluralResourceKey(resourceId, pluralForm)
        val watchedState =
            pluralStringStateMap.computeIfAbsent(key) {
                val state = mutableStateOf(crowdinResources.getQuantityString(resourceId, quantity))
                PluralWatchedState(state = state, quantityHint = quantity)
            }

        watchedState.quantityHint = quantity
        return watchedState.state
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getPluralForm(quantity: Int): String = resolvePluralForm(quantity)

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

        registerActiveWatcher(resourceId) {
            TextMetaData().apply {
                textAttributeKey = context.resources.getResourceEntryName(resourceId)
                stringDefault = context.resources.getString(resourceId)
                this.resourceId = resourceId
            }
        }
    }

    fun registerPluralResourceWatcher(resourceId: Int) {
        Log.d(
            Crowdin.CROWDIN_TAG,
            "Registering plural resource watcher for resource ID: $resourceId"
        )

        pluralResourceWatcherCountMap.compute(resourceId) { _, watcherCount ->
            (watcherCount ?: AtomicInteger(0)).apply { incrementAndGet() }
        }

        registerActiveWatcher(resourceId) {
            TextMetaData().apply {
                pluralName = context.resources.getResourceEntryName(resourceId)
                pluralQuantity = 0
                this.resourceId = resourceId
            }
        }
    }

    private fun registerActiveWatcher(
        resourceId: Int,
        watcherFactory: () -> TextMetaData,
    ) {

        // Register with WebSocket system if this is the first watcher for this resource
        if (!activeWatchers.containsKey(resourceId)) {
            try {
                val textMetaData = watcherFactory()
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

        resourceIDStringStateMap[resourceId]?.let { watchedState ->
            val newWatcherCount = watchedState.watcherCount.decrementAndGet()
            if (newWatcherCount <= 0) {
                resourceIDStringStateMap.remove(resourceId)
            }
        }

        removeActiveWatcherIfUnused(resourceId)
    }

    fun deRegisterPluralResourceWatcher(resourceId: Int) {
        Log.d(Crowdin.CROWDIN_TAG, "Deregister plural resource watcher for resource ID: $resourceId")

        pluralResourceWatcherCountMap[resourceId]?.let { watcherCount ->
            if (watcherCount.decrementAndGet() <= 0) {
                watcherCount.set(0)
                pluralResourceWatcherCountMap.remove(resourceId)
                pluralStringStateMap.keys.removeIf { it.resourceId == resourceId }
            }
        }

        removeActiveWatcherIfUnused(resourceId)
    }

    private fun removeActiveWatcherIfUnused(resourceId: Int) {
        val hasStringWatchers = resourceIDStringStateMap.containsKey(resourceId)
        val hasPluralWatchers = pluralResourceWatcherCountMap.containsKey(resourceId)

        // Deregister from WebSocket system if no more watchers for this resource
        if (!hasStringWatchers && !hasPluralWatchers) {
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
            ensureMainThread("updateStringFromWebSocket")

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

    @RequiresApi(Build.VERSION_CODES.N)
    fun updatePluralFromWebSocket(
        resourceId: Int,
        pluralForm: String,
        newValue: String,
    ) {
        try {
            ensureMainThread("updatePluralFromWebSocket")

            val key = PluralResourceKey(resourceId, pluralForm)
            pluralStringStateMap[key]?.state?.value = newValue

            Log.d(
                Crowdin.CROWDIN_TAG,
                "Updated WebSocket plural (internal) for resource $resourceId, form: $pluralForm"
            )
        } catch (e: Exception) {
            Log.e(
                Crowdin.CROWDIN_TAG,
                "Failed to update plural from WebSocket (internal) for resource $resourceId",
                e
            )
        }
    }

    private fun ensureMainThread(operationName: String) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalStateException("$operationName must be called on main thread")
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun resolvePluralForm(quantity: Int): String =
        try {
            val locale = crowdinResources.configuration.getLocale()
            val pluralRules = PluralRules.forLocale(locale)
            pluralRules.select(quantity.toDouble())
        } catch (e: Exception) {
            // Fallback keeps behavior stable even if locale resolution is unavailable.
            "quantity_$quantity"
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
        pluralStringStateMap.forEach { (key, watchedState) ->
            watchedState.state.value = crowdinResources.getQuantityString(key.resourceId, watchedState.quantityHint)
        }
        Log.d(Crowdin.CROWDIN_TAG, "Force updated all strings and plurals in ComposeStringRepository")
    }
}
