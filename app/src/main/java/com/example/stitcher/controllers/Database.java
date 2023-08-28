package com.example.stitcher.controllers;

import com.example.stitcher.models.DatabaseObject;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public interface Database {
    public abstract CompletableFuture<ArrayList<? extends DatabaseObject>> getAll();
}
