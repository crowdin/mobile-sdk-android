package com.crowdin.platform

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.crowdin.platform.data.model.ArrayData
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.PluralData
import com.crowdin.platform.data.model.StringData
import com.crowdin.platform.data.parser.JsonReader
import com.crowdin.platform.data.parser.ReaderFactory
import com.crowdin.platform.data.parser.XmlReader
import com.crowdin.platform.data.remote.Connectivity
import com.crowdin.platform.data.remote.CrowdinRetrofitService
import com.crowdin.platform.data.remote.NetworkType
import com.crowdin.platform.data.remote.api.CrowdinTranslationApi
import com.crowdin.platform.util.convertToJson
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

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

    @Test
    fun isOnlineTest() {
        // Given
        val mockContext = givenMockContext()

        // When
        val actual = Connectivity.isOnline(mockContext)

        // Then
        assertThat(actual, `is`(true))
    }

    @Test
    fun isNetworkAllowed_whenCellularOn_shouldReturnTrue() {
        // Given
        val mockContext = givenMockContext(0) // cellular type

        // When
        val actual = Connectivity.isNetworkAllowed(mockContext, NetworkType.CELLULAR)

        // Then
        assertThat(actual, `is`(true))
    }

    @Test
    fun isNetworkAllowed_whenCellularOffAndWifiOn_shouldReturnFalse() {
        // Given
        val mockContext = givenMockContext(1) // wifi type

        // When
        val actual = Connectivity.isNetworkAllowed(mockContext, NetworkType.CELLULAR)

        // Then
        assertThat(actual, `is`(false))
    }

    @Test
    fun isNetworkAllowed_whenWifiOn_shouldReturnTrue() {
        // Given
        val mockContext = givenMockContext(1)

        // When
        val actual = Connectivity.isNetworkAllowed(mockContext, NetworkType.WIFI)

        // Then
        assertThat(actual, `is`(true))
    }

    @Test
    fun translationApiTest() {
        val crowdinApi = CrowdinRetrofitService.getCrowdinTranslationApi()

        assertThat(crowdinApi, instanceOf(CrowdinTranslationApi::class.java))
    }

    @Test
    fun crowdinPreferences_setStringTest() {
        // Given
        val mockContext = mock(Context::class.java)
        val mockPref = mock(SharedPreferences::class.java)
        val mockEditor = mock(SharedPreferences.Editor::class.java)
        `when`(mockPref.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putString(any(), any())).thenReturn(mockEditor)
        `when`(
            mockContext.getSharedPreferences(
                "com.crowdin.platform.string.preferences",
                Context.MODE_PRIVATE
            )
        ).thenReturn(mockPref)
        val crowdinPreferences = CrowdinPreferences(mockContext)

        // When
        crowdinPreferences.setString("key0", "value0")

        // Then
        verify(mockPref).edit()
        verify(mockEditor).putString("key0", "value0")
    }

    @Test
    fun crowdinPreferences_getStringTest() {
        // Given
        val mockContext = mock(Context::class.java)
        val mockPref = mock(SharedPreferences::class.java)
        `when`(
            mockContext.getSharedPreferences(
                "com.crowdin.platform.string.preferences",
                Context.MODE_PRIVATE
            )
        ).thenReturn(mockPref)
        val crowdinPreferences = CrowdinPreferences(mockContext)

        // When
        crowdinPreferences.getString("key0")

        // Then
        verify(mockPref).getString("key0", "")
    }

    private fun givenMockContext(networkType: Int = 0): Context {
        val mockContext = mock(Context::class.java)
        val mockConnectivityManager = mock(ConnectivityManager::class.java)
        val mockNetworkInfo = mock(NetworkInfo::class.java)
        `when`(mockNetworkInfo.type).thenReturn(networkType)
        `when`(mockNetworkInfo.isConnected).thenReturn(true)
        `when`(mockConnectivityManager.activeNetworkInfo).thenReturn(mockNetworkInfo)
        `when`(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(
            mockConnectivityManager
        )

        return mockContext
    }
}
