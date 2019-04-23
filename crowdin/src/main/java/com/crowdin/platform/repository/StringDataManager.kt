package com.crowdin.platform.repository

import android.content.Context
import android.graphics.Bitmap
import com.crowdin.platform.LoadingStateListener
import com.crowdin.platform.LocalDataChangeObserver
import com.crowdin.platform.repository.local.LocalRepository
import com.crowdin.platform.repository.model.*
import com.crowdin.platform.repository.remote.Connectivity
import com.crowdin.platform.repository.remote.NetworkType
import com.crowdin.platform.repository.remote.RemoteRepository
import com.crowdin.platform.utils.FeatureFlags
import com.crowdin.platform.utils.ThreadUtils
import java.util.*
import kotlin.collections.ArrayList

internal class StringDataManager(private val remoteRepository: RemoteRepository,
                                 private val localRepository: LocalRepository,
                                 private val dataChangeObserver: LocalDataChangeObserver) : TextMetaDataProvider {

    companion object {
        private const val STATUS_OK = "ok"
    }

    private var loadingStateListeners: ArrayList<LoadingStateListener>? = null

    override fun provideTextKey(text: String): SearchResultData {
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
            ThreadUtils.runInBackgroundPool(Runnable {
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
            }, false)
        } else {
            sendOnFailure(Throwable(status))
        }
    }

    private fun validateData(context: Context, networkType: NetworkType): String {
        var status: String = STATUS_OK
        when {
            !Connectivity.isOnline(context) -> status = "No internet connection"
            !Connectivity.isNetworkAllowed(context, networkType) -> status = "Not allowed to load with current network type: ${networkType.name}"
        }

        return status
    }

    fun saveReserveResources(stringData: StringData? = null,
                             arrayData: ArrayData? = null,
                             pluralData: PluralData? = null) {
        if (FeatureFlags.isRealTimeUpdateEnabled) {
            when {
                stringData != null -> localRepository.setStringData("${Locale.getDefault().language}-copy", stringData)
                arrayData != null -> localRepository.setArrayData("${Locale.getDefault().language}-copy", arrayData)
                pluralData != null -> localRepository.setPluralData("${Locale.getDefault().language}-copy", pluralData)
            }
        }
    }

    fun addLoadingStateListener(listener: LoadingStateListener) {
        if (loadingStateListeners == null) {
            loadingStateListeners = ArrayList()
        }

        loadingStateListeners?.add(listener)
    }

    fun removeLoadingStateListener(listener: LoadingStateListener) {
        loadingStateListeners?.let {
            it.removeAt(it.indexOf(listener))
        }
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

    // TODO: extract to separate class
    fun sendScreenshotWithKeys(bitmap: Bitmap, resourceKeys: Any) {

    }
}
