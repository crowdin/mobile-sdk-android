package com.crowdin.platform

import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.local.LocalRepository
import com.crowdin.platform.data.model.ArrayData
import com.crowdin.platform.data.model.AuthInfo
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.PluralData
import com.crowdin.platform.data.model.StringData
import com.crowdin.platform.data.remote.RemoteRepository
import com.crowdin.platform.util.FeatureFlags
import com.crowdin.platform.util.getFormattedCode
import java.lang.reflect.Type
import java.util.Locale
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions

class DataManagerTest {

    private lateinit var mockLocalRepository: LocalRepository
    private lateinit var mockRemoteRepository: RemoteRepository
    private lateinit var mockPreferences: Preferences
    private lateinit var mockLocalDataChangeObserver: LocalDataChangeObserver

    @Before
    fun setUp() {
        mockLocalRepository = mock(LocalRepository::class.java)
        mockRemoteRepository = mock(RemoteRepository::class.java)
        mockPreferences = mock(Preferences::class.java)
        mockLocalDataChangeObserver = mock(LocalDataChangeObserver::class.java)
    }

    @Test
    fun provideTextMetaDataTest() {
        // Given
        val dataManager = givenDataManager()
        val expectedText = "Text_Test"

        // When
        dataManager.provideTextMetaData("Text_Test")

        // Then
        verify(mockLocalRepository).getTextData(expectedText)
    }

    @Test
    fun getStringTest() {
        // Given
        val dataManager = givenDataManager()
        val expectedLanguage = "EN"
        val expectedStringKey = "Key_Test"

        // When
        dataManager.getString("EN", "Key_Test")

        // Then
        verify(mockLocalRepository).getString(expectedLanguage, expectedStringKey)
    }

    @Test
    fun setStringTest() {
        // Given
        val dataManager = givenDataManager()
        val expectedLanguage = "EN"
        val expectedStringKey = "Key_Test"
        val expectedValue = "value"

        // When
        dataManager.setString("EN", "Key_Test", "value")

        // Then
        verify(mockLocalRepository).setString(expectedLanguage, expectedStringKey, expectedValue)
    }

    @Test
    fun getStringArrayTest() {
        // Given
        val dataManager = givenDataManager()
        val expectedStringKey = "Key_Test"

        // When
        dataManager.getStringArray("Key_Test")

        // Then
        verify(mockLocalRepository).getStringArray(expectedStringKey)
    }

    @Test
    fun getStringPluralTest() {
        // Given
        val dataManager = givenDataManager()
        val expectedResourceKey = "Key_Resource"
        val expectedQuantityKey = "Key_Quantity"

        // When
        dataManager.getStringPlural("Key_Resource", "Key_Quantity")

        // Then
        verify(mockLocalRepository).getStringPlural(expectedResourceKey, expectedQuantityKey)
    }

    @Test
    fun whenRealTimeDisabled_shouldNotSaveReserveResources() {
        // Given
        val mockConfig = mock(CrowdinConfig::class.java)
        `when`(mockConfig.isRealTimeUpdateEnabled).thenReturn(false)
        FeatureFlags.registerConfig(mockConfig)
        val dataManager = givenDataManager()
        val mockStringData = mock(StringData::class.java)

        // When
        dataManager.saveReserveResources(mockStringData)

        // Then
        verifyNoInteractions(mockLocalRepository)
        verifyNoInteractions(mockRemoteRepository)
        verifyNoInteractions(mockLocalDataChangeObserver)
    }

    @Test
    fun whenRealTimeEnabled_shouldSaveStringDataReserveResources() {
        // Given
        val mockConfig = mock(CrowdinConfig::class.java)
        `when`(mockConfig.isRealTimeUpdateEnabled).thenReturn(true)
        FeatureFlags.registerConfig(mockConfig)
        val dataManager = givenDataManager()
        val mockStringData = mock(StringData::class.java)
        Locale.setDefault(Locale.US)
        val expectedLanguage = Locale.getDefault().getFormattedCode() + "-copy"

        // When
        dataManager.saveReserveResources(mockStringData)

        // Then
        verify(mockLocalRepository).setStringData(expectedLanguage, mockStringData)
    }

