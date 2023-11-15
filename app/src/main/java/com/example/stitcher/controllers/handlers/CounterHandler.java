package com.example.stitcher.controllers.handlers;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.example.stitcher.controllers.CounterCollection;
import com.example.stitcher.controllers.ProjectsCollection;
import com.example.stitcher.models.Counter;
import com.example.stitcher.models.Project;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CounterHandler {

    public static CompletableFuture<Boolean> saveCounterState(Counter counter){
        CompletableFuture<Boolean> cf = new CompletableFuture<>();

        CounterCollection.getInstance().updateRecord(counter.getId(), counter)
                .thenAccept(success ->{
                    if(success){
                        cf.complete(true);
                    }else{
                        cf.completeExceptionally(
                                new Exception("Something went wrong when saving the counter!")
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

    public static boolean handleCounterValueChange(Counter counter, int addend){
        boolean success = false;

        if(counter.getCount() + addend > -1){
            counter.addToCount(addend);
            success = true;
        }

        return success;
    }
    public static CompletableFuture<Boolean> deleteCounter(Counter counter, Project parentProject){
        ProjectHandler.clearStatusList(parentProject.getStatus());
        parentProject.removeCounter(counter.getId());
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        ArrayList<Throwable> errors = new ArrayList<>();

        CompletableFuture futureCounter = CompletableFuture.supplyAsync(() ->
                CounterCollection.getInstance().deleteRecord(counter.getId())
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
        );

        CompletableFuture futureProject = CompletableFuture.supplyAsync(() ->
                ProjectsCollection.getInstance().removeCounterid(parentProject.getId(), counter.getId())
                        .thenAccept(success -> {
                            if(!success){
                                errors.add(new Exception("Something went wrong when removing the counter from the project!"));
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

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futureCounter, futureProject);

        allFutures.thenRun(() -> {
            cf.complete(errors.size()==0);
        });


        return cf;
    }

    public static CompletableFuture<Boolean> createNewCounter(Counter counter, Project parentProject){
        ProjectHandler.clearStatusList(parentProject.getStatus());
        parentProject.addCounterId(counter.getId());
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        ArrayList<Throwable> errors = new ArrayList<>();

        CompletableFuture futureCounter = CompletableFuture.supplyAsync(() ->
            CounterCollection.getInstance().insertRecord(counter.getId(), counter)
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
                ProjectsCollection.getInstance().updateCounterIds(parentProject.getId(), counter.getId())
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
            cf.complete(errors.size()==0);
        });


        return cf;
    }
}
