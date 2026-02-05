package com.crowdin.platform

import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.model.LanguageDetails
import com.crowdin.platform.data.model.ManifestData
import com.crowdin.platform.data.model.SupportedLanguages
import com.crowdin.platform.data.remote.CrowdingRepository
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import retrofit2.Call
import retrofit2.Response

class CrowdingRepositoryTest {
    private lateinit var mockDistributionApi: CrowdinDistributionApi
    private lateinit var testRepository: TestCrowdingRepository

    @Before
    fun setUp() {
        mockDistributionApi = mock(CrowdinDistributionApi::class.java)
        testRepository = TestCrowdingRepository(mockDistributionApi, "testHash")
    }

    @Test
    fun whenGetSupportedLanguages_shouldCallDistributionApi() {
        // Given
        val mockCall = mock(Call::class.java) as Call<SupportedLanguages>
        `when`(mockDistributionApi.getLanguages("testHash")).thenReturn(mockCall)
        val languages = givenSupportedLanguages()
        `when`(mockCall.execute()).thenReturn(Response.success(languages))

        // When
        testRepository.getSupportedLanguages()

        // Then
        verify(mockDistributionApi).getLanguages("testHash")
    }

    @Test
    fun whenGetSupportedLanguages_shouldReturnMapOfLanguages() {
        // Given
        val mockCall = mock(Call::class.java) as Call<SupportedLanguages>
        `when`(mockDistributionApi.getLanguages("testHash")).thenReturn(mockCall)
        val expectedLanguages = givenSupportedLanguages()
        `when`(mockCall.execute()).thenReturn(Response.success(expectedLanguages))

        // When
        val result = testRepository.getSupportedLanguages()

        // Then
        assertThat(result, equalTo(expectedLanguages))
        assertThat(result?.size, equalTo(3))
        assertThat(result?.get("de")?.name, equalTo("German"))
        assertThat(result?.get("it")?.locale, equalTo("it-IT"))
    }

    @Test
    fun whenGetLanguageInfo_shouldReturnLanguageDetailsDirectly() {
        // Given
        testRepository.crowdinLanguages = givenSupportedLanguages()

        // When
        val result = testRepository.getLanguageInfo("de")

        // Then
        assertThat(result, equalTo(LanguageDetails("German", "de-DE")))
    }

    @Test
    fun whenGetLanguageInfo_withInvalidCode_shouldReturnNull() {
        // Given
        testRepository.crowdinLanguages = givenSupportedLanguages()

        // When
        val result = testRepository.getLanguageInfo("fr")

        // Then
        assertThat(result, equalTo(null))
    }

    private fun givenSupportedLanguages(): SupportedLanguages =
        mapOf(
            "de" to LanguageDetails("German", "de-DE"),
            "it" to LanguageDetails("Italian", "it-IT"),
            "en" to LanguageDetails("English", "en-US"),
        )

    // Test implementation of abstract class
    private class TestCrowdingRepository(
        crowdinDistributionApi: CrowdinDistributionApi,
        distributionHash: String,
    ) : CrowdingRepository(crowdinDistributionApi, distributionHash) {
        override fun fetchData(
            configuration: android.content.res.Configuration?,
            languageCode: String?,
            supportedLanguages: SupportedLanguages?,
            languageDataCallback: LanguageDataCallback?,
        ) {
            // Test implementation
        }

        override fun onManifestDataReceived(
            configuration: android.content.res.Configuration?,
            manifest: ManifestData?,
            languageDataCallback: LanguageDataCallback?,
        ) {
            // Test implementation
        }
    }
}
