package com.crowdin.platform.repository.parser

import com.crowdin.platform.repository.remote.api.ArrayData
import org.xmlpull.v1.XmlPullParser

internal class ArrayParser {

    companion object {
        const val TAG_STRING_ARRAY: String = "string-array"
        private const val ITEM: String = "item"
    }

    val arrays: MutableList<ArrayData> = mutableListOf()
    private var isArrayStarted = false
    private var isItemStarted = false
    private var isInnerTagOpened = false

    private var arrayKey: String? = null
    private var arrayData: ArrayData? = null
    private var content: String = ""

    fun parseStartTag(parser: XmlPullParser) {
        when (parser.name) {
            TAG_STRING_ARRAY -> {
                isArrayStarted = true
                arrayData = ArrayData()
                val attrCount = parser.attributeCount
                (attrCount > 0).let { if (it) arrayKey = parser.getAttributeValue(0) }
            }
            ITEM -> isItemStarted = true
            else -> {
                if (isArrayStarted && isItemStarted) {
                    content += "<${parser.name}>"
                    isInnerTagOpened = true
                }
            }
        }
    }

    fun parseText(parser: XmlPullParser) {
        if (isInnerTagOpened) {
            content += parser.text

        } else if (isArrayStarted && isItemStarted) {
            when (arrayData?.values) {
                null -> arrayData?.values = arrayOf(parser.text)
                else -> arrayData?.values = arrayData?.values?.plus(parser.text)
            }
        }
    }

    fun parseEndTag(parser: XmlPullParser) {
        if (isArrayStarted) {
            when (parser.name) {
                TAG_STRING_ARRAY -> {
                    arrayData?.name = arrayKey
                    arrayData?.let { arrays.add(it) }
                    arrayKey = ""
                    isArrayStarted = false
                }
                ITEM -> {
                    val array = arrayData?.values
                    array?.set(array.size - 1, array[array.size - 1] + content)
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