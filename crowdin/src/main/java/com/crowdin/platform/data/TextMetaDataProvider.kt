package com.crowdin.platform.data

import com.crowdin.platform.data.model.TextMetaData

/**
 * Provides meta data related to text founded inside of local repository.
 */
internal interface TextMetaDataProvider {
    /**
     * Provides meta data related to text.
     *
     * @param localeCode locale code for the text.
     * @param text searchable text.
     * @see TextMetaData
     * */
    fun provideTextMetaData(
        localeCode: String,
        text: String,
    ): TextMetaData
}
