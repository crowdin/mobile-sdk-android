package com.crowdin.platform.data.remote

import com.crowdin.platform.BuildConfig
import com.crowdin.platform.Session
import com.crowdin.platform.SessionImpl
import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.remote.api.AuthApi
import com.crowdin.platform.data.remote.api.CrowdinApi
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import com.crowdin.platform.data.remote.api.CrowdinTranslationApi
import com.crowdin.platform.data.remote.interceptor.HeaderInterceptor
import com.crowdin.platform.data.remote.interceptor.SessionInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal object CrowdinRetrofitService {
    private const val BASE_DISTRIBUTION_URL = "https://distributions.crowdin.net/"
    private const val AUTH_API_URL = "https://accounts.crowdin.com/"
    private const val BASE_API_URL = "https://api.crowdin.com/"

    private var okHttpClient: OkHttpClient? = null
    private var interceptableOkHttpClient: OkHttpClient? = null

    private val crowdinDistributionApi: CrowdinDistributionApi? = null
    private val crowdinApi: CrowdinApi? = null
    private val authApi: AuthApi? = null
    private val crowdinTranslationApi: CrowdinTranslationApi? = null

    private fun getHttpClient(): OkHttpClient =
        if (okHttpClient == null) {
            val builder = OkHttpClient.Builder()
            builder.addInterceptor(HeaderInterceptor())
            if (BuildConfig.DEBUG) {
                builder.addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    },
                )
            }
            okHttpClient = builder.build()
            okHttpClient!!
        } else {
            okHttpClient!!
        }

    private fun getInterceptableHttpClient(session: Session): OkHttpClient =
        if (interceptableOkHttpClient == null) {
            val builder = OkHttpClient.Builder()
            builder.addInterceptor(SessionInterceptor(session))
            builder.addInterceptor(HeaderInterceptor())
            if (BuildConfig.DEBUG) {
                builder.addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    },
                )
            }

            interceptableOkHttpClient = builder.build()
            interceptableOkHttpClient!!
        } else {
            interceptableOkHttpClient!!
        }

    private fun getCrowdinRetrofit(
        okHttpClient: OkHttpClient,
        url: String,
    ): Retrofit =
        Retrofit
            .Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(url)
            .build()

    fun getCrowdinDistributionApi(): CrowdinDistributionApi =
        crowdinDistributionApi
            ?: getCrowdinRetrofit(getHttpClient(), BASE_DISTRIBUTION_URL).create(
                CrowdinDistributionApi::class.java,
            )

    fun getCrowdinApi(
        dataManager: DataManager,
        organizationName: String?,
    ): CrowdinApi {
        var baseUrl = BASE_API_URL
        organizationName?.let { baseUrl = "https://$organizationName.api.crowdin.com/" }
        return crowdinApi
            ?: getCrowdinRetrofit(
                getInterceptableHttpClient(SessionImpl(dataManager, getCrowdinAuthApi())),
                baseUrl,
            ).create(CrowdinApi::class.java)
    }

    fun getCrowdinAuthApi(): AuthApi =
        authApi
            ?: getCrowdinRetrofit(getHttpClient(), AUTH_API_URL).create(AuthApi::class.java)

    fun getCrowdinTranslationApi(): CrowdinTranslationApi =
        crowdinTranslationApi
            ?: getCrowdinRetrofit(
                getHttpClient(),
                BASE_API_URL,
            ).create(CrowdinTranslationApi::class.java)
}
