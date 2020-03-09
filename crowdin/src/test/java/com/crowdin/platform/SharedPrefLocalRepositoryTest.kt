package com.crowdin.platform

import android.content.Context
import android.content.SharedPreferences
import com.crowdin.platform.data.local.MemoryLocalRepository
import com.crowdin.platform.data.local.SharedPrefLocalRepository
import com.crowdin.platform.data.model.ArrayData
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.PluralData
import com.crowdin.platform.data.model.StringData
import com.google.gson.Gson
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class SharedPrefLocalRepositoryTest {

    private lateinit var mockMemoryLocalRepository: MemoryLocalRepository
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var context: Context

    @Before
    fun setUp() {
        sharedPrefs = mock(SharedPreferences::class.java)
        editor = mock(SharedPreferences.Editor::class.java)
        `when`(sharedPrefs.edit()).thenReturn(editor)
        `when`(editor.putString(any(), any())).thenReturn(editor)
        `when`(editor.remove(any())).thenReturn(editor)
        context = mock(Context::class.java)
        `when`(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs)
        mockMemoryLocalRepository = mock(MemoryLocalRepository::class.java)
    }

    @Test
    fun saveLanguageDataTest() {
        // Given
        val sharedPrefLocalRepository = SharedPrefLocalRepository(context, MemoryLocalRepository())
        val expectedLanguage = "EN"
        val languageData = LanguageData(expectedLanguage)
        val expectedJson = Gson().toJson(languageData)

        // When
        sharedPrefLocalRepository.saveLanguageData(languageData)

        // Then
        verify(sharedPrefs).edit()
        verify(editor).putString(expectedLanguage, expectedJson)
    }

    @Test
    fun setStringTest() {
        // Given
        val sharedPrefLocalRepository = SharedPrefLocalRepository(context, MemoryLocalRepository())
        val key = "key"
        val value = "value"
        val languageData = LanguageData("EN")
        languageData.resources.add(StringData(key, value))
        val expectedJson = Gson().toJson(languageData)
        val expectedLanguage = "EN"

        // When
        sharedPrefLocalRepository.setString("EN", key, value)

        // Then
        verify(sharedPrefs).edit()
        verify(editor).putString(expectedLanguage, expectedJson)
    }

    @Test
    fun setStringDataTest() {
        // Given
        val sharedPrefLocalRepository = SharedPrefLocalRepository(context, MemoryLocalRepository())
        val stringData = StringData("key", "value")
        val languageData = LanguageData("EN")
        languageData.resources.add(stringData)
        val expectedJson = Gson().toJson(languageData)
        val expectedLanguage = "EN"

        // When
        sharedPrefLocalRepository.setStringData("EN", stringData)

        // Then
        verify(sharedPrefs).edit()
        verify(editor).putString(expectedLanguage, expectedJson)
    }

    @Test
    fun setArrayDataTest() {
        // Given
        val sharedPrefLocalRepository = SharedPrefLocalRepository(context, MemoryLocalRepository())
        val arrayData = ArrayData("key", arrayOf("test", "test1"))
        val languageData = LanguageData("EN")
        languageData.arrays.add(arrayData)
        val expectedJson = Gson().toJson(languageData)
        val expectedLanguage = "EN"

        // When
        sharedPrefLocalRepository.setArrayData("EN", arrayData)

        // Then
        verify(sharedPrefs).edit()
        verify(editor).putString(expectedLanguage, expectedJson)
    }

    @Test
    fun setPluralDataTest() {
        // Given
        val sharedPrefLocalRepository = SharedPrefLocalRepository(context, MemoryLocalRepository())
        val pluralData = PluralData("key", mutableMapOf(Pair("test", "test1")))
        val languageData = LanguageData("EN")
        languageData.plurals.add(pluralData)
        val expectedJson = Gson().toJson(languageData)
        val expectedLanguage = "EN"

        // When
        sharedPrefLocalRepository.setPluralData("EN", pluralData)

        // Then
        verify(sharedPrefs).edit()
        verify(editor).putString(expectedLanguage, expectedJson)
    }

    @Test
    fun getStringTest() {
        // Given
        val sharedPrefLocalRepository =
            SharedPrefLocalRepository(context, mockMemoryLocalRepository)
        val expectedLanguage = "EN"
        val expectedKey = "key"

        // When
        sharedPrefLocalRepository.getString("EN", expectedKey)

        // Then
        verify(mockMemoryLocalRepository).getString(expectedLanguage, expectedKey)
    }

    @Test
    fun getLanguageDataTest() {
        // Given
        val sharedPrefLocalRepository =
            SharedPrefLocalRepository(context, mockMemoryLocalRepository)
        val expectedLanguage = "EN"

        // When
        sharedPrefLocalRepository.getLanguageData("EN")

        // Then
        verify(mockMemoryLocalRepository).getLanguageData(expectedLanguage)
    }

    @Test
    fun getStringArrayTest() {
        // Given
        val sharedPrefLocalRepository =
            SharedPrefLocalRepository(context, mockMemoryLocalRepository)
        val expectedKey = "key"

        // When
        sharedPrefLocalRepository.getStringArray("key")

        // Then
        verify(mockMemoryLocalRepository).getStringArray(expectedKey)
    }

    @Test
    fun getStringPluralTest() {
        // Given
        val sharedPrefLocalRepository =
            SharedPrefLocalRepository(context, mockMemoryLocalRepository)
        val expectedResourceKey = "resourceKey"
        val expectedQuantityKey = "quantityKey"

        // When
        sharedPrefLocalRepository.getStringPlural("resourceKey", "quantityKey")

        // Then
        verify(mockMemoryLocalRepository).getStringPlural(expectedResourceKey, expectedQuantityKey)
    }

    @Test
    fun isExistTest() {
        // Given
        val sharedPrefLocalRepository =
            SharedPrefLocalRepository(context, mockMemoryLocalRepository)
        val expectedLanguage = "EN"

        // When
        sharedPrefLocalRepository.isExist("EN")

        // Then
        verify(mockMemoryLocalRepository).isExist(expectedLanguage)
    }

    @Test
    fun getTextDataTest() {
        // Given
        val sharedPrefLocalRepository =
            SharedPrefLocalRepository(context, mockMemoryLocalRepository)
        val expectedText = "text"

        // When
        sharedPrefLocalRepository.getTextData("text")

        // Then
        verify(mockMemoryLocalRepository).getTextData(expectedText)
    }

    @Test
    fun saveDataTest() {
        // Given
        val sharedPrefLocalRepository =
            SharedPrefLocalRepository(context, mockMemoryLocalRepository)
        val expectedType = "type"
        val data = StringData()

        // When
        sharedPrefLocalRepository.saveData("type", data)

        // Then
        verify(mockMemoryLocalRepository).saveData(expectedType, data)
    }

    @Test
    fun saveData_removeOldTest() {
        // Given
        val sharedPrefLocalRepository =
            SharedPrefLocalRepository(context, mockMemoryLocalRepository)
        val expectedType = "type"
        val data = null

        // When
        sharedPrefLocalRepository.saveData("type", data)

        // Then
        verify(sharedPrefs).edit()
        verify(editor).remove(expectedType)
    }

    @Test
    fun getDataTest() {
        // Given
        val stringData = StringData()
        `when`(mockMemoryLocalRepository.getData<StringData>(any(), any())).thenReturn(stringData)
        val sharedPrefLocalRepository =
            SharedPrefLocalRepository(context, mockMemoryLocalRepository)

        // When
        val result =
            sharedPrefLocalRepository.getData<StringData>("stringData", StringData::class.java)

        // Then
        assertThat(result, instanceOf(StringData::class.java))
        assertThat((result as StringData), `is`(stringData))
    }

    @Test
    fun getData_nullDataTest() {
        // Given
        val stringData = null
        `when`(mockMemoryLocalRepository.getData<StringData>(any(), any())).thenReturn(stringData)
        `when`(sharedPrefs.getString(any(), any())).thenReturn(null)
        val sharedPrefLocalRepository =
            SharedPrefLocalRepository(context, mockMemoryLocalRepository)
        val expectedType = "stringData"

        // When
        val result =
            sharedPrefLocalRepository.getData<StringData>("stringData", StringData::class.java)

        // Then
        verify(sharedPrefs).getString(expectedType, null)
        assertThat(result, nullValue())
    }
}
