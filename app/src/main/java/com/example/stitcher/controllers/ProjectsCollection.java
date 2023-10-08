package com.example.stitcher.controllers;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.stitcher.controllers.constants.CollectionConstants;
import com.example.stitcher.models.DatabaseObject;
import com.example.stitcher.models.Project;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ProjectsCollection implements Collection {
    private CollectionReference collection;
    private FirebaseFirestore db;
    private static ProjectsCollection INSTANCE;

    private ProjectsCollection(){
        db = FirebaseFirestore.getInstance();
        collection = db.collection(CollectionConstants.PROJECT_COLLECTION.getValue());
    }

    public static ProjectsCollection getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ProjectsCollection();
        }

        return INSTANCE;
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
                                                                        .get(CollectionConstants.PROJECT_COUNTERS_FIELD.getValue());
                                    ArrayList<String> urlIds = (ArrayList<String>) document.getData()
                                                                        .get(CollectionConstants.PROJECT_URLS_FIELD.getValue());
                                    String name = (String) document.getData().get(CollectionConstants.PROJECT_NAME_FIELD.getValue());
                                    String status = (String) document.getData().get(CollectionConstants.PROJECT_STATUS_FIELD.getValue());
                                    String id = document.getId();

                                    Project newProject = new Project(id, counterIds, urlIds, name, status);
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

    public CompletableFuture<Boolean> removeCounterid(String projectId, String counterId){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            collection.document(projectId)
                    .update(CollectionConstants.PROJECT_COUNTERS_FIELD.getValue(), FieldValue.arrayRemove(counterId))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            cf.complete(true);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "ERROR", e);
                            cf.completeExceptionally(e);
                        }
                    });
        });

        return cf;
    }

    public CompletableFuture<Boolean> removeUrlId(String projectId, String urlId){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            collection.document(projectId)
                    .update(CollectionConstants.PROJECT_URLS_FIELD.getValue(), FieldValue.arrayRemove(urlId))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            cf.complete(true);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "ERROR", e);
                            cf.completeExceptionally(e);
                        }
                    });
        });

        return cf;
    }

    public CompletableFuture<Boolean> updateCounterIds(String id, String newCounterId){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            collection.document(id)
                    .update(CollectionConstants.PROJECT_COUNTERS_FIELD.getValue(), FieldValue.arrayUnion(newCounterId))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            cf.complete(true);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            cf.completeExceptionally(e);
                        }
                    });
        });

        return cf;
    }

    public CompletableFuture<Boolean> updateUrlIds(String id, String newUrlId){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            collection.document(id)
                    .update(CollectionConstants.PROJECT_URLS_FIELD.getValue(), FieldValue.arrayUnion(newUrlId))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            cf.complete(true);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            cf.completeExceptionally(e);
                        }
                    });
        });

        return cf;
    }

    public CompletableFuture<Boolean> updateStatus(String id, String newStatus){
        return updateStringField(id, CollectionConstants.PROJECT_STATUS_FIELD.getValue(), newStatus);
    }

    public CompletableFuture<Boolean> updateName(String id, String newName){
        return updateStringField(id, CollectionConstants.PROJECT_NAME_FIELD.getValue(), newName);
    }

    private CompletableFuture<Boolean> updateStringField(String id, String field, String value){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            collection.document(id)
                    .update(field, value)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            cf.complete(true);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            cf.completeExceptionally(e);
                        }
                    });
        });

        return cf;
    }

    @Override
    public CompletableFuture<Boolean> updateRecord(String id, DatabaseObject obj){
        Project project = (Project) obj;
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        ArrayList<String> errors = new ArrayList<>();

        DocumentReference documentReference = collection.document(project.getId());

        CompletableFuture futureCounters = CompletableFuture.supplyAsync(()->
                documentReference.update(CollectionConstants.PROJECT_COUNTERS_FIELD.getValue(), project.getCounterIds().toArray())
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

        CompletableFuture futureUrls = CompletableFuture.supplyAsync(()->
                documentReference.update(CollectionConstants.PROJECT_URLS_FIELD.getValue(), project.getUrlIds().toArray())
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
                documentReference.update(CollectionConstants.PROJECT_NAME_FIELD.getValue(), project.getName())
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

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futureCounters, futureUrls, futureName);

        allFutures.thenRun(() -> {
            System.out.println("Finished");
            cf.complete(errors.size()==0);
        });

        return cf;
    }

    @Override
    public CompletableFuture<Boolean> insertRecord(String id, DatabaseObject obj) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        Project project = (Project) obj;

        Map<String, Object> projectMap = new HashMap<>();
        projectMap.put("id", project.getId());
        projectMap.put(CollectionConstants.PROJECT_COUNTERS_FIELD.getValue(), project.getCounterIds());
        projectMap.put(CollectionConstants.PROJECT_URLS_FIELD.getValue(), project.getUrlIds());
        projectMap.put(CollectionConstants.PROJECT_NAME_FIELD.getValue(), project.getName());
        projectMap.put(CollectionConstants.PROJECT_STATUS_FIELD.getValue(), project.getStatus());

        collection.document(project.getId())
                .set(projectMap)
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
