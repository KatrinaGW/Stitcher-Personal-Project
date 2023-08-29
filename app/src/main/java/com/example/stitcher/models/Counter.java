package com.example.stitcher.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Formatter;

public class Counter extends DatabaseObject implements Parcelable {
    private int count;
    private int goal;
    private String name;

    public Counter(String id, int count, int goal, String name){
        super(id);
        this.count = count;
        this.goal = goal;
        this.name = name;
    }

    protected Counter(Parcel in) {
        super(in);
        count = in.readInt();
        goal = in.readInt();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(count);
        dest.writeInt(goal);
        dest.writeString(name);
    }

    public static final Creator<Counter> CREATOR = new Creator<Counter>() {
        @Override
        public Counter createFromParcel(Parcel in) {
            return new Counter(in);
        }

        @Override
        public Counter[] newArray(int size) {
            return new Counter[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public String getName() {
        return name;
    }

    public void addToCount(int addend){
        this.count += addend;
    }

    public void setName(String name) {
        this.name = name;
    }
}
