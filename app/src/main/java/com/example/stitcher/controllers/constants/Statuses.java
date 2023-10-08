package com.example.stitcher.controllers.constants;

public enum Statuses {
    IN_PROGRESS("In Progress"),
    ON_HOLD("On Hold"),
    COMPLETED("Completed"),
    ARCHIVED("Archived"),
    QUEUED("Queued");

    private String value;

    Statuses(String valueName) {
        this.value = valueName;
    }

    public String getValue() {
        return value;
    }
}
