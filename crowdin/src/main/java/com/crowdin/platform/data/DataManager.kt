package com.crowdin.platform.data

import android.content.Context
import com.crowdin.platform.LoadingStateListener
import com.crowdin.platform.LocalDataChangeObserver
import com.crowdin.platform.data.local.LocalRepository
import com.crowdin.platform.data.model.*
import com.crowdin.platform.data.remote.Connectivity
import com.crowdin.platform.data.remote.NetworkType
import com.crowdin.platform.data.remote.RemoteRepository
import com.crowdin.platform.util.FeatureFlags
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

internal class DataManager(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
    private val dataChangeObserver: LocalDataChangeObserver
) : TextMetaDataProvider {

    companion object {
        private const val STATUS_OK = "ok"
        const val SUF_COPY = "-copy"
        const val DISTRIBUTION_DATA = "distribution_data"
        const val AUTH_INFO = "auth_info"
    }

    private var loadingStateListeners: ArrayList<LoadingStateListener>? = null

    override fun provideTextMetaData(text: String): TextMetaData {
        return localRepository.getTextData(text)
    }

    fun getString(language: String, stringKey: String): String? {
        return localRepository.getString(language, stringKey)
    }

    fun setString(language: String, key: String, value: String) {
        localRepository.setString(language, key, value)
    }

    fun getStringArray(key: String): Array<String>? {
        return localRepository.getStringArray(key)
    }

    fun getStringPlural(resourceKey: String, quantityKey: String): String? {
        return localRepository.getStringPlural(resourceKey, quantityKey)
    }

    fun updateData(context: Context, networkType: NetworkType) {
        val status = validateData(context, networkType)
        if (status == STATUS_OK) {
            remoteRepository.fetchData(object : LanguageDataCallback {

                override fun onDataLoaded(languageData: LanguageData) {
                    localRepository.saveLanguageData(languageData)
                    if (FeatureFlags.isRealTimeUpdateEnabled) {
                        dataChangeObserver.onDataChanged()
                    }

                    sendOnDataChanged()
                }

                override fun onFailure(throwable: Throwable) {
                    sendOnFailure(throwable)
                }
            })
        } else {
            sendOnFailure(Throwable(status))
        }
    }

    private fun validateData(context: Context, networkType: NetworkType): String {
        var status: String = STATUS_OK
        when {
            !Connectivity.isOnline(context) -> status = "No internet connection"
            !Connectivity.isNetworkAllowed(context, networkType) -> status =
                "Not allowed to load with current network type: ${networkType.name}"
        }

        return status
    }

    fun saveReserveResources(
        stringData: StringData? = null,
        arrayData: ArrayData? = null,
        pluralData: PluralData? = null
    ) {
        if (FeatureFlags.isRealTimeUpdateEnabled) {
            when {
                stringData != null -> localRepository.setStringData(
                    Locale.getDefault().language + SUF_COPY,
                    stringData
                )
                arrayData != null -> localRepository.setArrayData(
                    Locale.getDefault().language + SUF_COPY,
                    arrayData
                )
                pluralData != null -> localRepository.setPluralData(
                    Locale.getDefault().language + SUF_COPY,
                    pluralData
                )
            }
        }
    }

    fun addLoadingStateListener(listener: LoadingStateListener) {
        if (loadingStateListeners == null) {
            loadingStateListeners = ArrayList()
        }

        loadingStateListeners?.add(listener)
    }

    fun removeLoadingStateListener(listener: LoadingStateListener): Boolean {
        return loadingStateListeners?.remove(listener) ?: false
    }

    private fun sendOnFailure(throwable: Throwable) {
        loadingStateListeners?.let { listeners ->
            listeners.forEach {
                it.onFailure(throwable)
            }
        }
    }

    private fun sendOnDataChanged() {
        loadingStateListeners?.let { listeners ->
            listeners.forEach {
                it.onDataChanged()
            }
        }
    }

    fun saveMapping(languageData: LanguageData) {
        localRepository.saveLanguageData(languageData)
    }

    fun getMapping(sourceLanguage: String): LanguageData? =
        localRepository.getLanguageData(sourceLanguage)

    fun saveData(type: String, data: Any?) {
        localRepository.saveData(type, data)
    }

    fun getData(type: String, classType: Type): Any? {
        return localRepository.getData(type, classType)
    }

    fun isAuthorized(): Boolean {
        val authInfo = getData(AUTH_INFO, AuthInfo::class.java) as AuthInfo?
        return authInfo != null
    }

    fun isTokenExpired(): Boolean {
        val authInfo = getData(AUTH_INFO, AuthInfo::class.java) as AuthInfo?
        return authInfo?.isExpired() ?: true
    }

    fun getAccessToken(): String? {
        val authInfo = getData(AUTH_INFO, AuthInfo::class.java) as AuthInfo?
        return authInfo?.accessToken
    }

    fun getRefreshToken(): String? {
        val authInfo = getData(AUTH_INFO, AuthInfo::class.java) as AuthInfo?
        return authInfo?.refreshToken
    }
}
