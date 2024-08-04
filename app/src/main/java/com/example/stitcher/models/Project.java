package com.example.stitcher.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Project extends DatabaseObject implements Parcelable {
    private ArrayList<String> counterIds;
    private ArrayList<String> urlIds;
    private ArrayList<String> notesIds;
    private String name;
    private String status;

    public Project(String id, String name, String status){
        super(id);
        counterIds = new ArrayList<>();
        urlIds = new ArrayList<>();
        notesIds = new ArrayList<>();
        this.name = name;
        this.status = status;
    }

    public Project(String id, ArrayList<String> counterIds, ArrayList<String> urlIds, ArrayList<String> notesIds, String name,
                   String status){
        this(id, name, status);
        this.counterIds = counterIds;
        this.urlIds = urlIds;
        this.notesIds = notesIds;
    }

    protected Project(Parcel in) {
        super(in);
        this.counterIds = in.readArrayList(null);
        this.urlIds = in.readArrayList(null);
        this.name = in.readString();
        this.status = in.readString();
        this.notesIds = in.readArrayList(null);
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

    public void addNoteId(String id){notesIds.add(id);}

    public String getId(){
        return id;
    }

    public ArrayList<String> getCounterIds(){
        return counterIds;
    }

    public ArrayList<String> getUrlIds(){
        return urlIds;
    }

    public ArrayList<String> getNotesIds(){return notesIds;}

    public void removeUrl(String urlId){
        this.urlIds.remove(urlId);
    }

    public void removeNote(String noteId){
        this.notesIds.remove(noteId);
    }

    public void removeCounter(String counterId){
        this.counterIds.remove(counterId);
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
        dest.writeString(status);
        dest.writeList(notesIds);
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String newStatus){
        this.status = newStatus;
    }
}
