package com.example.stitcher.controllers.handlers;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.example.stitcher.controllers.CounterCollection;
import com.example.stitcher.controllers.ProjectsCollection;
import com.example.stitcher.controllers.UrlCollection;
import com.example.stitcher.models.Project;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ProjectHandler {
    public static CompletableFuture<Boolean> createNewProject(Project project){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        ProjectsCollection.getInstance().insertRecord(project.getId(), project)
                .thenAccept(success -> cf.complete(success))
                .exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {
                        Log.e(TAG, "ERROR", throwable);
                        cf.completeExceptionally(throwable);
                        return null;
                    }
                });

        return cf;
    }

    public static CompletableFuture<Boolean> updateProjectStatus(Project project, String newStatus){
        project.setStatus(newStatus);
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        ProjectsCollection.getInstance().updateStatus(project.getId(), project.getStatus())
                .thenAccept(success ->
                        cf.complete(success)
                )
                .exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {
                        Log.e(TAG, "ERROR", throwable);
                        cf.completeExceptionally(throwable);
                        return null;
                    }
                });

        return cf;
    }

    public static CompletableFuture<Boolean> updateProjectName(Project project, String newName){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        project.setName(newName);

        ProjectsCollection.getInstance().updateName(project.getId(), project.getName())
                .thenAccept(success -> cf.complete(success))
                .exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {
                        Log.e(TAG, "ERROR", throwable);
                        cf.completeExceptionally(throwable);
                        return null;
                    }
                });

        return cf;
    }

    public static CompletableFuture<Boolean> deleteProject(Project project){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        ArrayList<Throwable> errors = new ArrayList<>();
        ArrayList<CompletableFuture> cfs = new ArrayList<>();

        for(String urlId : project.getUrlIds()){
            cfs.add(CompletableFuture.supplyAsync(() ->
                    UrlCollection.getInstance().deleteRecord(urlId)
                            .thenAccept(success -> {
                                if(!success){
                                    errors.add(new Exception("Something went wrong when deleting the url!"));
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
            ));
        }

        for(String counterId : project.getCounterIds()){
            cfs.add(CompletableFuture.supplyAsync(() ->
                    CounterCollection.getInstance().deleteRecord(counterId)
                            .thenAccept(success -> {
                                if(!success){
                                    errors.add(new Exception("Something went wrong when deleting the counter!"));
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
            ));
        }

        cfs.add(CompletableFuture.supplyAsync(() ->
                ProjectsCollection.getInstance().deleteRecord(project.getId())
                        .thenAccept(success -> {
                            if(!success){
                                errors.add(new Exception("Something went wrong when deleting the project!"));
                            }
                        })
                        .exceptionally(new Function<Throwable, Void>() {
                            @Override
                            public Void apply(Throwable throwable) {
                                errors.add(throwable);
                                Log.e(TAG, throwable.getMessage(), throwable);
                                return null;
                            }
                        }))
        );

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(cfs.toArray(new CompletableFuture[cfs.size()]));

        allFutures.thenRun(() -> {
            cf.complete(errors.size()==0);
        });


        return cf;

    }
}
