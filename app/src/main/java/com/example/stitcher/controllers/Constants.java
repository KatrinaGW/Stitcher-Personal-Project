package com.example.stitcher.controllers;

public enum Constants {
    COUNTER_COLLECTION("Counters");

    private String value;

    Constants(String valueName) {
        this.value = valueName;
    }

    public String getValue() {
        return value;
    }
}
