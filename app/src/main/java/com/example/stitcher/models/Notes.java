package com.example.stitcher.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Notes extends DatabaseObject implements Parcelable {
    private String note;
    private String title;

    public Notes(String id, String title, String note){
        super(id);
        this.note = note;
        this.title = title;
    }

    public String getId(){
        return id;
    }

    public String getNotes(){
        return note;
    }

    public String getTitle(){return title;}

    protected Notes(Parcel in) {
        super(in);
        note = in.readString();
        title = in.readString();
    }

    public void setBody(String newBody){
        this.note=newBody;
    }

    public void setTitle(String newTitle){
        this.title = newTitle;
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
        dest.writeString(note);
        dest.writeString(title);
    }
}
