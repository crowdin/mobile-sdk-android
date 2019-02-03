package com.crowdin.platform.repository.remote;

import android.util.Log;

import com.crowdin.platform.api.CrowdinApi;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RemoteStringRepository {

    private static final String TAG = RemoteStringRepository.class.getSimpleName();
    private final CrowdinApi crowdinApi;

    public RemoteStringRepository(CrowdinApi crowdinApi) {
        this.crowdinApi = crowdinApi;
    }

    public void checkUpdates() {
        crowdinApi.getValue().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: " + response.raw());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
            }
        });

    }
}
