package com.example.stitcher.controllers.handlers;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.example.stitcher.controllers.NotesCollection;
import com.example.stitcher.controllers.ProjectsCollection;
import com.example.stitcher.models.Notes;
import com.example.stitcher.models.Project;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class NotesHandler {
    public static CompletableFuture<Boolean> deleteNote(Notes note, Project parentProject){
        parentProject.removeNote(note.getId());
        ProjectHandler.clearStatusList(parentProject.getStatus());

        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        ArrayList<Throwable> errors = new ArrayList<>();

        CompletableFuture futureNote = CompletableFuture.supplyAsync(() ->
                        NotesCollection.getInstance().deleteRecord(note.getId())
                                .thenAccept(success -> {
                                    if(!success){
                                        errors.add(new Exception("Something went wrong when trying to delete from the notes collection"));
                                    }
                                })
                                .exceptionally(new Function<Throwable, Void>() {
                                    @Override
                                    public Void apply(Throwable throwable) {
                                        errors.add(throwable);
                                        Log.e(TAG, "ERROR", throwable);
                                        return null;
                                    }
                                })
                );

        CompletableFuture futureProject = CompletableFuture.supplyAsync(() ->
                ProjectsCollection.getInstance().removeNotesId(parentProject.getId(), note.getId())
                        .thenAccept(success -> {
                            if(!success){
                                errors.add(new Exception("Something went wrong when removing the note from the project!"));
                            }
                        }).exceptionally(new Function<Throwable, Void>() {
                            @Override
                            public Void apply(Throwable throwable) {
                                Log.e(TAG, "ERROR", throwable);
                                errors.add(throwable);
                                return null;
                            }
                        })
        );

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futureNote, futureProject);

        allFutures.thenRun(() -> cf.complete(errors.size()==0));

        return cf;
    }

    public static CompletableFuture<Notes> createNewNote(String noteBody, String noteTitle, Project parentProject){
        Notes note = new Notes(UUID.randomUUID().toString(), noteTitle, noteBody);
        parentProject.addNoteId(note.getId());
        ProjectHandler.clearStatusList(parentProject.getStatus());
        CompletableFuture<Notes> cf = new CompletableFuture<>();
        ArrayList<Throwable> errors = new ArrayList<>();

        CompletableFuture futureNote = CompletableFuture.supplyAsync(() ->
                NotesCollection.getInstance().insertRecord(note.getId(), note)
                        .thenAccept(success -> {
                            if(!success){
                                errors.add(new Exception("Something went wrong while creating the Note"));
                            }
                        })
                        .exceptionally(new Function<Throwable, Void>() {
                            @Override
                            public Void apply(Throwable throwable) {
                                errors.add(throwable);
                                return null;
                            }
                        })

        );

        CompletableFuture futureProjectUpdate = CompletableFuture.supplyAsync(() ->
                ProjectsCollection.getInstance().updateNotesId(parentProject.getId(), note.getId())
                        .thenAccept(success -> {
                            if(!success){
                                errors.add(new Exception("Something went wrong while updating the project notes"));
                            }
                        })
                        .exceptionally(new Function<Throwable, Void>() {
                            @Override
                            public Void apply(Throwable throwable) {
                                errors.add(throwable);
                                return null;
                            }
                        })
        );

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futureNote, futureProjectUpdate);

        allFutures.thenRun(() -> {
            cf.complete(note);
        });


        return cf;

    }

    public static CompletableFuture<Notes> saveNote(Notes note, String newBody, String newTitle){
        note.setBody(newBody);
        note.setTitle(newTitle);
        CompletableFuture<Notes> cf = new CompletableFuture<>();

        NotesCollection.getInstance().updateRecord(note.getId(), note)
                .thenAccept(success -> {
                    if(success){
                        cf.complete(note);
                    }else{
                        cf.completeExceptionally(
                                new Exception("Something went wrong when saving the note state")
                        );
                    }
                })
                .exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {
                        cf.completeExceptionally(throwable);
                        return null;
                    }
                });

        return cf;
    }


}
