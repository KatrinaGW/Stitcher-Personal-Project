package com.example.stitcher.models;

public abstract class DatabaseObject {
    protected String id;

    public DatabaseObject(String id){
        this.id = id;
    }
}
