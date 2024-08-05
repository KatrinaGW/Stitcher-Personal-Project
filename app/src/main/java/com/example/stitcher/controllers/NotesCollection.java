package com.example.stitcher.controllers;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.stitcher.controllers.constants.CollectionConstants;
import com.example.stitcher.models.DatabaseObject;
import com.example.stitcher.models.Notes;
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

public class NotesCollection implements Collection {
    CollectionReference collection;
    FirebaseFirestore db;
    private static NotesCollection INSTANCE;

    private NotesCollection(){
        db = FirebaseFirestore.getInstance();
        collection = db.collection(CollectionConstants.NOTES_COLLECTION.getValue());
    }

    public static NotesCollection getInstance(){
        if(INSTANCE == null){
            INSTANCE = new NotesCollection();
        }

        return INSTANCE;
    }

    private Notes documentSnapshotToNotes(DocumentSnapshot doc){
        String note = (String) doc.getData().get(CollectionConstants.NOTES_FIELD.getValue());
        String id = doc.getId();
        String title = (String) doc.getData().get(CollectionConstants.NOTES_TITLE_FIELD.getValue());

        return new Notes(id, title, note);
    }


    @Override
    public CompletableFuture<ArrayList<? extends DatabaseObject>> getAll() {
        ArrayList<Notes> notes = new ArrayList<Notes>();
        CompletableFuture<ArrayList<? extends DatabaseObject>> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            collection.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId());
                                    Notes newNotes = documentSnapshotToNotes(document);
                                    notes.add(newNotes);
                                }
                                cf.complete(notes);
                            } else {
                                Log.w(TAG, "Error", task.getException());
                                cf.complete(notes);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "ERROR", e);
                            cf.completeExceptionally(e);
                        }
                    });
        });

        return cf;
    }

    public CompletableFuture<ArrayList<Notes>> getNotesWithIds(ArrayList<String> ids){
        CompletableFuture<ArrayList<Notes>> cf = new CompletableFuture<>();
        ArrayList<Notes> notes = new ArrayList<>();

        ArrayList<String> searchIds = new ArrayList<>();

        for(String id : ids){
            searchIds.add(id.trim());
        }

        CompletableFuture.runAsync(() -> {
            collection.whereIn(FieldPath.documentId(), searchIds)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {

                                Log.d(TAG, "Turning document into Note");
                                Notes newNote = documentSnapshotToNotes(document);

                                notes.add(newNote);
                            }
                            cf.complete(notes);
                    })
                    .addOnFailureListener(e-> {
                        Log.e(TAG, "ERROR", e);
                        cf.completeExceptionally(e);
                    });
        });

        return cf;
    }

    @Override
    public CompletableFuture<Boolean> updateRecord(String id, DatabaseObject obj) {
        Notes note = (Notes) obj;
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        DocumentReference documentReference = collection.document(note.getId());

        ArrayList<String> errors = new ArrayList<>();

        CompletableFuture noteBody = CompletableFuture.supplyAsync(() ->
                documentReference.update(CollectionConstants.NOTES_FIELD.getValue(), note.getNotes())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "ERROR", e);
                                errors.add(e.getMessage());
                            }
                        }));

        CompletableFuture noteTitle = CompletableFuture.supplyAsync(() ->
                documentReference.update(CollectionConstants.NOTES_TITLE_FIELD.getValue(), note.getTitle())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "ERROR", e);
                                errors.add(e.getMessage());
                            }
                        }));

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(noteBody, noteTitle);

        allFutures.thenRun(() -> {
            System.out.println("Finished");
            cf.complete(errors.size()==0);
        });

        return cf;
    }

    @Override
    public CompletableFuture<Boolean> insertRecord(String id, DatabaseObject obj) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        Notes notes = (Notes) obj;

        Map<String, Object> notesMap = new HashMap<>();
        notesMap.put("id", notes.getId());
        notesMap.put(CollectionConstants.NOTES_FIELD.getValue(), notes.getNotes());
        notesMap.put(CollectionConstants.NOTES_TITLE_FIELD.getValue(), notes.getTitle());

        collection.document(notes.getId())
                .set(notesMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        cf.complete(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "ERROR WRITING DOCUMENT", e);
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
                        Log.d(TAG, "SUCCESSFULLY DELETED");
                        cf.complete(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Document failed to delete");
                        Log.e(TAG, e.getMessage());
                        cf.completeExceptionally(e);
                    }
                });

        return cf;
    }
}
