package com.example.stitcher.controllers.handlers;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.example.stitcher.constants.Statuses;
import com.example.stitcher.controllers.CounterCollection;
import com.example.stitcher.controllers.ProjectsCollection;
import com.example.stitcher.controllers.UrlCollection;
import com.example.stitcher.models.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ProjectHandler {
    private static HashMap<String, ArrayList<Project>> projectLists = new HashMap<>();

    public static CompletableFuture<Project> createNewProject(String projectName, String projectStatus){
        CompletableFuture<Project> cf = new CompletableFuture<>();
        Project project = new Project(UUID.randomUUID().toString(), projectName, projectStatus);

        ProjectsCollection.getInstance().insertRecord(project.getId(), project)
                .thenAccept(success -> cf.complete(project))
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

    public static void clearStatusList(String status){
        projectLists.remove(status);
    }

    public static CompletableFuture<ArrayList<Project>> getProjectsWithStatus(String status){
        CompletableFuture<ArrayList<Project>> cf = new CompletableFuture<>();

        if(projectLists.containsKey(status)){
            cf.complete(projectLists.get(status));
        }else{
            if(Statuses.getAllValues().contains(status)){
                ProjectsCollection.getInstance().getAllWithStatus(status)
                        .thenAccept(projects -> {
                            projectLists.put(status, projects);
                            cf.complete(projects);
                        })
                        .exceptionally(e -> {
                            Log.e("TAG", e.getMessage());
                            return null;
                        });
            }else{
                cf.completeExceptionally(new IllegalArgumentException(String.format("The status '%s' does not exist!", status)));
            }
        }

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
