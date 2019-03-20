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
            val xmlPullParser = pullParserFactory.newPullParser()

            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            xmlPullParser.setInput(byteStream, null)

            if (xmlPullParser != null) {
                val stringParser = StringParser()
                val arrayParser = ArrayParser()
                return parseXml(currentLocale, xmlPullParser, stringParser, arrayParser)
            }

        } catch (e: Exception) {
            return null
        }

        return null
    }

    private fun parseXml(currentLocale: String, parser: XmlPullParser,
                         stringParser: StringParser, arrayParser: ArrayParser): LanguageData {
        var eventType = parser.eventType

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    stringParser.parseStartTag(parser)
                    arrayParser.parseStartTag(parser)
                }
                XmlPullParser.TEXT -> {
                    stringParser.parseText(parser)
                    arrayParser.parseText(parser)
                }
                XmlPullParser.END_TAG -> {
                    stringParser.parseEndTag(parser)
                    arrayParser.parseEndTag(parser)
                }
            }
            eventType = parser.next()
        }

        val languageData = LanguageData(currentLocale)
        languageData.resources = stringParser.resources
        languageData.arrays = arrayParser.arrays

        return languageData
    }
}
