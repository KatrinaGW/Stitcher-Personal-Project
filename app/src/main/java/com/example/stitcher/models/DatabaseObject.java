package com.example.stitcher.models;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class DatabaseObject implements Parcelable {
    protected String id;

    public DatabaseObject(String id){
        this.id = id;
    }
    protected DatabaseObject(Parcel in){
        id = in.readString();
    }
}
