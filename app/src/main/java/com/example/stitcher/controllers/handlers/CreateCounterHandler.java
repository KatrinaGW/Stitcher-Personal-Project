package com.example.stitcher.controllers.handlers;

import com.example.stitcher.controllers.CounterCollection;
import com.example.stitcher.controllers.ProjectsCollection;
import com.example.stitcher.controllers.UrlCollection;
import com.example.stitcher.models.Counter;
import com.example.stitcher.models.Project;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CreateCounterHandler {
    private CounterCollection counterCollection = new CounterCollection();
    private ProjectsCollection projectsCollection = new ProjectsCollection();

    public CompletableFuture<Boolean> createNewCounter(Counter counter, Project parentProject){
        parentProject.addCounterId(counter.getId());
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        counterCollection.insertRecord(counter.getId(), counter)
                .thenAccept(success -> {
                    if(success){

                    }else{

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
