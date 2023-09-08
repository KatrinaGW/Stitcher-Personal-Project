package com.example.stitcher.controllers;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.stitcher.models.Counter;
import com.example.stitcher.models.DatabaseObject;
import com.example.stitcher.models.Url;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CounterCollection implements Database{
    private FirebaseFirestore db;
    private CollectionReference collection;
    private static CounterCollection INSTANCE;

    private CounterCollection(){
        db = FirebaseFirestore.getInstance();
        collection = db.collection(Constants.COUNTER_COLLECTION.getValue());
    }

    public static CounterCollection getInstance(){
        if(INSTANCE == null){
            INSTANCE = new CounterCollection();
        }

        return INSTANCE;
    }

    private Counter documentSnapshotToCounter(DocumentSnapshot document){
        int count = ((Long) (document.getData().get(Constants.COUNTER_COUNT_FIELD.getValue()))).intValue();
        int countGoal = ((Long) (document.getData().get(Constants.COUNTER_GOAL_FIELD.getValue()))).intValue();
        String name = (String) document.getData().get(Constants.COUNTER_NAME_FIELD.getValue());

        Counter newCounter = new Counter(document.getId(), count, countGoal, name);

        return newCounter;
    }

    @Override
    public CompletableFuture<Boolean> updateRecord(String id, DatabaseObject obj){
        Counter counter = (Counter) obj;
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        ArrayList<String> errors = new ArrayList<>();

        DocumentReference documentReference = collection.document(counter.getId());

        CompletableFuture futureCount = CompletableFuture.supplyAsync(()->
                documentReference.update(Constants.COUNTER_COUNT_FIELD.getValue(), counter.getCount())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                errors.add(e.getMessage());
                            }
                        })
        );

        CompletableFuture futureGoal = CompletableFuture.supplyAsync(()->
                documentReference.update(Constants.COUNTER_GOAL_FIELD.getValue(), counter.getGoal())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                errors.add(e.getMessage());
                            }
                        })
        );

        CompletableFuture futureName = CompletableFuture.supplyAsync(()->
                documentReference.update(Constants.COUNTER_NAME_FIELD.getValue(), counter.getName())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                errors.add(e.getMessage());
                            }
                        })
        );

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futureCount, futureGoal, futureName);

        allFutures.thenRun(() -> {
            cf.complete(errors.size()==0);
        });

        return cf;
    }

    @Override
    public CompletableFuture<ArrayList<? extends DatabaseObject>> getAll(){
        ArrayList<Counter> counters = new ArrayList<Counter>();
        CompletableFuture<ArrayList<? extends DatabaseObject>> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            collection.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId());

                                    Counter newCounter = documentSnapshotToCounter(document);

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

    @Override
    public CompletableFuture<Boolean> insertRecord(String id, DatabaseObject obj){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        Counter counter = (Counter) obj;

        Map<String, Object> counterMap = new HashMap<>();
        counterMap.put("id", counter.getId());
        counterMap.put(Constants.COUNTER_COUNT_FIELD.getValue(), counter.getCount());
        counterMap.put(Constants.COUNTER_GOAL_FIELD.getValue(), counter.getGoal());
        counterMap.put(Constants.COUNTER_NAME_FIELD.getValue(), counter.getName());

        collection.document(counter.getId())
                .set(counterMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        cf.complete(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        cf.completeExceptionally(e);
                    }
                });
        return cf;
    }

    public CompletableFuture<ArrayList<Counter>> getCountersWithIds(ArrayList<String> ids){
        CompletableFuture<ArrayList<Counter>> cf = new CompletableFuture<>();
        ArrayList<Counter> counters = new ArrayList<>();

        ArrayList<String> searchIds = new ArrayList<>();

        for(String id : ids){
            searchIds.add(id.trim());
        }

        if(searchIds.size() == 0){
            cf.complete(counters);
        }

        CompletableFuture.runAsync(() -> {
            collection.whereIn(FieldPath.documentId(),searchIds)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Log.d(TAG, "Turning document into Counter");
                            Counter newCounter = documentSnapshotToCounter(document);

                            counters.add(newCounter);
                        }
                        cf.complete(counters);
                    })
                    .addOnFailureListener(e -> cf.completeExceptionally(e));
        });


        return cf;
    }

    public CompletableFuture<Boolean> deleteRecord(String id){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        collection.document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Document successfully deleted");
                        cf.complete(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Document failed to delete");
                        Log.e(TAG, e.getMessage());
                        cf.completeExceptionally(e);
                    }
                });

        return cf;
    }
}
