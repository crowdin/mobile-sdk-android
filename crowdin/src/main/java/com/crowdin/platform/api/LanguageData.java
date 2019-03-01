package com.crowdin.platform.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class LanguageData {

    private String language;
    private int version;
    @SerializedName("app_version")
    private int appVersion;
    private Map<String, String> resources;
    private List<ArrayData> arrays;
    private List<PluralData> plurals;

    public LanguageData(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public int getVersion() {
        return version;
    }

    public int getAppVersion() {
        return appVersion;
    }

    public Map<String, String> getResources() {
        return resources;
    }

    public List<ArrayData> getArrays() {
        return arrays;
    }

    public List<PluralData> getPlurals() {
        return plurals;
    }

    public void setResources(Map<String, String> resources) {
        this.resources = resources;
    }

    public void setArrays(List<ArrayData> arrays) {
        this.arrays = arrays;
    }

    public void setPlurals(List<PluralData> plurals) {
        this.plurals = plurals;
    }
}
