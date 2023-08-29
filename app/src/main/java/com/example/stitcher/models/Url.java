package com.example.stitcher.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Url extends DatabaseObject implements Parcelable {
    private String url;

    public Url(String id, String url){
        super(id);
        this.url = url;
    }

    public String getId(){
        return id;
    }

    public String getUrl(){
        return url;
    }

    protected Url(Parcel in) {
        super(in);
        url = in.readString();
    }

    public static final Creator<Url> CREATOR = new Creator<Url>() {
        @Override
        public Url createFromParcel(Parcel in) {
            return new Url(in);
        }

        @Override
        public Url[] newArray(int size) {
            return new Url[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(url);
    }
}
