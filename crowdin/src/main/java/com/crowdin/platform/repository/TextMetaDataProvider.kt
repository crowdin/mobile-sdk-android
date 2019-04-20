package com.crowdin.platform.repository

import com.crowdin.platform.repository.model.SearchResultData

internal interface TextMetaDataProvider {

    /**
     * Provides meta data related to text founded inside of local repository.
     * @see SearchResultData
     */
    fun provideTextKey(text: String): SearchResultData
}