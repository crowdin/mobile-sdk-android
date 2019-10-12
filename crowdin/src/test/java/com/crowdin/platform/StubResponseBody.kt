package com.crowdin.platform

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.BufferedSource
import org.mockito.Mockito

class StubResponseBody : ResponseBody() {
    override fun contentLength(): Long {
        return 100
    }

    override fun contentType(): MediaType? {
        return null
    }

    override fun source(): BufferedSource {
        return Mockito.mock(BufferedSource::class.java)
    }
}