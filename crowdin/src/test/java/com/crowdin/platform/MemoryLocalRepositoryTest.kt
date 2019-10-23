package com.crowdin.platform

import com.crowdin.platform.data.local.MemoryLocalRepository
import com.crowdin.platform.data.model.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.util.*

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
        val locale = Locale.getDefault().toString()
        val memoryLocalRepository = MemoryLocalRepository()
        val key = "test key"
        val expectedArray = arrayOf("array0", "array1")
        val arrayData = ArrayData(key, expectedArray)

        // When
        memoryLocalRepository.setArrayData(locale, arrayData)

        // Then
        val actualValue = memoryLocalRepository.getStringArray(key)
        assertThat(actualValue, `is`(expectedArray))
    }

    @Test
    fun whenSetPluralData_shouldReturnSamePlural() {
        // Given
        val locale = Locale.getDefault().toString()
        val memoryLocalRepository = MemoryLocalRepository()
        val resourceKey = "test key"
        val quantityKey = "key0"
        val expectedValue = "value0"
        val expectedPlural = mutableMapOf(Pair(quantityKey, "value0"))
        val pluralData = PluralData(resourceKey, expectedPlural)

        // When
        memoryLocalRepository.setPluralData(locale, pluralData)

        // Then
        val actualValue = memoryLocalRepository.getStringPlural(resourceKey, quantityKey)
        assertThat(actualValue, `is`(expectedValue))
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
        val expectedAuthInfo = AuthInfo(AuthResponse(
                "", 0, "", ""))

        // When
        memoryLocalRepository.saveData("auth_info", expectedAuthInfo)

        // Then
        val actualAuthInfo = memoryLocalRepository.getData("auth_info", AuthInfo::class.java) as AuthInfo
        assertThat(actualAuthInfo, `is`(expectedAuthInfo))
    }

    @Test
    fun getTextData_searchPluralTest() {
        // Given
        val locale = Locale.getDefault().toString()
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
        val locale = Locale.getDefault().toString()
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
        val locale = Locale.getDefault().toString()
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
                StringData("String1", "string1"))
        val listOfArrayData = mutableListOf(
                ArrayData("Array0", arrayOf("array0:0", "array0:1")),
                ArrayData("Array1", arrayOf("array1:0", "array1:1")),
                ArrayData("Array2", arrayOf("array2:0", "array2:1")))
        val listOfPluralData = mutableListOf(
                PluralData("Plural0", mutableMapOf(Pair("pluralKey0", "pluralValue0")), 0),
                PluralData("Plural1", mutableMapOf(Pair("pluralKey1", "pluralValue1")), 1),
                PluralData("Plural2", mutableMapOf(Pair("pluralKey2", "pluralValue2")), 2))
        languageData.resources = listOfStringData
        languageData.arrays = listOfArrayData
        languageData.plurals = listOfPluralData

        return languageData
    }
}