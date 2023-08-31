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
    private ProjectsCollection projectsCollection = new ProjectsCollection();
    private CounterCollection counterCollection = new CounterCollection();
    private UrlCollection urlCollection = new UrlCollection();

    public CompletableFuture<Boolean> createNewProject(Project project){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        projectsCollection.insertRecord(project.getId(), project)
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

    public CompletableFuture<Boolean> updateProjectName(Project project){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        projectsCollection.updateName(project.getId(), project.getName())
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

    public CompletableFuture<Boolean> deleteProject(Project project){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        ArrayList<Throwable> errors = new ArrayList<>();
        ArrayList<CompletableFuture> cfs = new ArrayList<>();

        for(String urlId : project.getUrlIds()){
            cfs.add(CompletableFuture.supplyAsync(() ->
                    urlCollection.deleteRecord(urlId)
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
                    counterCollection.deleteRecord(counterId)
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
                projectsCollection.deleteRecord(project.getId())
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
