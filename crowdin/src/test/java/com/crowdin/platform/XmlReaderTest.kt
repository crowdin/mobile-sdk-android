package com.crowdin.platform

import com.crowdin.platform.data.parser.Parser
import com.crowdin.platform.data.parser.XmlReader
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

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
}