    @Test
    fun whenRealTimeEnabled_shouldSaveArrayDataReserveResources() {
        // Given
        val mockConfig = mock(CrowdinConfig::class.java)
        `when`(mockConfig.isRealTimeUpdateEnabled).thenReturn(true)
        FeatureFlags.registerConfig(mockConfig)
        val dataManager = givenDataManager()
        val mockArrayData = mock(ArrayData::class.java)
        Locale.setDefault(Locale.US)
        val expectedLanguage = Locale.getDefault().getFormattedCode() + "-copy"

        // When
        dataManager.saveReserveResources(arrayData = mockArrayData)

        // Then
        verify(mockLocalRepository).setArrayData(expectedLanguage, mockArrayData)
    }

    @Test
    fun whenRealTimeEnabled_shouldSavePluralDataReserveResources() {
        // Given
        val mockConfig = mock(CrowdinConfig::class.java)
        `when`(mockConfig.isRealTimeUpdateEnabled).thenReturn(true)
        FeatureFlags.registerConfig(mockConfig)
        val dataManager = givenDataManager()
        val mockPluralData = mock(PluralData::class.java)
        Locale.setDefault(Locale.US)
        val expectedLanguage = Locale.getDefault().getFormattedCode() + "-copy"

        // When
        dataManager.saveReserveResources(pluralData = mockPluralData)

        // Then
        verify(mockLocalRepository).setPluralData(expectedLanguage, mockPluralData)
    }

    @Test
    fun saveMappingTest() {
        // Given
        val dataManager = givenDataManager()
        val mockLanguageData = mock(LanguageData::class.java)

        // When
        dataManager.saveMapping(mockLanguageData)

        // Then
        verify(mockLocalRepository).saveLanguageData(mockLanguageData)
    }

    @Test
    fun getMappingTest() {
        // Given
        val dataManager = givenDataManager()
        val mockLanguageData = mock(LanguageData::class.java)
        val sourceLanguage = "EN"
        val mappingLanguage = "EN-mapping"
        `when`(mockLocalRepository.getLanguageData(mappingLanguage)).thenReturn(mockLanguageData)

        // When
        val result = dataManager.getMapping(sourceLanguage)

        // Then
        verify(mockLocalRepository).getLanguageData(mappingLanguage)
        assertThat(result, `is`(mockLanguageData))
    }

    @Test
    fun saveDataTest() {
        // Given
        val dataManager = givenDataManager()
        val type = "Type"
        val data = Any()

        // When
        dataManager.saveData(type, data)

        // Then
        verify(mockLocalRepository).saveData(type, data)
    }

    @Test
    fun getDataTest() {
        // Given
        val dataManager = givenDataManager()
        val type = "Type"
        val classType = Type::class.java

        // When
        dataManager.getData<Type>(type, classType)

        // Then
        verify(mockLocalRepository).getData<Type>(type, classType)
    }

    @Test
    fun isAuthorizedSuccessTest() {
        // Given
        val dataManager = givenDataManager()
        `when`(
            mockLocalRepository.getData<AuthInfo>(
                any(),
                any()
            )
        ).thenReturn(mock(AuthInfo::class.java))
        val expectedResult = true

        // When
        val actualResult = dataManager.isAuthorized()

        // Then
        assertThat(actualResult, `is`(expectedResult))
        verify(mockLocalRepository).getData<AuthInfo>("auth_info", AuthInfo::class.java)
    }

    @Test
    fun isAuthorizedFailureTest() {
        // Given
        val dataManager = givenDataManager()
        `when`(mockLocalRepository.getData<AuthInfo>(any(), any())).thenReturn(null)
        val expectedResult = false

        // When
        val actualResult = dataManager.isAuthorized()

        // Then
        assertThat(actualResult, `is`(expectedResult))
        verify(mockLocalRepository).getData<AuthInfo>("auth_info", AuthInfo::class.java)
    }

