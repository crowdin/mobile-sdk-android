package com.crowdin.platform.repository.parser

import org.xmlpull.v1.XmlPullParser

internal class StringParser {

    companion object {
        const val TAG_STRING: String = "string"
    }

    val resources: MutableMap<String, String> = mutableMapOf()
    private var isStringStarted = false
    private var isInnerTagOpened = false
    private var stringKey: String? = null
    private var content: String = ""

    fun parseStartTag(parser: XmlPullParser) {
        when (parser.name) {
            TAG_STRING -> {
                isStringStarted = true
                val attrCount = parser.attributeCount
                (attrCount > 0).let { if (it) stringKey = parser.getAttributeValue(0) }
            }
            else -> {
                if (isStringStarted) {
                    content += "<${parser.name}>"
                    isInnerTagOpened = true
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
        if (isStringStarted) {
            when (parser.name) {
                TAG_STRING -> {
                    if (stringKey != null) {
                        resources[stringKey!!] = content
                    }
                    isStringStarted = false
                    stringKey = null
                    content = ""
                }
                else -> {
                    content += "</${parser.name}>"
                    isInnerTagOpened = false
                }
            }
        }
    }
}