package com.crowdin.platform.repository.remote

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

    private var key: String? = null
    private var arrayData: ArrayData? = null
    private var stringValue: String = ""

    fun parseStartTag(parser: XmlPullParser) {
        when (parser.name) {
            TAG_STRING_ARRAY -> {
                arrayData = ArrayData()
                val attrCount = parser.attributeCount
                if (attrCount > 0) {
                    for (item: Int in 0 until parser.attributeCount) {
                        key = parser.getAttributeValue(item)
                        arrayData?.name = key
                        isArrayStarted = true
                        break
                    }
                }
            }
            ITEM -> isItemStarted = true
            else -> {
                if (isArrayStarted && isItemStarted) {
                    stringValue += "<${parser.name}>"
                    isInnerTagOpened = true
                }
            }
        }
    }

    fun parseText(parser: XmlPullParser) {
        if (isInnerTagOpened) {
            stringValue += parser.text

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
                    arrayData?.let { arrays.add(it) }
                    isArrayStarted = false
                }
                ITEM -> {
                    val array = arrayData?.values
                    array?.set(array.size - 1, array[array.size - 1] + stringValue)
                    stringValue = ""
                    isItemStarted = false
                }
                else -> {
                    stringValue += "</${parser.name}>"
                    isInnerTagOpened = false
                }
            }
        }
    }
}