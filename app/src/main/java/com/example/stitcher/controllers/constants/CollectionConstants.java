package com.example.stitcher.controllers.constants;

public enum CollectionConstants {
    PROJECT_STATUS_FIELD("status"),
    PROJECT_COLLECTION("Projects"),
    PROJECT_URLS_FIELD("urlIds"),
    PROJECT_COUNTERS_FIELD("counterIds"),
    PROJECT_NAME_FIELD("name"),
    URLS_COLLECTION("Urls"),
    URLS_FIELD("url"),
    COUNTER_COLLECTION("Counters"),
    COUNTER_COUNT_FIELD("count"),
    COUNTER_GOAL_FIELD("goal"),
    COUNTER_NAME_FIELD("name");

    private String value;

    CollectionConstants(String valueName) {
        this.value = valueName;
    }

    public String getValue() {
        return value;
    }
}
