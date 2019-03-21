package com.crowdin.platform.repository

import com.crowdin.platform.CrowdinConfig
import com.crowdin.platform.repository.local.LocalRepository
import com.crowdin.platform.repository.remote.RemoteRepository
import com.crowdin.platform.repository.remote.api.LanguageData
import com.crowdin.platform.utils.LocaleUtils

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

    fun updateData(config: CrowdinConfig) {
        val language = LocaleUtils.currentLanguage
        val filePaths = config.filePaths
        filePaths?.forEach {
            remoteRepository.fetchData(config.distributionKey, language, it, object : LanguageDataCallback {

                override fun onDataLoaded(languageData: LanguageData) {
                    localRepository.saveLanguageData(languageData)
                }
            })
        }
    }
}
