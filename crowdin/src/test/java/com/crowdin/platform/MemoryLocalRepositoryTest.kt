package com.crowdin.platform

import com.crowdin.platform.data.local.MemoryLocalRepository
import com.crowdin.platform.data.model.ArrayData
import com.crowdin.platform.data.model.AuthInfo
import com.crowdin.platform.data.model.AuthResponse
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.PluralData
import com.crowdin.platform.data.model.StringData
import com.crowdin.platform.util.getFormattedCode
import java.util.Locale
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class MemoryLocalRepositoryTest {

    @Test
    fun whenGetSavedLanguageData_shouldReturnSameData() {
        // Given
        val memoryLocalRepository = MemoryLocalRepository()
        val expectedLanguageData = givenLanguageData("EN")
        memoryLocalRepository.saveLanguageData(expectedLanguageData)

        // When
        val actualLanguageData = memoryLocalRepository.getLanguageData("EN")

        // Then
        assertThat(actualLanguageData, `is`(expectedLanguageData))
    }

    @Test
    fun whenUpdateSavedLanguageData_shouldReturnSameData() {
        // Given
        val memoryLocalRepository = MemoryLocalRepository()
        val expectedLanguageData = givenLanguageData("EN")
        val updateData = givenLanguageData("EN")
        memoryLocalRepository.saveLanguageData(expectedLanguageData)
        memoryLocalRepository.saveLanguageData(updateData)

        // When
        val actualLanguageData = memoryLocalRepository.getLanguageData("EN")

        // Then
        assertThat(actualLanguageData, `is`(expectedLanguageData))
    }

    @Test
    fun whenSaveString_shouldReturnSameString() {
        // Given
        val memoryLocalRepository = MemoryLocalRepository()
        val key = "test key"
        val expectedValue = "Test value"

        // When
        memoryLocalRepository.setString("EN", key, expectedValue)

        // Then
        val actualValue = memoryLocalRepository.getString("EN", key)
        assertThat(actualValue, `is`(expectedValue))
    }

    @Test
    fun whenGetUnknownString_shouldReturnNull() {
        // Given
        val memoryLocalRepository = MemoryLocalRepository()
        memoryLocalRepository.setString("EN", "test key", "Test value")

        // When
        val actualValue = memoryLocalRepository.getString("EN", "randomKey")

        // Then
        assertThat(actualValue, nullValue())
    }

    @Test
    fun whenSetStringData_shouldReturnSameString() {
        // Given
        val memoryLocalRepository = MemoryLocalRepository()
        val key = "test key"
        val expectedValue = "Test value"
        val stringData = StringData(key, expectedValue)

        // When
        memoryLocalRepository.setStringData("EN", stringData)

        // Then
        val actualValue = memoryLocalRepository.getString("EN", key)
        assertThat(actualValue, `is`(expectedValue))
    }

    @Test
    fun whenSetArrayData_shouldReturnSameArray() {
        // Given
        val locale = Locale.getDefault().getFormattedCode()
        val memoryLocalRepository = MemoryLocalRepository()
        val array = arrayOf("array0", "array1")
        val arrayData = ArrayData("arrayKey", array)
        val arrayData1 = ArrayData("arrayKey1", array)

        // When
        memoryLocalRepository.setArrayData(locale, arrayData)
        memoryLocalRepository.setArrayData(locale, arrayData)
        memoryLocalRepository.setArrayData(locale, arrayData1)
        val actualValue = memoryLocalRepository.getStringArray("arrayKey")

        // Then
        assertThat(actualValue, `is`(array))
    }

    @Test
    fun whenGetUnknownArrayData_shouldReturnNull() {
        // Given
        val locale = Locale.getDefault().getFormattedCode()
        val memoryLocalRepository = MemoryLocalRepository()
        val arrayData = ArrayData("test key", arrayOf("array0", "array1"))
        memoryLocalRepository.setArrayData(locale, arrayData)

        // When
        val actualValue = memoryLocalRepository.getStringArray("randomKey")

        // Then
        assertThat(actualValue, nullValue())
    }

    @Test
    fun whenSetPluralData_shouldReturnSamePlural() {
        // Given
        val locale = Locale.getDefault().getFormattedCode()
        val memoryLocalRepository = MemoryLocalRepository()
        val resourceKey0 = "test key"
        val resourceKey1 = "test key1"
        val quantityKey0 = "key0"
        val quantityKey1 = "key1"
        val expectedPlural = mutableMapOf(Pair(quantityKey0, "value0"))
        val expectedPlural1 = mutableMapOf(Pair(quantityKey1, "value1"))
        val pluralData = PluralData(resourceKey0, expectedPlural)
        val pluralData1 = PluralData(resourceKey1, expectedPlural1)
        val expectedValue = "value0"
        val expectedValue1 = "value1"

        // When
        memoryLocalRepository.setPluralData(locale, pluralData)
        memoryLocalRepository.setPluralData(locale, pluralData1)
        memoryLocalRepository.setPluralData(locale, pluralData1)

        // Then
        val value1 = memoryLocalRepository.getStringPlural(resourceKey0, quantityKey0)
        assertThat(value1, `is`(expectedValue))
        val value2 = memoryLocalRepository.getStringPlural(resourceKey1, quantityKey1)
        assertThat(value2, `is`(expectedValue1))
    }

    @Test
    fun whenGetUnknownPluralData_shouldReturnNull() {
        // Given
        val locale = Locale.getDefault().getFormattedCode()
        val memoryLocalRepository = MemoryLocalRepository()
        val expectedPlural = mutableMapOf(Pair("key0", "value0"))
        val pluralData = PluralData("test key0", expectedPlural)
        memoryLocalRepository.setPluralData(locale, pluralData)

        // When
        val value = memoryLocalRepository.getStringPlural("randomKey", "randomKey")

        // Then
        assertThat(value, nullValue())
    }

    @Test
    fun isExistTest() {
        // Given
        val memoryLocalRepository = MemoryLocalRepository()
        memoryLocalRepository.saveLanguageData(LanguageData("EN"))

        // When
        val isExist = memoryLocalRepository.isExist("EN")

        // Then
        assertThat(isExist, `is`(true))
    }

    @Test
    fun saveDataTest() {
        // Given
        val memoryLocalRepository = MemoryLocalRepository()
        val expectedAuthInfo = AuthInfo(
            AuthResponse(
                "", 0, "", ""
            )
        )

        // When
        memoryLocalRepository.saveData("auth_info", expectedAuthInfo)

        // Then
        val actualAuthInfo: AuthInfo? =
            memoryLocalRepository.getData("auth_info", AuthInfo::class.java)
        assertThat(actualAuthInfo, `is`(expectedAuthInfo))
    }

    @Test
    fun getTextData_searchPluralTest() {
        // Given
        val locale = Locale.getDefault().getFormattedCode()
        val memoryLocalRepository = MemoryLocalRepository()
        val languageData = givenLanguageData(locale)
        memoryLocalRepository.saveLanguageData(languageData)
        val expectedPluralName = "Plural1"
        val expectedNumber = 1

        // When
        val textMetaData = memoryLocalRepository.getTextData("pluralValue1")

        // Then
        assertThat(textMetaData.pluralName, `is`(expectedPluralName))
        assertThat(textMetaData.pluralQuantity, `is`(expectedNumber))
    }

    @Test
    fun getTextData_searchStringTest() {
        // Given
        val locale = Locale.getDefault().getFormattedCode()
        val memoryLocalRepository = MemoryLocalRepository()
        val languageData = givenLanguageData(locale)
        memoryLocalRepository.saveLanguageData(languageData)
        val expectedTextKey = "String1"

        // When
        val textMetaData = memoryLocalRepository.getTextData("string1")

        // Then
        assertThat(textMetaData.textAttributeKey, `is`(expectedTextKey))
    }

    @Test
    fun getTextData_searchArrayTest() {
        // Given
        val locale = Locale.getDefault().getFormattedCode()
        val memoryLocalRepository = MemoryLocalRepository()
        val languageData = givenLanguageData(locale)
        memoryLocalRepository.saveLanguageData(languageData)
        val expectedTextKey = "Array1"
        val expectedIndex = 1

        // When
        val textMetaData = memoryLocalRepository.getTextData("array1:1")

        // Then
        assertThat(textMetaData.arrayName, `is`(expectedTextKey))
        assertThat(textMetaData.arrayIndex, `is`(expectedIndex))
    }

    private fun givenLanguageData(locale: String): LanguageData {
        val languageData = LanguageData(locale)
        val listOfStringData = mutableListOf(
            StringData("String0", "string0"),
            StringData("String1", "string1")
        )
        val listOfArrayData = mutableListOf(
            ArrayData("Array0", arrayOf("array0:0", "array0:1")),
            ArrayData("Array1", arrayOf("array1:0", "array1:1")),
            ArrayData("Array2", arrayOf("array2:0", "array2:1"))
        )
        val listOfPluralData = mutableListOf(
            PluralData("Plural0", mutableMapOf(Pair("pluralKey0", "pluralValue0")), 0),
            PluralData("Plural1", mutableMapOf(Pair("pluralKey1", "pluralValue1")), 1),
            PluralData("Plural2", mutableMapOf(Pair("pluralKey2", "pluralValue2")), 2)
        )
        languageData.resources = listOfStringData
        languageData.arrays = listOfArrayData
        languageData.plurals = listOfPluralData

        return languageData
    }
}
