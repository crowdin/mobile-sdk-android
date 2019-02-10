package com.crowdin.platform.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResourcesResponse {

    @SerializedName("data")
    private List<LanguageData> dataList;

    public List<LanguageData> getDataList() {
        return dataList;
    }
}