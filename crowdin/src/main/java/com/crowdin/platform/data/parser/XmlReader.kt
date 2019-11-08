package com.crowdin.platform.data.parser

import com.crowdin.platform.data.model.LanguageData
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream

internal class XmlReader(private var parser: Parser) : Reader {

    override fun close() {
        parser.clearData()
    }

    override fun parseInput(
        byteStream: InputStream,
        xmlPullParserFactory: XmlPullParserFactory?
    ): LanguageData {
        try {
            val xmlPullParser = xmlPullParserFactory!!.newPullParser()
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            xmlPullParser.setInput(byteStream, null)

            if (xmlPullParser != null) {
                return parseXml(xmlPullParser, parser)
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
