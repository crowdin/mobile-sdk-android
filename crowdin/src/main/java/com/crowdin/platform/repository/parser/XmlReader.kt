package com.crowdin.platform.repository.parser

import com.crowdin.platform.repository.remote.api.LanguageData
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream

internal class XmlReader : Reader {

    override fun parseInput(byteStream: InputStream): LanguageData {
        val pullParserFactory: XmlPullParserFactory
        try {
            pullParserFactory = XmlPullParserFactory.newInstance()
            val xmlPullParser = pullParserFactory.newPullParser()

            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            xmlPullParser.setInput(byteStream, null)

            if (xmlPullParser != null) {
                val stringResourcesParser = StringResourceParser()
                return parseXml(xmlPullParser, stringResourcesParser)
            }

        } catch (e: Exception) {
            return LanguageData()
        }

        return LanguageData()
    }

    private fun parseXml(xmlPullParser: XmlPullParser, parser: Parser): LanguageData {
        var eventType = xmlPullParser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    parser.onStartTag(xmlPullParser)
                }
                XmlPullParser.TEXT -> {
                    parser.onText(xmlPullParser)
                }
                XmlPullParser.END_TAG -> {
                    parser.onEndTag(xmlPullParser)
                }
            }
            eventType = xmlPullParser.next()
        }

        return parser.getLanguageData()
    }
}
