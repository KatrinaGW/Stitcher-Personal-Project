package com.example.stitcher.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Notes extends DatabaseObject implements Parcelable {
    private String notes;

    public Notes(String id, String notes){
        super(id);
        this.notes = notes;
    }

    public String getId(){
        return id;
    }

    public String getNotes(){
        return notes;
    }

    protected Notes(Parcel in) {
        super(in);
        notes = in.readString();
    }

    public static final Creator<Notes> CREATOR = new Creator<Notes>() {
        @Override
        public Notes createFromParcel(Parcel in) {
            return new Notes(in);
        }

        @Override
        public Notes[] newArray(int size) {
            return new Notes[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(notes);
    }
}
