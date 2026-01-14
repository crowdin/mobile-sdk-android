package com.crowdin.platform.data

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import com.crowdin.platform.Crowdin.CROWDIN_TAG
import com.crowdin.platform.LoadingStateListener
import com.crowdin.platform.LocalDataChangeObserver
import com.crowdin.platform.Preferences
import com.crowdin.platform.data.local.LocalRepository
import com.crowdin.platform.data.model.ArrayData
import com.crowdin.platform.data.model.AuthInfo
import com.crowdin.platform.data.model.CachedLanguages
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.ManifestData
import com.crowdin.platform.data.model.PluralData
import com.crowdin.platform.data.model.StringData
import com.crowdin.platform.data.model.SupportedLanguages
import com.crowdin.platform.data.model.SyncData
import com.crowdin.platform.data.model.TextMetaData
import com.crowdin.platform.data.remote.Connectivity
import com.crowdin.platform.data.remote.NetworkType
import com.crowdin.platform.data.remote.RemoteRepository
import com.crowdin.platform.util.FeatureFlags
import com.crowdin.platform.util.ThreadUtils
import com.crowdin.platform.util.getFormattedCode
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.Locale

internal class DataManager(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
    private val crowdinPreferences: Preferences,
    private val dataChangeObserver: LocalDataChangeObserver,
) : TextMetaDataProvider {
    private var loadingStateListeners: ArrayList<LoadingStateListener>? = null

    override fun provideTextMetaData(
        localeCode: String,
        text: String,
    ): TextMetaData = localRepository.getTextData(localeCode, text)

    fun getLanguageData(language: String): LanguageData? = localRepository.getLanguageData(language)

    fun getString(
        language: String,
        stringKey: String,
    ): String? = localRepository.getString(language, stringKey)

    fun setString(
        language: String,
        key: String,
        value: String,
    ) {
        localRepository.setString(language, key, value)
    }

    fun getStringArray(
        localeCode: String,
        key: String,
    ): Array<String>? = localRepository.getStringArray(localeCode, key)

    fun getStringPlural(
        localeCode: String,
        resourceKey: String,
        quantityKey: String,
    ): String? = localRepository.getStringPlural(localeCode, resourceKey, quantityKey)

    fun updateData(
        context: Context,
        networkType: NetworkType,
        onFinished: (() -> Unit)? = null,
    ) {
        ThreadUtils.runInBackgroundPool({
            val languageInfo = getSupportedLanguages()
            val status = validateData(context, networkType)
            if (status == STATUS_OK) {
                Log.v(CROWDIN_TAG, "Update data from Api started")

                remoteRepository.fetchData(
                    context.resources.configuration,
                    supportedLanguages = languageInfo,
                    languageDataCallback =
                        object : LanguageDataCallback {
                            override fun onDataLoaded(languageData: LanguageData) {
                                Log.v(CROWDIN_TAG, "Update data from Api finished")
                                crowdinPreferences.setLastUpdate(System.currentTimeMillis())
                                refreshData(languageData)
                                ThreadUtils.executeOnMain {
                                    onFinished?.invoke()
                                }
                            }

                            override fun onFailure(throwable: Throwable) {
                                Log.e(CROWDIN_TAG, "Update data from Api failed. ${throwable.message}")

                                sendOnFailure(throwable)
                                ThreadUtils.executeOnMain {
                                    onFinished?.invoke()
                                }
                            }
                        },
                )
            } else {
                sendOnFailure(Throwable(status))
                ThreadUtils.executeOnMain {
                    onFinished?.invoke()
                }
            }
        }, true)
    }

    fun refreshData(languageData: LanguageData) {
        localRepository.saveLanguageData(languageData)
        if (FeatureFlags.isRealTimeUpdateEnabled || FeatureFlags.isScreenshotEnabled) {
            dataChangeObserver.onDataChanged()
        }

        sendOnDataChanged()
    }

    private fun validateData(
        context: Context,
        networkType: NetworkType,
    ): String {
        var status: String = STATUS_OK
        when {
            !Connectivity.isOnline(context) -> status = "No internet connection"
            !Connectivity.isNetworkAllowed(context, networkType) ->
                status = "Not allowed to load with current network type: ${networkType.name}"
        }

        return status
    }

    fun saveReserveResources(
        locale: Locale,
        stringData: StringData? = null,
        arrayData: ArrayData? = null,
        pluralData: PluralData? = null,
    ) {
        if (FeatureFlags.isRealTimeUpdateEnabled || FeatureFlags.isScreenshotEnabled) {
            val localeCode = locale.getFormattedCode()
            when {
                stringData != null -> {
                    if (localRepository.containsKey(localeCode, stringData.stringKey)) {
                        localRepository.setStringData(
                            localeCode + SUF_COPY,
                            stringData,
                        )
                    }
                }

                arrayData != null -> {
                    if (localRepository.containsKey(localeCode, arrayData.name)) {
                        localRepository.setArrayData(
                            localeCode + SUF_COPY,
                            arrayData,
                        )
                    }
                }

                pluralData != null -> {
                    if (localRepository.containsKey(localeCode, pluralData.name)) {
                        localRepository.setPluralData(
                            localeCode + SUF_COPY,
                            pluralData,
                        )
                    }
                }
            }
        }
    }

    fun addLoadingStateListener(listener: LoadingStateListener) {
        if (loadingStateListeners == null) {
            loadingStateListeners = ArrayList()
        }

        loadingStateListeners?.add(listener)
    }

    fun removeLoadingStateListener(listener: LoadingStateListener): Boolean = loadingStateListeners?.remove(listener) ?: false

    private fun sendOnFailure(throwable: Throwable) {
        loadingStateListeners?.let { listeners ->
            listeners.forEach {
                ThreadUtils.executeOnMain {
                    it.onFailure(throwable)
                }
            }
        }
    }

    private fun sendOnDataChanged() {
        loadingStateListeners?.let { listeners ->
            listeners.forEach {
                ThreadUtils.executeOnMain {
                    it.onDataChanged()
                }
            }
        }
    }

    fun saveMapping(languageData: LanguageData) {
        languageData.language += MAPPING_SUF
        localRepository.saveLanguageData(languageData)
    }

    fun getMapping(sourceLanguage: String): LanguageData? = localRepository.getLanguageData(sourceLanguage + MAPPING_SUF)

    @WorkerThread
    fun getManifest(): ManifestData? {
        var manifest: ManifestData? = getData(MANIFEST_DATA, ManifestData::class.java)
        if (manifest == null) {
            remoteRepository.getManifest {
                saveData(MANIFEST_DATA, it)
                manifest = it
            }
        }

        return manifest
    }

    fun saveData(
        type: String,
        data: Any?,
    ) {
        localRepository.saveData(type, data)
    }

    fun <T> getData(
        type: String,
        classType: Type,
    ): T? = localRepository.getData(type, classType)

    fun isAuthorized(): Boolean = (getData(AUTH_INFO, AuthInfo::class.java) as AuthInfo?) != null

    fun isTokenExpired(): Boolean = (getData(AUTH_INFO, AuthInfo::class.java) as AuthInfo?)?.isExpired() ?: true

    fun getAccessToken(): String? = (getData(AUTH_INFO, AuthInfo::class.java) as AuthInfo?)?.accessToken

    fun getRefreshToken(): String? = (getData(AUTH_INFO, AuthInfo::class.java) as AuthInfo?)?.refreshToken

    fun saveDistributionHash(distributionHash: String) {
        crowdinPreferences.setString(DISTRIBUTION_HASH, distributionHash)
    }

    fun getDistributionHash(): String? = crowdinPreferences.getString(DISTRIBUTION_HASH)

    fun invalidateAuthData() {
        saveData(AUTH_INFO, null)
    }

    @WorkerThread
    fun getSupportedLanguages(): SupportedLanguages? {
        Log.v(CROWDIN_TAG, "Getting supported languages started")
        val syncData = crowdinPreferences.getData<SyncData>(SYNC_DATA, SyncData::class.java)
        val cachedLanguages = getData<CachedLanguages>(CACHED_LANGUAGES, CachedLanguages::class.java)
        val currentTimestamp = syncData?.timestamp ?: 0L

        return if (cachedLanguages != null && cachedLanguages.manifestTimestamp == currentTimestamp) {
            cachedLanguages.languages
        } else {
            val fetchedLanguages = remoteRepository.getSupportedLanguages()
            if (fetchedLanguages != null) {
                saveData(CACHED_LANGUAGES, CachedLanguages(fetchedLanguages, currentTimestamp))
            } else {
                Log.v(CROWDIN_TAG, "Failed to fetch languages from distribution")
            }
            Log.v(CROWDIN_TAG, "Supported languages: $fetchedLanguages")
            fetchedLanguages
        }
    }

    @WorkerThread
    fun getTicket(event: String): String? {
        try {
            var ticketValue: String? = null
            val type = object : TypeToken<MutableMap<String, TicketItem>>() {}.type
            var ticketsMap: MutableMap<String, TicketItem>? = localRepository.getData(EVENT_TICKETS, type)
            if (ticketsMap == null) {
                ticketsMap = mutableMapOf()
            }

            val ticketItem = ticketsMap[event]
            if (ticketItem == null || ticketItem.isExpired()) {
                Log.d(CROWDIN_TAG, "Ticket expired for event: $event")
                remoteRepository.getTicket(event)?.data?.ticket?.let {
                    ticketsMap[event] = TicketItem(it, System.currentTimeMillis() + EVENT_TICKETS_EXPIRATION)
                    localRepository.saveData(EVENT_TICKETS, ticketsMap)
                    ticketValue = it
                }
            } else {
                Log.d(CROWDIN_TAG, "Ticket not expired for event: $event")
                ticketValue = ticketItem.ticket
            }

            return ticketValue
        } catch (throwable: Throwable) {
            Log.e(CROWDIN_TAG, "Ticket failed", throwable)
            return null
        }
    }

    fun clearSocketData() {
        localRepository.saveData(EVENT_TICKETS, null)
    }

    data class TicketItem(
        val ticket: String,
        val expirationTime: Long,
    ) {
        fun isExpired(): Boolean = System.currentTimeMillis() > expirationTime
    }

    companion object {
        private const val STATUS_OK = "ok"
        const val SUF_COPY = "-copy"
        const val DISTRIBUTION_DATA = "distribution_data"
        const val AUTH_INFO = "auth_info"
        const val DISTRIBUTION_HASH = "distribution_hash"
        const val MAPPING_SUF = "-mapping"
        const val CACHED_LANGUAGES = "cached_languages"
        const val MANIFEST_DATA = "manifest_data"
        const val SYNC_DATA = "sync_data"
        const val EVENT_TICKETS = "event_tickets"
        const val EVENT_TICKETS_EXPIRATION = 1000 * 60 * 4
    }
}
