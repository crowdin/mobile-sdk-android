package com.crowdin.platform.data

import com.crowdin.platform.data.model.SearchResultData

internal interface TextMetaDataProvider {

    /**
     * Provides meta data related to text founded inside of local repository.
     * @see SearchResultData
     */
    fun provideTextKey(text: String): SearchResultData
}