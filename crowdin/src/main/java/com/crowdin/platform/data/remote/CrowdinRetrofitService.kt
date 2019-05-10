package com.crowdin.platform.data.remote

import com.crowdin.platform.BuildConfig
import com.crowdin.platform.data.remote.api.CrowdinApi
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

internal class CrowdinRetrofitService private constructor() {

    private lateinit var okHttpClient: OkHttpClient
    private val crowdinDistributionApi: CrowdinDistributionApi? = null
    private val crowdinApi: CrowdinApi? = null

    fun init() {
        okHttpClient = getHttpClient()
    }

    private fun getHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }
        return builder.build()
    }

    private fun getCrowdinRetrofit(okHttpClient: OkHttpClient, url: String): Retrofit {
        return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(url)
                .build()
    }

    fun getCrowdinDistributionApi(): CrowdinDistributionApi =
            crowdinDistributionApi
                    ?: getCrowdinRetrofit(okHttpClient, BASE_DISTRIBUTION_URL).create(CrowdinDistributionApi::class.java)

    fun getCrowdinApi(): CrowdinApi = crowdinApi
            ?: getCrowdinRetrofit(okHttpClient, BASE_API_URL).create(CrowdinApi::class.java)

    companion object {

        private const val BASE_DISTRIBUTION_URL = "https://crowdin-distribution.s3.us-east-1.amazonaws.com/"
        private const val BASE_API_URL = "https://crowdin.com/api/v2/"

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
