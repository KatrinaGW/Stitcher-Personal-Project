package com.example.stitcher.controllers.handlers;

import com.example.stitcher.controllers.CounterCollection;
import com.example.stitcher.controllers.ProjectsCollection;
import com.example.stitcher.controllers.UrlCollection;
import com.example.stitcher.models.Counter;
import com.example.stitcher.models.Project;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CreateCounterHandler {
    private CounterCollection counterCollection = new CounterCollection();
    private ProjectsCollection projectsCollection = new ProjectsCollection();

    public CompletableFuture<Boolean> createNewCounter(Counter counter, Project parentProject){
        parentProject.addCounterId(counter.getId());
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        ArrayList<Throwable> errors = new ArrayList<>();

        CompletableFuture futureCounter = CompletableFuture.supplyAsync(() ->
            counterCollection.insertRecord(counter.getId(), counter)
                    .thenAccept(success -> {
                        if(!success){
                            errors.add(new Exception("Something went wrong while creating the Counter"));
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
                projectsCollection.updateCounterIds(parentProject.getId(), parentProject.getCounterIds())
                        .thenAccept(success -> {
                            if(!success){
                                errors.add(new Exception("Something went wrong while updating the project counters"));
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

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futureCounter, futureProjectUpdate);

        allFutures.thenRun(() -> {
            System.out.println("Finished");
            cf.complete(errors.size()==0);
        });


        return cf;
    }
}
