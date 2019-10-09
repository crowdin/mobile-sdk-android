package com.crowdin.platform

import com.crowdin.platform.data.parser.Reader
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class StringDataManagerTest {

    private lateinit var mockDistributionApi: CrowdinDistributionApi
    private lateinit var mockReader: Reader

    @Before
    fun setUp() {
        mockDistributionApi = mock(CrowdinDistributionApi::class.java)
        mockReader = mock(Reader::class.java)
    }

    @Test
    fun testManager() {
        // Given
//        val stringDataRemoteRepository =
//                StringDataRemoteRepository(
//                        mockDistributionApi,
//                        mockReader,
//                        "hash",
//                        arrayOf("string.xml", "plural.xml"))
        // When
        // TODO: update
    }
}