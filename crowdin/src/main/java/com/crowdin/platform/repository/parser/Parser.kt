package com.crowdin.platform.repository.parser

import com.crowdin.platform.repository.model.LanguageData
import org.xmlpull.v1.XmlPullParser

/**
 * Responsible for handling events related to xml parsing
 */
internal interface Parser {

    fun onStartTag(parser: XmlPullParser)
    fun onText(parser: XmlPullParser)
    fun onEndTag(parser: XmlPullParser)
    fun getLanguageData(): LanguageData
    fun clearData()
}