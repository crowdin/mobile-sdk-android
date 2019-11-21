package com.crowdin.platform

import com.crowdin.platform.data.model.ArrayData
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.PluralData
import com.crowdin.platform.data.model.StringData
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test

class LanguageDataTest {

    @Test
    fun updateLanguageDataResourcesTest() {
        // Given
        val actualLanguageData = LanguageData()
        val secondLanguageData = LanguageData()
        val expectedListOfStringData = mutableListOf(
                StringData("Key0"),
                StringData("Key1"),
                StringData("Key2"))
        secondLanguageData.resources = expectedListOfStringData

        // When
        actualLanguageData.updateResources(secondLanguageData)

        // Then
        assertThat(actualLanguageData.resources, `is`(expectedListOfStringData))
    }

    @Test
    fun updateLanguageDataArrayTest() {
        // Given
        val actualLanguageData = LanguageData()
        val secondLanguageData = LanguageData()
        val expectedListOfArrayData = mutableListOf(
                ArrayData("Key0"),
                ArrayData("Key1"),
                ArrayData("Key2"))
        secondLanguageData.arrays = expectedListOfArrayData

        // When
        actualLanguageData.updateResources(secondLanguageData)

        // Then
        assertThat(actualLanguageData.arrays, `is`(expectedListOfArrayData))
    }

    @Test
    fun updateLanguageDataPluralTest() {
        // Given
        val actualLanguageData = LanguageData()
        val secondLanguageData = LanguageData()
        val expectedListOfPluralData = mutableListOf(
                PluralData("Key0"),
                PluralData("Key1"),
                PluralData("Key2"))
        secondLanguageData.plurals = expectedListOfPluralData

        // When
        actualLanguageData.updateResources(secondLanguageData)

        // Then
        assertThat(actualLanguageData.plurals, `is`(expectedListOfPluralData))
    }
}
