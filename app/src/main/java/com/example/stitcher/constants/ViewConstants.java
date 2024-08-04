package com.example.stitcher.constants;

public enum ViewConstants {
    FRAGMENT_CONFIRM_LABEL("confirm_label"),
    FRAGMENT_CANCEL_LABEL("cancel_label"),
    FRAGMENT_HEADER("header_label"),
    BACK_NAVIGATE_URL("back_navigate_url"),
    FRAGMENT_PROJECT_COUNTERS("project_counters"),
    FRAGMENT_ERROR_MSG("error_msg"),
    FRAGMENT_EXISTING_STRING("fragment_text_value"),
    FRAGMENT_HINT_MSG("hint_msg"),
    SELECTED_PROJECT("selectedProject"),
    SELECTED_COUNTER("selectedCounter"),
    SELECTED_URL("selectedUrl"),
    PARENT_PROJECT("parentProject"),
    NOTES_FIELD("noteTitlews");

    private String value;

    ViewConstants(String valueName) {
        this.value = valueName;
    }

    public String getValue() {
        return value;
    }
}
