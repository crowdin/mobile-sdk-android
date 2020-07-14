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
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class XmlReaderTest {

    @Test
    fun clearDataTest() {
        // Given
        val parser = mock(Parser::class.java)
        val xmlReader = XmlReader(parser)

        // When
        xmlReader.parseInput(mock(InputStream::class.java))

        // Then
        verify(parser).clearData()
    }

    @Test
    fun parseInput_nullParserTest() {
        // Given
        val parser = mock(Parser::class.java)
        val xmlReader = XmlReader(parser)

        // When
        val result = xmlReader.parseInput(mock(InputStream::class.java))

        // Then
        assertThat(result.language, `is`(""))
        assertThat(result.resources, `is`(emptyList<StringData>()))
        assertThat(result.arrays, `is`(emptyList<ArrayData>()))
        assertThat(result.plurals, `is`(emptyList<PluralData>()))
    }
}
