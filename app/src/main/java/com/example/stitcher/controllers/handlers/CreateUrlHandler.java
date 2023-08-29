package com.example.stitcher.controllers.handlers;

import com.example.stitcher.controllers.ProjectsCollection;
import com.example.stitcher.controllers.UrlCollection;
import com.example.stitcher.models.Project;
import com.example.stitcher.models.Url;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CreateUrlHandler {
    UrlCollection urlCollection = new UrlCollection();
    ProjectsCollection projectsCollection = new ProjectsCollection();

    public CompletableFuture<Boolean> createNewUrl(Url url, Project parentProject){
        parentProject.addUrlId(url.getId());
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        ArrayList<Throwable> errors = new ArrayList<>();

        CompletableFuture futureUrl = CompletableFuture.supplyAsync(() ->
                urlCollection.insertRecord(url.getId(), url)
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
                projectsCollection.updateUrlIds(parentProject.getId(), url.getId())
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
