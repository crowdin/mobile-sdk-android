package com.crowdin.platform.data.parser

import com.crowdin.platform.data.model.ArrayData
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.PluralData
import com.crowdin.platform.data.model.StringData
import org.xmlpull.v1.XmlPullParser

internal class StringResourceParser : Parser {

    companion object {
        private const val TAG_STRING: String = "string"
        private const val TAG_STRING_ARRAY: String = "string-array"
        private const val TAG_PLURALS: String = "plurals"
        private const val ITEM: String = "item"
    }

    // String
    private val resources: MutableList<StringData> = mutableListOf()
    private var isStringStarted = false
    private var stringKey: String? = null
    private var stringData: StringData? = null

    // Array
    private val arrays: MutableList<ArrayData> = mutableListOf()
    private var isArrayStarted = false
    private var isItemStarted = false
    private var arrayKey: String? = null
    private var arrayData: ArrayData? = null

    // Plurals
    private var plurals: MutableList<PluralData> = mutableListOf()
    private var isPluralStarted = false
    private var pluralData: PluralData? = null
    private var quantityKey: String = ""
    private var quantityItemKey: String = ""

    private var isInnerTagOpened = false
    private var content: String = ""

    override fun onStartTag(parser: XmlPullParser) {
        when (parser.name) {
            TAG_STRING -> {
                isStringStarted = true
                stringData = StringData()
                val attrCount = parser.attributeCount
                (attrCount > 0).let { if (it) stringKey = parser.getAttributeValue(0) }
            }
            TAG_STRING_ARRAY -> {
                isArrayStarted = true
                arrayData = ArrayData()
                val attrCount = parser.attributeCount
                (attrCount > 0).let { if (it) arrayKey = parser.getAttributeValue(0) }
            }
            TAG_PLURALS -> {
                isPluralStarted = true
                pluralData = PluralData()
                val attrCount = parser.attributeCount
                (attrCount > 0).let { if (it) quantityKey = parser.getAttributeValue(0) }
            }
            ITEM -> {
                isItemStarted = true
                if (isPluralStarted) {
                    val attrCount = parser.attributeCount
                    (attrCount > 0).let { if (it) quantityItemKey = parser.getAttributeValue(0) }
                }
            }
            else -> {
                if ((isArrayStarted && isItemStarted) ||
                    (isPluralStarted && isItemStarted) ||
                    isStringStarted
                ) {
                    content += "<${parser.name}>"
                    isInnerTagOpened = true
                }
            }
        }
    }

    override fun onText(parser: XmlPullParser) {
        if (isStringStarted ||
            (isArrayStarted && isInnerTagOpened) ||
            (isPluralStarted && isItemStarted && isInnerTagOpened)
        ) {
            content += unEscapeQuotes(parser.text)
        } else if (isArrayStarted && isItemStarted) {
            when (arrayData?.values) {
                null -> arrayData?.values = arrayOf(parser.text)
                else -> arrayData?.values = arrayData?.values?.plus(parser.text)
            }
        } else if (isPluralStarted && isItemStarted) {
            content += unEscapeQuotes(parser.text)
        }
    }
    private fun unEscapeQuotes(replaceString: String): String {
        return replaceString.replace("\\\"", "\"")
                .replace("\\\'", "\'")
    }

    override fun onEndTag(parser: XmlPullParser) {
        if (isStringStarted || isArrayStarted || isPluralStarted) {
            when (parser.name) {
                TAG_STRING -> {
                    if (stringKey != null) {
                        stringData?.stringKey = stringKey!!
                        stringData?.stringValue = content
                        stringData?.let { resources.add(it) }
                    }
                    isStringStarted = false
                    stringKey = null
                    content = ""
                }
                TAG_STRING_ARRAY -> {
                    arrayData?.name = arrayKey!!
                    arrayData?.let { arrays.add(it) }
                    arrayKey = ""
                    isArrayStarted = false
                }
                TAG_PLURALS -> {
                    pluralData?.name = quantityKey
                    pluralData?.let { plurals.add(it) }
                    isPluralStarted = false
                }
                ITEM -> {
                    if (isArrayStarted) {
                        val array = arrayData?.values
                        array?.set(array.size - 1, array[array.size - 1] + content)
                    } else if (isPluralStarted) {
                        val quantityValues = pluralData?.quantity
                        quantityValues?.set(quantityItemKey, content)
                        quantityItemKey = ""
                    }
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

    override fun getLanguageData(): LanguageData {
        val languageData = LanguageData()
        languageData.resources.addAll(resources)
        languageData.arrays.addAll(arrays)
        languageData.plurals.addAll(plurals)

        return languageData
    }

    override fun clearData() {
        resources.clear()
        arrays.clear()
        plurals.clear()
    }
}
