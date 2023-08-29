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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class UrlCollection implements Database{
    CollectionReference collection;
    FirebaseFirestore db;

    public UrlCollection(){
        db = FirebaseFirestore.getInstance();
        collection = db.collection(Constants.URLS_COLLECTION.getValue());
    }

    private Url documentSnapshotToUrl(DocumentSnapshot doc){
        String url = (String) doc.getData().get(Constants.URLS_FIELD.getValue());
        String id = doc.getId();

        Url newURL = new Url(id, url);

        return newURL;
    }

    @Override
    public CompletableFuture<ArrayList<? extends DatabaseObject>> getAll() {
        ArrayList<Url> urls = new ArrayList<Url>();
        CompletableFuture<ArrayList<? extends DatabaseObject>> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            collection.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId());
                                    Url newURL = documentSnapshotToUrl(document);
                                    urls.add(newURL);
                                }
                                cf.complete(urls);
                            } else {
                                Log.w(TAG, "Error", task.getException());
                                cf.complete(urls);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "ERROR", e);
                            cf.completeExceptionally(e);
                        }
                    });
        });

        return cf;
    }

    public CompletableFuture<ArrayList<Url>> getUrlsWithIds(ArrayList<String> ids) {
        CompletableFuture<ArrayList<Url>> cf = new CompletableFuture<>();
        ArrayList<Url> urls = new ArrayList<>();

        ArrayList<String> searchIds = new ArrayList<>();

        for(String id : ids){
            searchIds.add(id.trim());
        }

        CompletableFuture.runAsync(() -> {
            collection.whereIn(FieldPath.documentId(),searchIds)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Log.d(TAG, "Turning document into Url");
                            Url newUrl = documentSnapshotToUrl(document);

                            urls.add(newUrl);
                        }
                        cf.complete(urls);
                    })
                    .addOnFailureListener(e -> cf.completeExceptionally(e));
        });


        return cf;
    }

    @Override
    public CompletableFuture<Boolean> updateRecord(String id, DatabaseObject obj) {
        Url url = (Url) obj;
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        DocumentReference documentReference = collection.document(url.getId());

        CompletableFuture.runAsync(() ->
                documentReference.update(Constants.URLS_FIELD.getValue(), url.getUrl())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        cf.complete(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "ERROR", e);
                        cf.completeExceptionally(e);
                    }
                }));

        return cf;
    }

    @Override
    public CompletableFuture<Boolean> insertRecord(String id, DatabaseObject obj) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        Url url = (Url) obj;

        Map<String, Object> urlMap = new HashMap<>();
        urlMap.put("id", url.getId());
        urlMap.put(Constants.URLS_FIELD.getValue(), url.getUrl());

        collection.document(url.getId())
                .set(urlMap)
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

    @Override
    public CompletableFuture<Boolean> deleteRecord(String id) {
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
