package com.crowdin.platform.utils;

import android.content.Context;
import android.support.annotation.Nullable;

import com.crowdin.platform.api.ResourcesResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    private FileUtils() {
    }

    @Nullable
    public static ResourcesResponse getJsonFromApi(@Nullable Context context) {
        if (context == null) return null;

        String json;
        ResourcesResponse data = null;
        try {
            InputStream inputStream = context.getAssets().open("crowdin_api.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
            Gson gson = new Gson();
            return gson.fromJson(json, ResourcesResponse.class);

        } catch (IOException e) {
            return data;
        }
    }

}
