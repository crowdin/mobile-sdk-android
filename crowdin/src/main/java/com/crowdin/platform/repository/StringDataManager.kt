package com.crowdin.platform.repository

import com.crowdin.platform.repository.local.LocalRepository
import com.crowdin.platform.repository.remote.RemoteRepository
import com.crowdin.platform.repository.remote.api.LanguageData
import com.crowdin.platform.utils.ThreadUtils
import java.util.*

internal class StringDataManager(private val remoteRepository: RemoteRepository,
                                 private val localRepository: LocalRepository) {

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

    fun updateData() {
        val language = Locale.getDefault().language
        ThreadUtils.runInBackgroundPool(Runnable {
            remoteRepository.fetchData(language, object : LanguageDataCallback {

                override fun onDataLoaded(languageData: LanguageData) {
                    localRepository.saveLanguageData(languageData)
                }
            })
        }, false)
    }
}
