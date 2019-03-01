package com.crowdin.platform.api;

import java.util.Map;

public class PluralData {

    private String name;
    private Map<String, String> quantity;

    public String getName() {
        return name;
    }

    public Map<String, String> getQuantity() {
        return quantity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(Map<String, String> quantity) {
        this.quantity = quantity;
    }
}
