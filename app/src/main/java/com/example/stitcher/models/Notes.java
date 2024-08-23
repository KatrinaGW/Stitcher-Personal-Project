package com.example.stitcher.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Notes extends DatabaseObject implements Parcelable {
    private String note;
    private String title;

    public Notes(String id, String title, String note){
        super(id);
        this.note = note;
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if(o==this){
            return true;
        }

        if(!(o instanceof Notes)){
            return false;
        }

        Notes otherNote = (Notes) o;

        return Objects.equals(otherNote.getId(), this.id);
    }

    @Override
    public final int hashCode() {
        byte[] bytes = this.id.getBytes(StandardCharsets.US_ASCII);
        int total = 0;

        for(byte b : bytes){
            total+=(int) b;
        }

        return total;
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
