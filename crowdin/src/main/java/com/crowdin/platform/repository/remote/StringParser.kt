package com.crowdin.platform.repository.remote

import org.xmlpull.v1.XmlPullParser

internal class StringParser {

    private val TAG_STRING: String = "string"
    val resources: MutableMap<String, String> = mutableMapOf()
    private var wasStringStart = false
    private var key: String? = null
    private var value: String? = null

    fun parseStartTag(parser: XmlPullParser) {
        val name = parser.name
        if (name == TAG_STRING || wasStringStart) {
            val attrCount = parser.attributeCount
            if (attrCount > 0) {
                for (item: Int in 0 until parser.attributeCount) {
                    if (wasStringStart) {
                        value += "<${parser.name}>"
                    } else {
                        key = parser.getAttributeValue(item)
                    }

                    if (parser.next() == XmlPullParser.TEXT) {
                        if (value == null) {
                            value = parser.text
                        } else {
                            value += parser.text
                        }
                    }

                    wasStringStart = true
                    break
                }
            } else {
                if (wasStringStart) {
                    value += "<${parser.name}>"

                    if (parser.next() == XmlPullParser.TEXT) {
                        value += parser.text
                    }
                }
            }
        }
    }

    fun parseText(parser: XmlPullParser) {
        if (wasStringStart) {
            value += parser.text
        }
    }

    fun parseEndTag(parser: XmlPullParser) {
        val name = parser.name
        if (wasStringStart) {
            if (name == TAG_STRING) {
                if (key != null && value != null) {
                    resources.put(key!!, value!!)
                }
                wasStringStart = false
                key = null
                value = null

            } else {
                value += "</${parser.name}>"
            }
        }
    }
}