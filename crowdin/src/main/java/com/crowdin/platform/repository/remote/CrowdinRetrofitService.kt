package com.crowdin.platform.repository.remote

import android.content.Context
import com.crowdin.platform.BuildConfig
import com.crowdin.platform.repository.remote.api.CrowdinApi
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

internal class CrowdinRetrofitService private constructor() {

    private lateinit var retrofit: Retrofit
    private lateinit var okHttpClient: OkHttpClient

    private val crowdinApi: CrowdinApi? = null

    fun init(context: Context) {
        val cache = Cache(context.cacheDir, SIZE_BYTES)
        okHttpClient = getHttpClient(cache)
        retrofit = getCrowdinRetrofit(okHttpClient)
    }

    private fun getHttpClient(cache: Cache): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.cache(cache)

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }
        return builder.build()
    }

    private fun getCrowdinRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .build()
    }

    fun getCrowdinApi(): CrowdinApi {
        return crowdinApi ?: retrofit.create(CrowdinApi::class.java)
    }

    companion object {

        private const val SIZE_BYTES = 1024L * 1024L * 8L
        private const val BASE_URL = "https://crowdin-distribution.s3.us-east-1.amazonaws.com/"
        private var sInstance: CrowdinRetrofitService? = null

        val instance: CrowdinRetrofitService
            get() {
                if (sInstance == null) {
                    sInstance = CrowdinRetrofitService()
                }
                return sInstance as CrowdinRetrofitService
            }
    }
}
