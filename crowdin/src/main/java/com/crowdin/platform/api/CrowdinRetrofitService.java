package com.crowdin.platform.api;

import android.content.Context;

import com.crowdin.platform.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CrowdinRetrofitService {

    private static final long SIZE_BYTES = 1024L * 1024L * 8L;
    private static final String BASE_URL = "https://crowdin.com/";
    private static CrowdinRetrofitService sInstance;

    private Retrofit retrofit;
    private OkHttpClient okHttpClient;
    private CrowdinApi crowdinApi;

    private CrowdinRetrofitService() {
    }

    public static CrowdinRetrofitService getInstance() {
        if (sInstance == null) {
            sInstance = new CrowdinRetrofitService();
        }
        return sInstance;
    }

    public void init(Context context) {
        Cache cache = new Cache(context.getCacheDir(), SIZE_BYTES);
        okHttpClient = getHttpClient(cache);
        retrofit = getCrowdinRetrofit(okHttpClient);
    }

    private Retrofit getCrowdinRetrofit(OkHttpClient okHttpClient) {
        Gson gson = new GsonBuilder().serializeNulls().create();

        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    private static OkHttpClient getHttpClient(Cache cache) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cache(cache);

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }
        return builder.build();
    }

    public CrowdinApi getCrowdinApi() {
        return crowdinApi == null ? retrofit.create(CrowdinApi.class) : crowdinApi;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
}
