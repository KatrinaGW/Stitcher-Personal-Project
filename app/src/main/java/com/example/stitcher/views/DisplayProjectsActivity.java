package com.example.stitcher.views;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stitcher.R;
import com.example.stitcher.constants.Actions;
import com.example.stitcher.constants.Statuses;
import com.example.stitcher.constants.ViewConstants;
import com.example.stitcher.controllers.ProjectsCollection;
import com.example.stitcher.controllers.array_adapters.ProjectsArrayAdapter;
import com.example.stitcher.controllers.handlers.ProjectHandler;
import com.example.stitcher.models.DatabaseObject;
import com.example.stitcher.models.Project;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Function;

public class DisplayProjectsActivity extends AppCompatActivity implements
        EnterTextFragment.EnterTextFragmentHandler, ConfirmationFragment.ConfirmationFragmentHandler {
    ArrayList<Project> projects;
    ProjectsArrayAdapter projectsArrayAdapter;
    ListView projectsListview;
    Button newProjectBtn;
    FloatingActionButton deleteProjectBtn;
    FloatingActionButton editProjectBtn;
    private String currentAction;
    private Project clickedProject;
    private LinearLayout statusesLayout;
    private String visibleStatus;
    private ArrayList<Button> statusButtons;
    private GradientDrawable selectedButtonBackground;
    private GradientDrawable unselectedButtonBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_projects);

        statusesLayout = findViewById(R.id.statuses_linear_layout);
        visibleStatus = Statuses.IN_PROGRESS.getValue();
        setStatusesLayout();

    }

    @Override
    protected void onResume() {
        super.onResume();
        currentAction = Actions.NO_ACTION.getValue();
        init();
    }

    private void init(){
        projectsListview = findViewById(R.id.strings_listview);
        newProjectBtn = findViewById(R.id.new_project_btn);
        deleteProjectBtn = findViewById(R.id.delete_project_fab);
        editProjectBtn = findViewById(R.id.edit_project_fab);
        setProjectsArrayAdapter();
        setListeners();
    }

    private void setStatusesLayout(){
        statusButtons = new ArrayList<>();

        unselectedButtonBackground = new GradientDrawable();
        unselectedButtonBackground.setShape(GradientDrawable.RECTANGLE);
        unselectedButtonBackground.setCornerRadius(20); // Adjust the corner radius as needed
        unselectedButtonBackground.setColor(getResources().getColor(R.color.url_background));

        selectedButtonBackground = new GradientDrawable();
        selectedButtonBackground.setShape(GradientDrawable.RECTANGLE);
        selectedButtonBackground.setCornerRadius(20); // Adjust the corner radius as needed
        selectedButtonBackground.setColor(getResources().getColor(R.color.purple_500));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        layoutParams.setMargins(5, 0, 5, 0);

        for(String status : Statuses.getAllValues()){
            Button newButton = new Button(this);
            newButton.setText(status);
            newButton.setTextSize(11);
            newButton.setTextColor(Color.WHITE);
            newButton.setLayoutParams(layoutParams);
            newButton.setBackground(status.equals(Statuses.IN_PROGRESS.getValue()) ?
                    selectedButtonBackground : unselectedButtonBackground);

            newButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    statusButtonClicked((Button) v);
                }
            });
            statusButtons.add(newButton);
            statusesLayout.addView(newButton);
        }
    }

    private void statusButtonClicked(Button button){
        if(!currentAction.equals(Actions.DELETING) && getSupportFragmentManager().getFragments().isEmpty()){
            visibleStatus = button.getText().toString();
            for(Button statusButton : statusButtons){
                if(statusButton == button){
                    statusButton.setBackground(selectedButtonBackground);
                }else{
                    statusButton.setBackground(unselectedButtonBackground);
                }
            }
            setProjectsArrayAdapter();
        }

    }

    private void setCurrentAction(String newAction){
        currentAction = newAction;
        boolean isNoAction = currentAction.equals(Actions.NO_ACTION.getValue());

        projectsListview.setVisibility(
                currentAction.equals(Actions.DELETING.getValue()) || isNoAction
                        || currentAction.equals(Actions.UPDATING.getValue()) ?
                        View.VISIBLE : View.GONE);
        newProjectBtn.setEnabled(isNoAction);
        deleteProjectBtn.setEnabled(isNoAction || currentAction.equals(Actions.DELETING.getValue()));
        editProjectBtn.setEnabled(isNoAction || currentAction.equals(Actions.UPDATING.getValue()));
    }

    private void setListeners(){

        deleteProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentAction(currentAction.equals(Actions.DELETING.getValue())
                        && getSupportFragmentManager().getFragments().isEmpty() ?
                        Actions.NO_ACTION.getValue() : Actions.DELETING.getValue());
            }
        });

        projectsListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickedProject = projects.get(position);

                if(currentAction.equals(Actions.NO_ACTION.getValue())){
                    Intent projectIntent = new Intent(DisplayProjectsActivity.this, DisplayProject.class);
                    projectIntent.putExtra(ViewConstants.SELECTED_PROJECT.getValue(), clickedProject);

                    startActivity(projectIntent);
                }else if(currentAction.equals(Actions.DELETING.getValue()) && getSupportFragmentManager().getFragments().isEmpty()){
                    Bundle bundle = new Bundle();
                    bundle.putString(ViewConstants.FRAGMENT_HEADER.getValue(), String.format("Delete %s?", clickedProject.getName()));
                    bundle.putString(ViewConstants.FRAGMENT_CONFIRM_LABEL.getValue(), "Yes, delete");
                    bundle.putString(ViewConstants.FRAGMENT_CANCEL_LABEL.getValue(), "No, Cancel");
                    ConfirmationFragment fragment = new ConfirmationFragment();
                    fragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.project_name_fragment_container, ConfirmationFragment.class, null)
                            .replace(R.id.project_name_fragment_container, fragment, null)
                            .commit();
                } else if (currentAction.equals(Actions.UPDATING.getValue())) {
                    projectsListview.setVisibility(View.GONE);
                    Bundle bundle = new Bundle();
                    bundle.putInt(ViewConstants.FRAGMENT_HINT_MSG.getValue(), R.string.project_name_hint);
                    bundle.putInt(ViewConstants.FRAGMENT_ERROR_MSG.getValue(), R.string.project_name_error_msg);
                    bundle.putString(ViewConstants.FRAGMENT_EXISTING_STRING.getValue(), clickedProject.getName());
                    EnterTextFragment fragment = new EnterTextFragment();
                    fragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.project_name_fragment_container, EnterTextFragment.class, null)
                            .replace(R.id.project_name_fragment_container, fragment, null)
                            .commit();

                }
            }
        });

        editProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentAction(
                        currentAction.equals(Actions.UPDATING.getValue()) ?
                                Actions.NO_ACTION.getValue() :
                                Actions.UPDATING.getValue()
                );
            }
        });

        newProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DisplayProjectsActivity.this, CreateProjectActivity.class));
            }
        });
    }

    private void setProjectsArrayAdapter(){
        DisplayProjectsActivity projsActivity = this;

        ProjectHandler.getProjectsWithStatus(visibleStatus)
                .thenAccept(newProjs -> {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            projects = newProjs;
                            projectsArrayAdapter = new ProjectsArrayAdapter(projsActivity, projects);
                            projectsListview.setAdapter(projectsArrayAdapter);
                        }
                    });
                })
                .exceptionally(throwable -> {
                    Log.e(TAG, throwable.getMessage());
                    return null;
                });
    }

    @Override
    public void dismissFragment() {
        setCurrentAction(Actions.NO_ACTION.getValue());

        getSupportFragmentManager().beginTransaction().
                remove(getSupportFragmentManager().findFragmentById(R.id.project_name_fragment_container)).commit();
    }

    @Override
    public void createNew(String input) {
        if(currentAction.equals(Actions.UPDATING.getValue())){
            ProjectHandler.updateProjectName(clickedProject, input)
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

    @Override
    public void cancelled() {
        dismissFragment();
    }

    @Override
    public void confirmed() {
        ProjectHandler.deleteProject(clickedProject)
                .thenAccept(success ->
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(success){
                                    setCurrentAction(Actions.NO_ACTION.getValue());
                                    setProjectsArrayAdapter();
                                    dismissFragment();
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
