package com.crowdin.platform.data.remote

import com.crowdin.platform.BuildConfig
import com.crowdin.platform.data.remote.api.CrowdinApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

internal class CrowdinRetrofitService private constructor() {

    private lateinit var retrofit: Retrofit
    private lateinit var okHttpClient: OkHttpClient

    private val crowdinApi: CrowdinApi? = null

    fun init() {
        okHttpClient = getHttpClient()
        retrofit = getCrowdinRetrofit(okHttpClient)
    }

    private fun getHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()

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
