package com.example.stitcher.controllers;

public enum Constants {
    COUNTER_COLLECTION("Counters"),
    COUNTER_COUNT_FIELD("count"),
    COUNTER_GOAL_FIELD("goal"),
    COUNTER_NAME_FIELD("name");

    private String value;

    Constants(String valueName) {
        this.value = valueName;
    }

    public String getValue() {
        return value;
    }
}
