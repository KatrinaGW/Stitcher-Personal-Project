package com.example.stitcher.models;

import java.util.Formatter;

public class Counter extends DatabaseObject{
    private int count;
    private int goal;
    private String name;

    public Counter(String id, int count, int goal, String name){
        super(id);
        this.count = count;
        this.goal = goal;
        this.name = name;
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

    public void setName(String name) {
        this.name = name;
    }
}
