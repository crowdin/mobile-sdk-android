package com.crowdin.platform

import com.crowdin.platform.data.model.PluralData
import com.crowdin.platform.data.parser.StringResourceParser
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.xmlpull.v1.XmlPullParser

class StringResourceParserTest {

    @Test
    fun getLanguageData_getDefaultLanguageTest() {
        // Given
        val stringResourceParser = StringResourceParser()

        // When
        val result = stringResourceParser.getLanguageData()

        // Then
        assertThat(result.language, `is`(""))
        assertThat(result.resources, `is`(emptyList()))
        assertThat(result.arrays, `is`(emptyList()))
        assertThat(result.plurals, `is`(emptyList()))
    }

    @Test
    fun parseStringDataTest() {
        // Given
        val stringResourceParser = StringResourceParser()
        val mockParser = givenStringDataParser()
        val expectedKey = "stringKey"
        val expectedValue = "stringValue"

        // When
        stringResourceParser.onStartTag(mockParser)
        stringResourceParser.onText(mockParser)
        stringResourceParser.onEndTag(mockParser)

        // Then
        val stringData = stringResourceParser.getLanguageData().resources.first()
        assertThat(stringData.stringKey, `is`(expectedKey))
        assertThat(stringData.stringValue, `is`(expectedValue))
    }

    @Test
    fun parseArrayDataTest() {
        // Given
        val stringResourceParser = StringResourceParser()
        val expectedKey = "arrayKey"
        val expectedArray = arrayOf("arrayValue", "arrayValue1")
        val parser = mock(XmlPullParser::class.java)

        // When
        addArrayItem(stringResourceParser, parser)

        // Then
        val arrayData = stringResourceParser.getLanguageData().arrays.first()
        assertThat(arrayData.name, `is`(expectedKey))
        assertArrayEquals(arrayData.values, expectedArray)
    }

    @Test
    fun parsePluralDataTest() {
        // Given
        val stringResourceParser = StringResourceParser()
        val expectedKey = "pluralKey"
        val expectedQuantity = mutableMapOf(
            Pair("key0", "value0"),
            Pair("key1", "value1")
        )
        val expectedPluralData = PluralData(expectedKey, expectedQuantity)
        val parser = mock(XmlPullParser::class.java)

        // When
        addPluralItem(stringResourceParser, parser)

        // Then
        val actualPluralData = stringResourceParser.getLanguageData().plurals.first()
        assertThat(actualPluralData.name, `is`(expectedKey))
        assertEquals(expectedPluralData, actualPluralData)
    }

    @Test
    fun parseInnerTagTest() {
        val stringResourceParser = StringResourceParser()
        val parser = mock(XmlPullParser::class.java)
        `when`(parser.name).thenReturn("string")
        `when`(parser.attributeCount).thenReturn(1)
        `when`(parser.getAttributeValue(0)).thenReturn("stringKey")
        // Start string
        `when`(parser.text).thenReturn("test ")
        stringResourceParser.onStartTag(parser)
        stringResourceParser.onText(parser)
        // Add inner tag text
        `when`(parser.name).thenReturn("b")
        `when`(parser.attributeCount).thenReturn(0)
        stringResourceParser.onStartTag(parser)
        `when`(parser.text).thenReturn("test")
        stringResourceParser.onText(parser)
        stringResourceParser.onEndTag(parser)
        // End string
        `when`(parser.name).thenReturn("string")
        stringResourceParser.onEndTag(parser)
        val expectedKey = "stringKey"
        val expectedValue = "test <b>test</b>"

        val stringData = stringResourceParser.getLanguageData().resources.first()
        assertThat(stringData.stringKey, `is`(expectedKey))
        assertThat(stringData.stringValue, `is`(expectedValue))
    }

