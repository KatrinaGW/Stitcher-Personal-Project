package com.example.stitcher;

import android.graphics.Color;

import com.example.stitcher.models.Counter;

public class HelperFunctions {

    public static int getCounterColourCode(Counter counter){
        int colourCode = Color.BLACK;

        if(counter.getCount() == counter.getGoal()){
            colourCode = Color.GREEN;
        }

        if(counter.getCount() > counter.getGoal()){
            colourCode = Color.RED;
        }

        return colourCode;
    }
}
