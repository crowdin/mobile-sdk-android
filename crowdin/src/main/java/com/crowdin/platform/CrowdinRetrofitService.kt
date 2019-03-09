package com.crowdin.platform

import android.content.Context
import com.crowdin.platform.api.CrowdinApi
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class CrowdinRetrofitService private constructor() {

    lateinit var retrofit: Retrofit
    lateinit var okHttpClient: OkHttpClient

    private val crowdinApi: CrowdinApi? = null

    fun init(context: Context) {
        val cache = Cache(context.cacheDir, SIZE_BYTES)
        okHttpClient = getHttpClient(cache)
        retrofit = getCrowdinRetrofit(okHttpClient)
    }

    private fun getCrowdinRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val gson = GsonBuilder().serializeNulls().create()

        return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
    }

    fun getCrowdinApi(): CrowdinApi {
        return crowdinApi ?: retrofit.create(CrowdinApi::class.java)
    }

    companion object {

        private val SIZE_BYTES = 1024L * 1024L * 8L
        private val BASE_URL = "https://crowdin.com/"
        private var sInstance: CrowdinRetrofitService? = null

        val instance: CrowdinRetrofitService
            get() {
                if (sInstance == null) {
                    sInstance = CrowdinRetrofitService()
                }
                return sInstance as CrowdinRetrofitService
            }

        private fun getHttpClient(cache: Cache): OkHttpClient {
            val builder = OkHttpClient.Builder()
            builder.cache(cache)

            if (BuildConfig.DEBUG) {
                builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            }
            return builder.build()
        }
    }
}
