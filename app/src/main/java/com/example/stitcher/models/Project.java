package com.example.stitcher.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Project extends DatabaseObject implements Parcelable {
    private ArrayList<String> counterIds;
    private ArrayList<String> urlIds;
    private String name;

    public Project(String id, String name){
        super(id);
        counterIds = new ArrayList<>();
        urlIds = new ArrayList<>();
        this.name = name;
    }

    public Project(String id, ArrayList<String> counterIds, ArrayList<String> urlIds, String name){
        super(id);
        this.counterIds = counterIds;
        this.urlIds = urlIds;
        this.name = name;
    }

    protected Project(Parcel in) {
        super(in);
        this.counterIds = in.readArrayList(null);
        this.urlIds = in.readArrayList(null);
        this.name = in.readString();
    }

    public String getName(){
        return name;
    }

    public void setName(String newName){
        this.name = newName;
    }

    public void addCounterId(String id){
        counterIds.add(id);
    }

    public void addUrlId(String id){
        urlIds.add(id);
    }

    public String getId(){
        return id;
    }

    public ArrayList<String> getCounterIds(){
        return counterIds;
    }

    public ArrayList<String> getUrlIds(){
        return urlIds;
    }

    public void removeUrl(String urlId){
        this.urlIds.remove(urlId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeList(counterIds);
        dest.writeList(urlIds);
        dest.writeString(name);
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };
}
