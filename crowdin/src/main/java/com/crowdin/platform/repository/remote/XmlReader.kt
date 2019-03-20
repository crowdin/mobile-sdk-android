package com.crowdin.platform.repository.remote

import com.crowdin.platform.repository.remote.api.LanguageData
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream

internal class XmlReader {

    fun parseInput(byteStream: InputStream, currentLocale: String): LanguageData? {
        val pullParserFactory: XmlPullParserFactory
        try {
            pullParserFactory = XmlPullParserFactory.newInstance()
            val parser = pullParserFactory.newPullParser()

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(byteStream, null)

            if (parser != null) {
                val resources = parseXml(parser)
                val languageData = LanguageData(currentLocale)
                languageData.resources = resources
                return languageData
            }

        } catch (e: Exception) {
            return null
        }

        return null
    }

    private fun parseXml(parser: XmlPullParser): MutableMap<String, String> {
        var eventType = parser.eventType
        val stringParser = StringParser()

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    stringParser.parseStartTag(parser)
                }
                XmlPullParser.TEXT -> {
                    stringParser.parseText(parser)
                }
                XmlPullParser.END_TAG -> {
                    stringParser.parseEndTag(parser)
                }
            }
            eventType = parser.next()
        }

        return stringParser.resources
    }
}
