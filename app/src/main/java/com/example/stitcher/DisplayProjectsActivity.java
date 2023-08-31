package com.example.stitcher;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stitcher.controllers.ProjectsCollection;
import com.example.stitcher.controllers.array_adapters.ProjectsArrayAdapter;
import com.example.stitcher.controllers.handlers.ProjectHandler;
import com.example.stitcher.models.DatabaseObject;
import com.example.stitcher.models.Project;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Function;

public class DisplayProjectsActivity extends AppCompatActivity implements EnterTextFragment.EnterTextFragmentHandler {
    ArrayList<Project> projects;
    ProjectsArrayAdapter projectsArrayAdapter;
    ListView projectsListview;
    Button newProjectBtn;
    FloatingActionButton deleteProjectBtn;
    private boolean deletingProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_projects);

    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        deletingProject = false;
        projectsListview = findViewById(R.id.strings_listview);
        newProjectBtn = findViewById(R.id.new_project_btn);
        deleteProjectBtn = findViewById(R.id.delete_project_fab);
        setProjectsArrayAdapter();
        setListeners();
    }

    private void toggleDeleting(){
        deletingProject = !deletingProject;
        newProjectBtn.setEnabled(!deletingProject);
    }

    private void setListeners(){

        deleteProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDeleting();
            }
        });

        projectsListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Project clickedProject = projects.get(position);

                if(!deletingProject){
                    Intent projectIntent = new Intent(DisplayProjectsActivity.this, DisplayProject.class);
                    projectIntent.putExtra(ViewConstants.SELECTED_PROJECT.getValue(), clickedProject);

                    startActivity(projectIntent);
                }else{
                    ProjectHandler projectHandler = new ProjectHandler();

                    projectHandler.deleteProject(clickedProject)
                            .thenAccept(success ->
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(success){
                                                toggleDeleting();
                                                setProjectsArrayAdapter();
                                            }else{
                                                Log.e(TAG, "Something went wrong when deleting the project!");
                                            }
                                        }
                                    }))
                            .exceptionally(new Function<Throwable, Void>() {
                                @Override
                                public Void apply(Throwable throwable) {
                                    Log.e(TAG, throwable.getMessage(), throwable);
                                    return null;
                                }
                            });
                }
            }
        });

        newProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                projectsListview.setVisibility(View.GONE);
                newProjectBtn.setEnabled(false);
                deleteProjectBtn.setEnabled(false);

                Bundle bundle = new Bundle();
                bundle.putInt(ViewConstants.FRAGMENT_HINT_MSG.getValue(), R.string.project_name_hint);
                bundle.putInt(ViewConstants.FRAGMENT_ERROR_MSG.getValue(), R.string.project_name_error_msg);
                EnterTextFragment fragment = new EnterTextFragment();
                fragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.new_project_fragment_container, EnterTextFragment.class, null)
                        .replace(R.id.new_project_fragment_container, fragment, null)
                        .commit();
            }
        });
    }

    private void setProjectsArrayAdapter(){
        ProjectsCollection projCollection = new ProjectsCollection();

        DisplayProjectsActivity projsActivity = this;

        if(projects == null){
            projects = new ArrayList<Project>();
        }else{
            projects.clear();
        }

        projCollection.getAll()
                .thenAccept(newProjs -> {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(DatabaseObject proj : newProjs){
                                projects.add((Project) proj);
                            }
                            projectsArrayAdapter = new ProjectsArrayAdapter(projsActivity, projects);
                            projectsListview.setAdapter(projectsArrayAdapter);
                        }
                    });
                })
                .exceptionally(throwable -> {
                    Log.w(TAG, throwable.getMessage());
                    return null;
                });
    }

    @Override
    public void dismissFragment() {
        newProjectBtn.setEnabled(true);
        projectsListview.setVisibility(View.VISIBLE);
        deleteProjectBtn.setEnabled(true);

        getSupportFragmentManager().beginTransaction().
                remove(getSupportFragmentManager().findFragmentById(R.id.new_project_fragment_container)).commit();
    }

    @Override
    public void createNew(String input) {
        ProjectHandler projectHandler = new ProjectHandler();

        projectHandler.createNewProject(new Project(UUID.randomUUID().toString(), input))
                .thenAccept(success ->
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(success){
                                    setProjectsArrayAdapter();
                                    dismissFragment();
                                }else{
                                    Log.e(TAG, "Something went wrong when creating the project");
                                }
                            }
                        }))
                .exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {
                        Log.e(TAG, throwable.getMessage(), throwable);
                        return null;
                    }
                });
    }
}
