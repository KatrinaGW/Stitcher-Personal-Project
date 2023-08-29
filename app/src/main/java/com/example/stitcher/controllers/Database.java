package com.example.stitcher.controllers;

import com.example.stitcher.models.DatabaseObject;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public interface Database {
    public abstract CompletableFuture<ArrayList<? extends DatabaseObject>> getAll();
    public abstract CompletableFuture<Boolean> updateRecord(String id, DatabaseObject obj);
    public abstract CompletableFuture<Boolean> insertRecord(String id, DatabaseObject obj);
    public abstract CompletableFuture<Boolean> deleteRecord(String id);
}

/**
 * TODO:
 * Make static references for all of the collections
 */
