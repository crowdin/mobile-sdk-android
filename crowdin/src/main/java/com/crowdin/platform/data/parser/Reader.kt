package com.crowdin.platform.data.parser

import com.crowdin.platform.data.model.LanguageData
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream

internal interface Reader {

    /**
     * Converts input stream to language data object.
     *
     * @param byteStream stream to be parsed.
     */
    fun parseInput(byteStream: InputStream, xmlPullParserFactory: XmlPullParserFactory?): LanguageData

    /**
     * Close reader and clear all data.
     */
    fun close()
}