package com.example.stitcher;

import java.util.ArrayList;

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

    public static ArrayList<String> getAllValues() {
        ArrayList<String> valuesList = new ArrayList<>();
        for (Statuses status : Statuses.values()) {
            valuesList.add(status.getValue());
        }
        return valuesList;
    }
}