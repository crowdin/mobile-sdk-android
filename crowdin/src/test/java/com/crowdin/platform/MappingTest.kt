package com.crowdin.platform

import com.crowdin.platform.data.getMappingValueForKey
import com.crowdin.platform.data.model.ArrayData
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.PluralData
import com.crowdin.platform.data.model.StringData
import com.crowdin.platform.data.model.TextMetaData
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.Assert.assertThat
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class MappingTest {

    @Test
    fun getMappingValueForStringDataKeyTest() {
        // Given
        val mockTextMetaData = mock(TextMetaData::class.java)
        `when`(mockTextMetaData.hasAttributeKey).thenReturn(true)
        `when`(mockTextMetaData.textAttributeKey).thenReturn("key1")
        val languageData = LanguageData()
        languageData.resources = mutableListOf(
            StringData("key0", "value0"),
            StringData("key1", "value1"),
            StringData("key2", "value2")
        )
        val expectedValue = "value1"

        // When
        val actualResult = getMappingValueForKey(mockTextMetaData, languageData)

        // Then
        assertThat(actualResult, `is`(expectedValue))
    }

    @Test
    fun getMappingValueForArrayDataKeyTest() {
        // Given
        val mockTextMetaData = mock(TextMetaData::class.java)
        `when`(mockTextMetaData.isArrayItem).thenReturn(true)
        `when`(mockTextMetaData.arrayName).thenReturn("key1")
        `when`(mockTextMetaData.arrayIndex).thenReturn(2)
        val languageData = LanguageData()
        languageData.arrays = mutableListOf(
            ArrayData("key0", arrayOf("key0_value0", "key0_value1", "key0_value2")),
            ArrayData("key1", arrayOf("key1_value1", "key1_value1", "key1_value2")),
            ArrayData("key2", arrayOf("key2_value0", "key2_value1", "key2_value2"))
        )
        val expectedValue = "key1_value2"

        // When
        val actualResult = getMappingValueForKey(mockTextMetaData, languageData)

        // Then
        assertThat(actualResult, `is`(expectedValue))
    }

    @Test
    fun getMappingValueForPluralDataKeyTest() {
        // Given
        val mockTextMetaData = mock(TextMetaData::class.java)
        `when`(mockTextMetaData.isPluralData).thenReturn(true)
        `when`(mockTextMetaData.pluralName).thenReturn("key1")
        val languageData = LanguageData()
        languageData.plurals = mutableListOf(
            PluralData("key0", mutableMapOf(Pair("key0", "value0"))),
            PluralData("key1", mutableMapOf(Pair("key1", "value1"))),
            PluralData("key2", mutableMapOf(Pair("key2", "value2")))
        )
        val expectedValue = "value1"

        // When
        val actualResult = getMappingValueForKey(mockTextMetaData, languageData)

        // Then
        assertThat(actualResult, `is`(expectedValue))
    }

    @Test
    fun getMappingValueForKey_itemNotFoundTest() {
        // Given
        val textMetaData = TextMetaData()
        val languageData = LanguageData()

        // When
        val actualResult = getMappingValueForKey(textMetaData, languageData)

        // Then
        assertThat(actualResult, `is`(nullValue()))
    }
}
