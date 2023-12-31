package com.example.stitcher.controllers.handlers;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.example.stitcher.controllers.ProjectsCollection;
import com.example.stitcher.controllers.UrlCollection;
import com.example.stitcher.models.Project;
import com.example.stitcher.models.Url;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class UrlHandler {
    public static CompletableFuture<Boolean> deleteUrl(Url url, Project parentProject){
        parentProject.removeUrl(url.getId());
        ProjectHandler.clearStatusList(parentProject.getStatus());
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        ArrayList<Throwable> errors = new ArrayList<>();

        CompletableFuture futureUrl = CompletableFuture.supplyAsync(() ->
            UrlCollection.getInstance().deleteRecord(url.getId())
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
        );

        CompletableFuture futureProject = CompletableFuture.supplyAsync(() ->
                ProjectsCollection.getInstance().removeUrlId(parentProject.getId(), url.getId())
                .thenAccept(success -> {
                    if(!success){
                        errors.add(new Exception("Something went wrong when removing the url from the project!"));
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

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futureUrl, futureProject);

        allFutures.thenRun(() -> {
            cf.complete(errors.size()==0);
        });


        return cf;
    }

    public static CompletableFuture<Boolean> createNewUrl(Url url, Project parentProject){
        parentProject.addUrlId(url.getId());
        ProjectHandler.clearStatusList(parentProject.getStatus());
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        ArrayList<Throwable> errors = new ArrayList<>();

        CompletableFuture futureUrl = CompletableFuture.supplyAsync(() ->
                UrlCollection.getInstance().insertRecord(url.getId(), url)
                        .thenAccept(success -> {
                            if(!success){
                                errors.add(new Exception("Something went wrong while creating the Url"));
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
                ProjectsCollection.getInstance().updateUrlIds(parentProject.getId(), url.getId())
                        .thenAccept(success -> {
                            if(!success){
                                errors.add(new Exception("Something went wrong while updating the project urls"));
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

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futureUrl, futureProjectUpdate);

        allFutures.thenRun(() -> {
            cf.complete(errors.size()==0);
        });


        return cf;
    }
}
