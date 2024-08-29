package com.example.stitcher.views;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.stitcher.R;
import com.example.stitcher.constants.ViewConstants;
import com.example.stitcher.controllers.CounterCollection;
import com.example.stitcher.controllers.NotesCollection;
import com.example.stitcher.controllers.handlers.CounterHandler;
import com.example.stitcher.controllers.handlers.NotesHandler;
import com.example.stitcher.models.Counter;
import com.example.stitcher.models.Notes;
import com.example.stitcher.models.Project;
import com.example.stitcher.models.Url;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.function.Function;

public class UrlWebviewActivity extends AppCompatActivity implements ProjectCountersFragment.ProjectCountersFragmentHandler, NotesFragment.NotesFragmentHandler {
    WebView webview;
    Url url;
    Project parentProject;
    Button backBtn;
    ArrayList<Counter> counters;
    ArrayList<Notes> notes;
    FloatingActionButton countersBtn;
    FloatingActionButton addToCounterBtn;
    FloatingActionButton subtractCounterBtn;
    FloatingActionButton saveBtn;
    FloatingActionButton editBtn;
    FloatingActionButton addCounterBtn;
    FloatingActionButton viewNotesBtn;
    TextView counterValueTxt;
    TextView savedTxt;
    FrameLayout countersFrame;
    private boolean countersFragmentVisible;
    private boolean notesFragmentVisible;
    private Counter chosenCounter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);

        Intent intent = getIntent();
        url = intent.getParcelableExtra(ViewConstants.SELECTED_URL.getValue());
        parentProject = intent.getParcelableExtra(ViewConstants.PARENT_PROJECT.getValue());
        counters = intent.getParcelableArrayListExtra(ViewConstants.FRAGMENT_PROJECT_COUNTERS.getValue());
        notes = intent.getParcelableArrayListExtra(ViewConstants.NOTES_FIELD.getValue());

        checkListsOrGet();

        setViewComponents();
        setListeners();

        webview = (WebView) findViewById(R.id.myWebView);
        webview.setWebViewClient(new MyWebViewClient(this));
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(url.getUrl());
    }

    private void checkListsOrGet(){
        if(counters == null){
            CounterCollection.getInstance().getCountersWithIds(parentProject.getCounterIds())
                    .thenAccept(parentCounters ->
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    counters = parentCounters;
                                    if(counters.isEmpty()){
                                        countersBtn.setVisibility(View.GONE);
                                    }else{
                                        countersBtn.setVisibility(View.VISIBLE);
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

        if(notes == null){
            NotesCollection.getInstance().getNotesWithIds(parentProject.getNotesIds())
                    .thenAccept(parentNotes ->
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    notes = parentNotes;
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

    private void setViewComponents(){
        backBtn = findViewById(R.id.webview_back_btn);
        countersBtn = findViewById(R.id.counters_fab);
        countersFrame = findViewById(R.id.project_counters_frame);
        savedTxt = findViewById(R.id.webview_saved);
        savedTxt.setVisibility(View.GONE);
        countersFrame.setVisibility(View.GONE);
        countersFragmentVisible = false;
        notesFragmentVisible = false;
        addCounterBtn = findViewById(R.id.add_counter_webview_fab);
        viewNotesBtn = findViewById(R.id.webview_notes_btn);
        saveBtn = findViewById(R.id.counter_webview_save);
        addToCounterBtn = findViewById(R.id.add_to_counter_fab);
        subtractCounterBtn = findViewById(R.id.counters_subtract_fab);
        counterValueTxt = findViewById(R.id.counter_value_txt);
        editBtn = findViewById(R.id.counter_webview_edit_btn);
        editBtn.setVisibility(View.GONE);
        saveBtn.setVisibility(View.GONE);
        addToCounterBtn.setVisibility(View.GONE);
        subtractCounterBtn.setVisibility(View.GONE);
        counterValueTxt.setVisibility(View.GONE);

        handleCountersBtnImage();
    }

    private void setListeners(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent projectIntent = new Intent(UrlWebviewActivity.this, DisplayProject.class);
                projectIntent.putExtra(ViewConstants.SELECTED_PROJECT.getValue(), parentProject);
                startActivity(projectIntent);
            }
        });

        viewNotesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFragmentVisibility(false, true);

                if(notesFragmentVisible){
                    Bundle bundle = new Bundle();
                    NotesFragment fragment = new NotesFragment();

                    bundle.putParcelableArrayList(ViewConstants.NOTES_FIELD.getValue(), notes);
                    bundle.putParcelable(ViewConstants.PARENT_PROJECT.getValue(), parentProject);

                    fragment.setArguments(bundle);

                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.choose_counter_fragment_container, NotesFragment.class, null)
                            .replace(R.id.choose_counter_fragment_container, fragment, null)
                            .commit();
                }
            }
        });

        countersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFragmentVisibility(true, false);

                if(countersFragmentVisible){
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(ViewConstants.FRAGMENT_PROJECT_COUNTERS.getValue(), counters);
                    ProjectCountersFragment fragment = new ProjectCountersFragment();
                    fragment.setArguments(bundle);

                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.choose_counter_fragment_container, ProjectCountersFragment.class, null)
                            .replace(R.id.choose_counter_fragment_container, fragment, null)
                            .commit();
                }
            }

        });

        subtractCounterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCountChange(-1);
            }
        });

        addToCounterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCountChange(1);
            }
        });

        addCounterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent counterIntent = new Intent(UrlWebviewActivity.this, StitchCounterActivity.class);
                counterIntent.putExtra(ViewConstants.PARENT_PROJECT.getValue(), parentProject);
                counterIntent.putExtra(ViewConstants.BACK_NAVIGATE_URL.getValue(), url);

                startActivity(counterIntent);
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent counterIntent = new Intent(UrlWebviewActivity.this, StitchCounterActivity.class);
                counterIntent.putExtra(ViewConstants.SELECTED_COUNTER.getValue(), chosenCounter);
                counterIntent.putExtra(ViewConstants.PARENT_PROJECT.getValue(), parentProject);
                counterIntent.putExtra(ViewConstants.BACK_NAVIGATE_URL.getValue(), url);

                startActivity(counterIntent);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CounterHandler.saveCounterState(chosenCounter)
                        .thenAccept(success ->
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        savedTxt.setVisibility(View.VISIBLE);
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                savedTxt.setVisibility(View.GONE);
                                            }
                                        }, 3000);
                                    }
                                })
                        )
                        .exceptionally(new Function<Throwable, Void>() {
                            @Override
                            public Void apply(Throwable throwable) {
                                Log.e(TAG, throwable.getMessage(), throwable);
                                return null;
                            }
                        });
            }
        });
    }

    private void handleCountersBtnImage(){
        if(countersFragmentVisible){
            countersBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circled_x));
        }

        if(!countersFragmentVisible && chosenCounter != null){
            countersBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circled_back_arrow));
        }

        if(!countersFragmentVisible && chosenCounter == null){
            countersBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tallies));

        }

        if(counters == null || counters.isEmpty()){
            countersBtn.setVisibility(View.GONE);
        }
    }

    private void handleCountChange(int changeValue){
        boolean validCount = CounterHandler.handleCounterValueChange(chosenCounter, changeValue);

        if(validCount){
            handleCounterValue();
        }
    }

    private void toggleFragmentVisibility(boolean toggleCounters, boolean toggleNotes){
        if(toggleCounters){
            countersFragmentVisible = !countersFragmentVisible;
        }

        if(toggleNotes){
            notesFragmentVisible=!notesFragmentVisible;
        }

        boolean fragmentShowing = countersFragmentVisible || notesFragmentVisible;

        if(countersFragmentVisible){
            chosenCounter = null;
        }

        countersFrame.setVisibility(fragmentShowing ? View.VISIBLE : View.GONE);
        addToCounterBtn.setVisibility(chosenCounter == null ? View.GONE : View.VISIBLE);
        subtractCounterBtn.setVisibility(chosenCounter == null ? View.GONE : View.VISIBLE);
        counterValueTxt.setVisibility(chosenCounter == null  ? View.GONE : View.VISIBLE);
        addCounterBtn.setEnabled(chosenCounter == null && !fragmentShowing);
        viewNotesBtn.setEnabled(chosenCounter==null && !fragmentShowing);
        saveBtn.setVisibility(chosenCounter == null ? View.GONE : View.VISIBLE);
        editBtn.setVisibility(chosenCounter == null ? View.GONE : View.VISIBLE);
        handleCountersBtnImage();

        countersBtn.setVisibility(counters.isEmpty() ? View.GONE : View.VISIBLE);

        if(counterValueTxt.getVisibility() == View.VISIBLE){
            handleCounterValue();
        }
    }

    private void handleCounterValue(){
        int count = chosenCounter.getCount();
        counterValueTxt.setText(String.valueOf(count));
        subtractCounterBtn.setEnabled(count > 0);
        counterValueTxt.setTextColor(HelperFunctions.getCounterColourCode(chosenCounter));
    }

    @Override
    public void counterChosen(Counter counter) {
        chosenCounter = counter;
        toggleFragmentVisibility(true, false);


    }

    @Override
    public void closed() {
        toggleFragmentVisibility(false, true);
    }

    @Override
    public void noteCreated(String noteTitle, String noteBody) {
        NotesHandler.createNewNote(noteBody, noteTitle, parentProject)
                .thenAccept(note ->
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toggleFragmentVisibility(false, true);
                                notes.add(note);
                            }
                        })
                )
                .exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {
                        Log.e(TAG, throwable.getMessage(), throwable);

                        return null;
                    }
                });
    }

    @Override
    public void noteUpdated(Notes note, String newBody, String newTitle) {
        NotesHandler.saveNote(note, newBody, newTitle)
                .thenAccept(success ->
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toggleFragmentVisibility(false, true);
                            }
                        })
                )
                .exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {
                        Log.e(TAG, throwable.getMessage(), throwable);

                        return null;
                    }
                });
    }

    @Override
    public void noteDeleted(Notes note) {
        NotesHandler.deleteNote(note, parentProject)
                .thenAccept(success ->
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(success){
                                    int index = notes.indexOf(note);
                                    notes.remove(index);
                                    toggleFragmentVisibility(false, true);
                                }else{
                                    Log.e(TAG, "Something went wrong when deleting the note");
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
