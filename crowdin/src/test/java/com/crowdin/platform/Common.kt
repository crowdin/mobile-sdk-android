package com.crowdin.platform

import com.crowdin.platform.data.model.ArrayData
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.PluralData
import com.crowdin.platform.data.model.StringData
import com.crowdin.platform.data.parser.JsonReader
import com.crowdin.platform.data.parser.ReaderFactory
import com.crowdin.platform.data.parser.XmlReader
import com.crowdin.platform.util.convertToJson
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class Common {

    @Test
    fun convertToJsonTest() {
        // Given
        val languageData = LanguageData("en")
        languageData.resources = mutableListOf(
            StringData("key0", "value0"),
            StringData("key1", "value1")
        )
        languageData.arrays = mutableListOf(
            ArrayData("array0", arrayOf("one", "two"))
        )
        languageData.plurals = mutableListOf(
            PluralData("plural", mutableMapOf("one" to "value"))
        )
        val expectedJson =
            "{\"language\":\"en\",\"strings\":{\"key0\":\"value0\",\"key1\":\"value1\"},\"arrays\":{\"array0\":[\"one\",\"two\"]},\"plurals\":{\"plural\":{\"one\":\"value\"}}}"

        // When
        val actualJson = convertToJson(languageData)

        // Then
        assertThat(actualJson, equalTo(expectedJson))
    }

    @Test
    fun readerFactoryTest() {
        val xmlReader = ReaderFactory.createReader(ReaderFactory.ReaderType.XML)
        assertThat(xmlReader, instanceOf(XmlReader::class.java))
        val jsonReader = ReaderFactory.createReader(ReaderFactory.ReaderType.JSON)
        assertThat(jsonReader, instanceOf(JsonReader::class.java))
    }
}
