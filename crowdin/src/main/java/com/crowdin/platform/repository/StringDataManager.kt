package com.crowdin.platform.repository

import android.content.Context
import com.crowdin.platform.repository.local.LocalRepository
import com.crowdin.platform.repository.remote.Connectivity
import com.crowdin.platform.repository.remote.NetworkType
import com.crowdin.platform.repository.remote.RemoteRepository
import com.crowdin.platform.repository.remote.api.LanguageData
import com.crowdin.platform.utils.ThreadUtils

internal class StringDataManager(private val remoteRepository: RemoteRepository,
                                 private val localRepository: LocalRepository): TextIdProvider {

    override fun provideTextId(text: String): Int? {
        return localRepository.getTextId(text)
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
        if (Connectivity.isOnline(context) && Connectivity.isNetworkAllowed(context, networkType)) {
            ThreadUtils.runInBackgroundPool(Runnable {
                remoteRepository.fetchData(object : LanguageDataCallback {

                    override fun onDataLoaded(languageData: LanguageData) {
                        localRepository.saveLanguageData(languageData)
                    }
                })
            }, false)
        }
    }
}

interface TextIdProvider {

    fun provideTextId(text: String): Int?
}