    @Test
    fun isTokenExpiredSuccessTest() {
        // Given
        val dataManager = givenDataManager()
        val mockAuthInfo = mock(AuthInfo::class.java)
        `when`(mockLocalRepository.getData<AuthInfo>(any(), any())).thenReturn(mockAuthInfo)
        `when`(mockAuthInfo.isExpired()).thenReturn(true)
        val expectedResult = true

        // When
        val actualResult = dataManager.isTokenExpired()

        // Then
        assertThat(actualResult, `is`(expectedResult))
        verify(mockLocalRepository).getData<AuthInfo>("auth_info", AuthInfo::class.java)
    }

    @Test
    fun isTokenExpiredFailureTest() {
        // Given
        val dataManager = givenDataManager()
        val mockAuthInfo = mock(AuthInfo::class.java)
        `when`(mockLocalRepository.getData<AuthInfo>(any(), any())).thenReturn(mockAuthInfo)
        `when`(mockAuthInfo.isExpired()).thenReturn(false)
        val expectedResult = false

        // When
        val actualResult = dataManager.isTokenExpired()

        // Then
        assertThat(actualResult, `is`(expectedResult))
        verify(mockLocalRepository).getData<AuthInfo>("auth_info", AuthInfo::class.java)
    }

    @Test
    fun getAccessTokenTest() {
        // Given
        val dataManager = givenDataManager()
        val mockAuthInfo = mock(AuthInfo::class.java)
        `when`(mockLocalRepository.getData<AuthInfo>(any(), any())).thenReturn(mockAuthInfo)
        val expectedAccessToken = "access_token_test"
        `when`(mockAuthInfo.accessToken).thenReturn(expectedAccessToken)

        // When
        val actualAccessToken = dataManager.getAccessToken()

        // Then
        assertThat(actualAccessToken, `is`(expectedAccessToken))
        verify(mockLocalRepository).getData<AuthInfo>("auth_info", AuthInfo::class.java)
    }

    @Test
    fun getRefreshTokenTest() {
        // Given
        val dataManager = givenDataManager()
        val mockAuthInfo = mock(AuthInfo::class.java)
        `when`(mockLocalRepository.getData<AuthInfo>(any(), any())).thenReturn(mockAuthInfo)
        val expectedRefreshToken = "refresh_token_test"
        `when`(mockAuthInfo.refreshToken).thenReturn(expectedRefreshToken)

        // When
        val actualRefreshToken = dataManager.getRefreshToken()

        // Then
        assertThat(actualRefreshToken, `is`(expectedRefreshToken))
        verify(mockLocalRepository).getData<AuthInfo>("auth_info", AuthInfo::class.java)
    }

    @Test
    fun addLoadingStateListenerTest() {
        // Given
        val dataManager = givenDataManager()
        val loadingStateListener = mock(LoadingStateListener::class.java)

        // When
        dataManager.addLoadingStateListener(loadingStateListener)

        // Then
        val result = dataManager.removeLoadingStateListener(loadingStateListener)
        assertThat(result, `is`(true))
    }

    @Test
    fun removeLoadingStateListener_nullListenersTest() {
        // Given
        val dataManager = givenDataManager()
        val loadingStateListener0 = mock(LoadingStateListener::class.java)
        val loadingStateListener1 = mock(LoadingStateListener::class.java)
        dataManager.addLoadingStateListener(loadingStateListener0)

        // When
        val result = dataManager.removeLoadingStateListener(loadingStateListener1)

        // Then
        assertThat(result, `is`(false))
    }

    @Test
    fun saveDistributionHash() {
        // Given
        val dataManager = givenDataManager()
        val hash = "test hash"

        // When
        dataManager.saveDistributionHash(hash)

        // Then
        verify(mockPreferences).setString("distribution_hash", hash)
    }

    @Test
    fun getDistributionHash() {
        // Given
        val dataManager = givenDataManager()

        // When
        dataManager.getDistributionHash()

        // Then
        verify(mockPreferences).getString("distribution_hash")
    }

    private fun givenDataManager(): DataManager =
        DataManager(
            mockRemoteRepository,
            mockLocalRepository,
            mockPreferences,
            mockLocalDataChangeObserver
        )
}
