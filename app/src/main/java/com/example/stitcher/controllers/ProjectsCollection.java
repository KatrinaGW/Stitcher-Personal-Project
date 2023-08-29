package com.example.stitcher.controllers;

import static android.content.ContentValues.TAG;

import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.stitcher.models.Counter;
import com.example.stitcher.models.DatabaseObject;
import com.example.stitcher.models.Project;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class ProjectsCollection implements Database{
    private CollectionReference collection;
    private FirebaseFirestore db;

    public ProjectsCollection(){
        db = FirebaseFirestore.getInstance();
        collection = db.collection(Constants.PROJECT_COLLECTION.getValue());
    }

    @Override
    public CompletableFuture<ArrayList<? extends DatabaseObject>> getAll() {
        ArrayList<Project> projects = new ArrayList<Project>();
        CompletableFuture<ArrayList<? extends DatabaseObject>> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            collection.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId());
                                    ArrayList<String> counterIds = (ArrayList<String>) document.getData()
                                                                        .get(Constants.PROJECT_COUNTERS_FIELD.getValue());
                                    ArrayList<String> urlIds = (ArrayList<String>) document.getData()
                                                                        .get(Constants.PROJECT_URLS_FIELD.getValue());
                                    String name = (String) document.getData().get(Constants.PROJECT_NAME_FIELD.getValue());
                                    String id = document.getId();

                                    Project newProject = new Project(id, counterIds, urlIds, name);
                                    projects.add(newProject);
                                }
                                cf.complete(projects);
                            } else {
                                Log.w(TAG, "Error", task.getException());
                                cf.complete(projects);
                            }
                        }
                    });
        });

        return cf;
    }

    @Override
    public CompletableFuture<Boolean> updateRecord(String id, DatabaseObject obj) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> insertRecord(String id, DatabaseObject obj) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> deleteRecord(String id) {
        return null;
    }
}
