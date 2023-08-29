package com.example.stitcher;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stitcher.controllers.ProjectsCollection;
import com.example.stitcher.controllers.UrlCollection;
import com.example.stitcher.controllers.array_adapters.ProjectsArrayAdapter;
import com.example.stitcher.controllers.array_adapters.UrlsArrayAdapter;
import com.example.stitcher.models.DatabaseObject;
import com.example.stitcher.models.Project;
import com.example.stitcher.models.Url;

import java.util.ArrayList;

public class DisplayProjectsActivity extends AppCompatActivity {
    ArrayList<Project> projects;
    ProjectsArrayAdapter projectsArrayAdapter;
    ListView projectsListview;
    Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_strings);

    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        projectsListview = findViewById(R.id.strings_listview);
        backBtn = findViewById(R.id.strings_back_btn);
        setProjectsArrayAdapter();
        setListeners();
    }

    private void setListeners(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DisplayProjectsActivity.this, MainActivity.class));
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

}
