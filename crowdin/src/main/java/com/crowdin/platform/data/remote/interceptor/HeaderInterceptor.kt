package com.crowdin.platform.data.remote.interceptor

import android.os.Build
import com.crowdin.platform.BuildConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class HeaderInterceptor : Interceptor {

    private val userAgent =
        "crowdin-android-sdk/${BuildConfig.VERSION_NAME} android/${Build.VERSION.SDK_INT}"

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = addHeadersToRequest(chain.request())

        return chain.proceed(request)
    }

    private fun addHeadersToRequest(original: Request): Request {
        val requestBuilder = original.newBuilder()
            .header("User-Agent", userAgent)
            .method(original.method, original.body)

        return requestBuilder.build()
    }
}
