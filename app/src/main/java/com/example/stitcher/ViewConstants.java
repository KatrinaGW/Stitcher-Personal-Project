package com.example.stitcher;

public enum ViewConstants {
    FRAGMENT_PROJECT_COUNTERS("project_counters"),
    FRAGMENT_ERROR_MSG("error_msg"),
    FRAGMENT_EXISTING_STRING("fragment_text_value"),
    FRAGMENT_HINT_MSG("hint_msg"),
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
