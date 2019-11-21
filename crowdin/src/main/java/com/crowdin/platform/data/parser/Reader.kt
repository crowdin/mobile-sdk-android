package com.crowdin.platform.data.parser

import com.crowdin.platform.data.model.LanguageData
import java.io.InputStream
import org.xmlpull.v1.XmlPullParserFactory

internal interface Reader {

    /**
     * Converts input stream to language data object.
     *
     * @param byteStream stream to be parsed.
     */
    fun parseInput(
        byteStream: InputStream,
        xmlPullParserFactory: XmlPullParserFactory?
    ): LanguageData

    /**
     * Close reader and clear all data.
     */
    fun close()
}
