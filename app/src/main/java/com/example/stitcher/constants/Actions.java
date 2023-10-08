package com.example.stitcher.constants;

public enum Actions {
    NO_ACTION("no_action"),
    UPDATING("updating"),
    DELETING("deleting"),
    ADDING("adding");

    private String value;

    Actions(String valueName) {
        this.value = valueName;
    }

    public String getValue() {
        return value;
    }
}
