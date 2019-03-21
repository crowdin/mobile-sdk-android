package com.crowdin.platform.repository.parser

import org.xmlpull.v1.XmlPullParser

internal class StringParser {

    companion object {
        const val TAG_STRING: String = "string"
    }

    val resources: MutableMap<String, String> = mutableMapOf()
    private var isStringStarted = false
    private var key: String? = null
    private var content: String = ""

    fun parseStartTag(parser: XmlPullParser) {
        val name = parser.name
        if (name == TAG_STRING || isStringStarted) {
            val attrCount = parser.attributeCount
            if (attrCount > 0) {
                for (item: Int in 0 until parser.attributeCount) {
                    if (isStringStarted) {
                        content += "<$name>"
                    } else {
                        key = parser.getAttributeValue(item)
                    }

                    isStringStarted = true
                    break
                }
            } else {
                if (isStringStarted) {
                    content += "<$name>"

                    if (parser.next() == XmlPullParser.TEXT) {
                        content += parser.text
                    }
                }
            }
        }
    }

    fun parseText(parser: XmlPullParser) {
        if (isStringStarted) {
            content += parser.text
        }
    }

    fun parseEndTag(parser: XmlPullParser) {
        val name = parser.name
        if (isStringStarted) {
            if (name == TAG_STRING) {
                if (key != null) {
                    resources[key!!] = content
                }
                isStringStarted = false
                key = null
                content = ""

            } else {
                content += "</$name>"
            }
        }
    }
}