    @Test
    fun parseInnerTagWithAttributeTest() {
        val stringResourceParser = StringResourceParser()
        val parser = mock(XmlPullParser::class.java)
        `when`(parser.name).thenReturn("string")
        `when`(parser.attributeCount).thenReturn(1)
        `when`(parser.getAttributeValue(0)).thenReturn("stringKey")
        // Start string
        `when`(parser.text).thenReturn("test ")
        stringResourceParser.onStartTag(parser)
        stringResourceParser.onText(parser)
        // Add inner tag with attributes text
        `when`(parser.name).thenReturn("b")
        `when`(parser.getAttributeName(0)).thenReturn("attrName")
        `when`(parser.getAttributeValue(0)).thenReturn("attrValue")
        stringResourceParser.onStartTag(parser)
        `when`(parser.text).thenReturn("test")
        stringResourceParser.onText(parser)
        stringResourceParser.onEndTag(parser)
        // End string
        `when`(parser.name).thenReturn("string")
        stringResourceParser.onEndTag(parser)
        val expectedKey = "stringKey"
        val expectedValue = "test <b attrName=\"attrValue\">test</b>"

        val stringData = stringResourceParser.getLanguageData().resources.first()
        assertThat(stringData.stringKey, `is`(expectedKey))
        assertThat(stringData.stringValue, `is`(expectedValue))
    }

    @Test
    fun clearDataTest() {
        // Given
        val stringResourceParser = StringResourceParser()
        // Add data
        val mockParser = givenStringDataParser()
        stringResourceParser.onStartTag(mockParser)
        stringResourceParser.onText(mockParser)
        stringResourceParser.onEndTag(mockParser)
        addArrayItem(stringResourceParser, mockParser)
        addPluralItem(stringResourceParser, mockParser)

        // When
        stringResourceParser.clearData()

        // Then
        val languageData = stringResourceParser.getLanguageData()
        assertThat(languageData.resources.size, `is`(0))
        assertThat(languageData.arrays.size, `is`(0))
        assertThat(languageData.plurals.size, `is`(0))
    }

    private fun parserArrayItem(
        stringResourceParser: StringResourceParser,
        parser: XmlPullParser,
        text: String
    ) {
        // start array item tag
        `when`(parser.name).thenReturn("item")
        stringResourceParser.onStartTag(parser)
        // on text
        `when`(parser.text).thenReturn(text)
        stringResourceParser.onText(parser)
        // close item tag
        `when`(parser.name).thenReturn("item")
        stringResourceParser.onEndTag(parser)
    }

    private fun addArrayItem(stringResourceParser: StringResourceParser, parser: XmlPullParser) {
        // start array tag
        `when`(parser.name).thenReturn("string-array")
        `when`(parser.attributeCount).thenReturn(1)
        `when`(parser.getAttributeValue(0)).thenReturn("arrayKey")
        stringResourceParser.onStartTag(parser)
        // parse array items
        parserArrayItem(stringResourceParser, parser, "arrayValue")
        parserArrayItem(stringResourceParser, parser, "arrayValue1")
        // close array tag
        `when`(parser.name).thenReturn("string-array")
        stringResourceParser.onEndTag(parser)
    }

    private fun addPluralItem(stringResourceParser: StringResourceParser, parser: XmlPullParser) {
        // start plural tag
        `when`(parser.name).thenReturn("plurals")
        `when`(parser.attributeCount).thenReturn(1)
        `when`(parser.getAttributeValue(0)).thenReturn("pluralKey")
        stringResourceParser.onStartTag(parser)
        // parse plural items
        parserPluralItem(stringResourceParser, parser, "key0", "value0")
        parserPluralItem(stringResourceParser, parser, "key1", "value1")
        // close plural tag
        `when`(parser.name).thenReturn("plurals")
        stringResourceParser.onEndTag(parser)
    }

    private fun parserPluralItem(
        stringResourceParser: StringResourceParser,
        parser: XmlPullParser,
        quantityKey: String,
        text: String
    ) {
        // start plural item tag
        `when`(parser.name).thenReturn("item")
        `when`(parser.getAttributeValue(0)).thenReturn(quantityKey)
        stringResourceParser.onStartTag(parser)
        // on text
        `when`(parser.text).thenReturn(text)
        stringResourceParser.onText(parser)
        // close item tag
        `when`(parser.name).thenReturn("item")
        stringResourceParser.onEndTag(parser)
    }

    private fun givenStringDataParser(): XmlPullParser {
        val parser = mock(XmlPullParser::class.java)
        `when`(parser.name).thenReturn("string")
        `when`(parser.attributeCount).thenReturn(1)
        `when`(parser.getAttributeValue(0)).thenReturn("stringKey")
        `when`(parser.text).thenReturn("stringValue")

        return parser
    }
}
