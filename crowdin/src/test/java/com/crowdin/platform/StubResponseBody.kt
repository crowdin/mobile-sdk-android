package com.crowdin.platform

import java.io.InputStream
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.BufferedSource
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class StubResponseBody : ResponseBody() {
    override fun contentLength(): Long {
        return 100
    }

    override fun contentType(): MediaType? {
        return null
    }

    override fun source(): BufferedSource {
        val bufferedSource = mock(BufferedSource::class.java)
        `when`(bufferedSource.inputStream()).thenReturn(mock(InputStream::class.java))
        return bufferedSource
    }
}
