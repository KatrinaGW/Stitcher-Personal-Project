package com.example.stitcher.views;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stitcher.R;
import com.example.stitcher.constants.Statuses;
import com.example.stitcher.constants.ViewConstants;
import com.example.stitcher.controllers.array_adapters.StatusesArrayAdapter;
import com.example.stitcher.controllers.handlers.ProjectHandler;

import java.util.function.Function;

public class CreateProjectActivity extends AppCompatActivity {
    private Button backBtn;
    private Button createProjBtn;
    private EditText projectNameTxt;
    private ListView statusesListview;
    private StatusesArrayAdapter statusesArrayAdapter;
    private String chosenStatus;
    private TextView errorTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_project);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private boolean verify(){
        return !this.projectNameTxt.getText().toString().isEmpty();
    }

    private void setAdapters(){
        statusesArrayAdapter = new StatusesArrayAdapter(this, Statuses.getAllValues());
        statusesListview.setAdapter(statusesArrayAdapter);
    }

    private void setListeners(){
        this.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateProjectActivity.this, DisplayProjectsActivity.class));
            }
        });

        this.createProjBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verify()){
                    String name = projectNameTxt.getText().toString();
                    ProjectHandler.createNewProject(name, chosenStatus)
                            .thenAccept(project ->
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                                Intent projectIntent = new Intent(CreateProjectActivity.this, DisplayProject.class);
                                                projectIntent.putExtra(ViewConstants.SELECTED_PROJECT.getValue(), project);

                                                startActivity(projectIntent);
                                        }
                                    }))
                            .exceptionally(new Function<Throwable, Void>() {
                                @Override
                                public Void apply(Throwable throwable) {
                                    Log.e(TAG, throwable.getMessage(), throwable);
                                    return null;
                                }
                            });
                }else{
                    errorTxt.setVisibility(View.VISIBLE);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            errorTxt.setVisibility(View.GONE);
                        }
                    }, 3000);
                }
            }
        });

        this.statusesListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chosenStatus = Statuses.getAllValues().get(position);
            }
        });
    }

    private void init(){
        this.backBtn = findViewById(R.id.new_project_back_btn);
        this.createProjBtn = findViewById(R.id.create_project_button);
        this.statusesListview = findViewById(R.id.statuses_listview);
        this.projectNameTxt = findViewById(R.id.create_project_name_edittext);
        this.errorTxt = findViewById(R.id.project_name_error_text);
        this.chosenStatus = Statuses.QUEUED.getValue();

        setAdapters();
        setListeners();
    }

}
