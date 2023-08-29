package com.crowdin.platform

import com.crowdin.platform.data.model.ManifestData
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal fun givenManifestData(): ManifestData =
    Gson().fromJson(
        "{\"files\":[\"\\/strings.xml\"],\"languages\":[\"en\", \"ar-EG\",\"de\",\"id\",\"es-ES\",\"uk\"]," +
                "\"language_mapping\":[],\"custom_languages\":[],\"timestamp\":1693294857," +
                "\"content\":{\"ar-EG\":[\"\\/content\\/ar-EG\\/strings.xml\"]," +
                "\"en\":[\"\\/content\\/en\\/strings.xml\"]," +
                "\"de\":[\"\\/content\\/de\\/strings.xml\"]," +
                "\"id\":[\"\\/content\\/id\\/strings.xml\"]," +
                "\"es-ES\":[\"\\/content\\/es-ES\\/strings.xml\"]," +
                "\"uk\":[\"\\/content\\/uk\\/strings.xml\"]}," +
                "\"mapping\":[\"\\/mapping\\/en\\/strings.xml\"]}",
        ManifestData::class.java
    )

internal fun givenMockMappingFileResponse(
    mockDistributionApi: CrowdinDistributionApi,
    success: Boolean = true,
    successCode: Int = 200
) {
    @Suppress("UNCHECKED_CAST")
    val mockedCall = mock(Call::class.java) as Call<ResponseBody>
    `when`(mockDistributionApi.getMappingFile(any(), any(), any())).thenReturn(mockedCall)

    val response = if (success) {
        Response.success<ResponseBody>(successCode, StubResponseBody())
    } else {
        Response.error(403, StubResponseBody())
    }
    `when`(mockedCall.execute()).thenReturn(response)
}

internal fun givenMockManifestResponse(
    mockDistributionApi: CrowdinDistributionApi,
    success: Boolean = true,
    successCode: Int = 200
) {
    @Suppress("UNCHECKED_CAST")
    val mockedCall = mock(Call::class.java) as Call<ManifestData>
    `when`(mockDistributionApi.getResourceManifest(any())).thenReturn(mockedCall)
    val response = if (success) {
        Response.success<ManifestData>(
            successCode,
            ManifestData(
                listOf("strings.xml"),
                123124154L,
                listOf(),
                hashMapOf(),
                hashMapOf(),
                hashMapOf(
                    "en" to listOf("/content/en/string.xml"),
                    "de" to listOf("/content/de/string.xml")
                ),
                listOf("/mapping/en/string.xml")
            )
        )
    } else {
        Response.error(403, StubResponseBody())
    }

    doAnswer {
        @Suppress("UNCHECKED_CAST")
        val callback = it.getArgument(0, Callback::class.java) as Callback<ManifestData>
        callback.onResponse(mockedCall, response)
    }.`when`(mockedCall).enqueue(any())
}
