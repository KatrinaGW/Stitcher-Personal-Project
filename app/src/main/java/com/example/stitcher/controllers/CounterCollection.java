package com.example.stitcher.controllers;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.stitcher.models.Counter;
import com.example.stitcher.models.DatabaseObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class CounterCollection implements Database{
    FirebaseFirestore db;

    public CounterCollection(){
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public CompletableFuture<ArrayList<? extends DatabaseObject>> getAll(){
        ArrayList<Counter> counters = new ArrayList<Counter>();
        CompletableFuture<ArrayList<? extends DatabaseObject>> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            db.collection(Constants.COUNTER_COLLECTION.getValue())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId());
                                    int count = ((Long) (document.getData().get(Constants.COUNTER_COUNT_FIELD.getValue()))).intValue();
                                    int countGoal = ((Long) (document.getData().get(Constants.COUNTER_GOAL_FIELD.getValue()))).intValue();
                                    String name = (String) document.getData().get(Constants.COUNTER_NAME_FIELD.getValue());

                                    Counter newCounter = new Counter(document.getId(), count, countGoal, name);
                                    counters.add(newCounter);
                                }
                                cf.complete(counters);
                            } else {
                                Log.w(TAG, "Error", task.getException());
                                cf.complete(counters);
                            }
                        }
                    });
        });

        return cf;
    }
}
