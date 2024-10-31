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
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.LanguagesInfo
import com.crowdin.platform.data.model.ManifestData
import com.crowdin.platform.data.model.PluralData
import com.crowdin.platform.data.model.StringData
import com.crowdin.platform.data.model.TextMetaData
import com.crowdin.platform.data.remote.Connectivity
import com.crowdin.platform.data.remote.NetworkType
import com.crowdin.platform.data.remote.RemoteRepository
import com.crowdin.platform.util.FeatureFlags
import com.crowdin.platform.util.ThreadUtils
import com.crowdin.platform.util.getFormattedCode
import java.lang.reflect.Type
import java.util.Locale

internal class DataManager(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
    private val crowdinPreferences: Preferences,
    private val dataChangeObserver: LocalDataChangeObserver,
) : TextMetaDataProvider {
    companion object {
        private const val STATUS_OK = "ok"
        const val SUF_COPY = "-copy"
        const val DISTRIBUTION_DATA = "distribution_data"
        const val AUTH_INFO = "auth_info"
        const val DISTRIBUTION_HASH = "distribution_hash"
        const val MAPPING_SUF = "-mapping"
        const val SUPPORTED_LANGUAGES = "supported_languages"
        const val MANIFEST_DATA = "manifest_data"
        const val SYNC_DATA = "sync_data"
    }

    private var loadingStateListeners: ArrayList<LoadingStateListener>? = null

    override fun provideTextMetaData(text: String): TextMetaData = localRepository.getTextData(text)

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

    fun getStringArray(key: String): Array<String>? = localRepository.getStringArray(key)

    fun getStringPlural(
        resourceKey: String,
        quantityKey: String,
    ): String? = localRepository.getStringPlural(resourceKey, quantityKey)

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
        stringData: StringData? = null,
        arrayData: ArrayData? = null,
        pluralData: PluralData? = null,
    ) {
        if (FeatureFlags.isRealTimeUpdateEnabled || FeatureFlags.isScreenshotEnabled) {
            when {
                stringData != null -> {
                    if (localRepository.containsKey(stringData.stringKey)) {
                        localRepository.setStringData(
                            Locale.getDefault().getFormattedCode() + SUF_COPY,
                            stringData,
                        )
                    }
                }

                arrayData != null -> {
                    if (localRepository.containsKey(arrayData.name)) {
                        localRepository.setArrayData(
                            Locale.getDefault().getFormattedCode() + SUF_COPY,
                            arrayData,
                        )
                    }
                }

                pluralData != null -> {
                    if (localRepository.containsKey(pluralData.name)) {
                        localRepository.setPluralData(
                            Locale.getDefault().getFormattedCode() + SUF_COPY,
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
        languageData.language = languageData.language + MAPPING_SUF
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
    fun getSupportedLanguages(): LanguagesInfo? {
        Log.v(CROWDIN_TAG, "Getting supported languages started")

        var info: LanguagesInfo? = getData(SUPPORTED_LANGUAGES, LanguagesInfo::class.java)
        if (info == null) {
            info = remoteRepository.getSupportedLanguages()
            saveData(SUPPORTED_LANGUAGES, info)
        }

        Log.v(CROWDIN_TAG, "Supported languages: $info")

        return info
    }
}
