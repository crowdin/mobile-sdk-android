package com.crowdin.platform.repository.parser

import com.crowdin.platform.repository.remote.api.PluralData
import org.xmlpull.v1.XmlPullParser

internal class PluralParser {

    companion object {
        const val TAG_PLURALS: String = "plurals"
        private const val ITEM: String = "item"
    }

    var plurals: MutableList<PluralData> = mutableListOf()
    private var isPluralStarted = false
    private var isItemStarted = false
    private var isInnerTagOpened = false

    private var pluralData: PluralData? = null
    private var content: String = ""

    private var key: String = ""
    private var quantityKey: String = ""

    fun parseStartTag(parser: XmlPullParser) {
        when (parser.name) {
            TAG_PLURALS -> {
                isPluralStarted = true
                pluralData = PluralData()
                val attrCount = parser.attributeCount
                (attrCount > 0).let { if (it) key = parser.getAttributeValue(0) }
            }
            ITEM -> {
                isItemStarted = true
                val attrCount = parser.attributeCount
                (attrCount > 0).let { if (it) quantityKey = parser.getAttributeValue(0) }
            }
            else -> {
                if (isPluralStarted && isItemStarted) {
                    content += "<${parser.name}>"
                    isInnerTagOpened = true
                }
            }
        }
    }

    fun parseText(parser: XmlPullParser) {
        if (isPluralStarted && isItemStarted && isInnerTagOpened) {
            content += parser.text

        } else if (isPluralStarted && isItemStarted) {
            pluralData?.quantity?.set(quantityKey, parser.text)
        }
    }

    fun parseEndTag(parser: XmlPullParser) {
        if (isPluralStarted) {
            when (parser.name) {
                TAG_PLURALS -> {
                    pluralData?.name = key
                    pluralData?.let { plurals.add(it) }
                    isPluralStarted = false
                }
                ITEM -> {
                    val quantityValues = pluralData?.quantity
                    quantityValues?.set(quantityKey, quantityValues[quantityKey] + content)
                    quantityKey = ""
                    content = ""
                    isItemStarted = false
                }
                else -> {
                    content += "</${parser.name}>"
                    isInnerTagOpened = false
                }
            }
        }
    }
}