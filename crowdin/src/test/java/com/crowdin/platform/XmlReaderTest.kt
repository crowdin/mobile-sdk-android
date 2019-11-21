package com.crowdin.platform

import com.crowdin.platform.data.model.ArrayData
import com.crowdin.platform.data.model.PluralData
import com.crowdin.platform.data.model.StringData
import com.crowdin.platform.data.parser.Parser
import com.crowdin.platform.data.parser.XmlReader
import java.io.InputStream
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

class XmlReaderTest {

    @Test
    fun closeTest() {
        // Given
        val parser = mock(Parser::class.java)
        val xmlReader = XmlReader(parser)

        // When
        xmlReader.close()

        // Then
        verify(parser).clearData()
    }

    @Test
    fun parseInput_nullParserTest() {
        // Given
        val parser = mock(Parser::class.java)
        val xmlReader = XmlReader(parser)
        val xmlPullParserFactory = mock(XmlPullParserFactory::class.java)

        // When
        val result = xmlReader.parseInput(mock(InputStream::class.java), xmlPullParserFactory)

        // Then
        assertThat(result.language, `is`(""))
        assertThat(result.resources, `is`(emptyList<StringData>()))
        assertThat(result.arrays, `is`(emptyList<ArrayData>()))
        assertThat(result.plurals, `is`(emptyList<PluralData>()))
    }

    @Test
    fun parseInput_PullParserTest() {
        // Given
        val parser = mock(Parser::class.java)
        val xmlReader = XmlReader(parser)
        val xmlPullParserFactory = mock(XmlPullParserFactory::class.java)
        val xmlPullParser = spy(XmlPullParser::class.java)
        `when`(xmlPullParserFactory.newPullParser()).thenReturn(xmlPullParser)
        `when`(xmlPullParser.eventType).thenReturn(1)

        // When
        xmlReader.parseInput(mock(InputStream::class.java), xmlPullParserFactory)

        // Then
        verify(parser).getLanguageData()
    }
}
