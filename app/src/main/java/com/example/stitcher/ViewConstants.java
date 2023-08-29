package com.example.stitcher;

public enum ViewConstants {
    SELECTED_PROJECT("selectedProject"),
    SELECTED_COUNTER("selectedCounter"),
    SELECTED_URL("selectedUrl"),
    PARENT_PROJECT("parentProject");

    private String value;

    ViewConstants(String valueName) {
        this.value = valueName;
    }

    public String getValue() {
        return value;
    }
}
