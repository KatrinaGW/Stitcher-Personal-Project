package com.example.stitcher.controllers;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.CompletableFuture;

public class Database {
    FirebaseFirestore db;

    public Database(){
        db = FirebaseFirestore.getInstance();
    }

    public CompletableFuture<Boolean> test_function(){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            db.collection(Constants.COUNTER_COLLECTION.getValue())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId());
                                }
                                cf.complete(true);
                            } else {
                                Log.w(TAG, "Error", task.getException());
                                cf.complete(false);
                            }
                        }
                    });
        });

        return cf;
    }
}
