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
import com.example.stitcher.controllers.handlers.CounterHandler;
import com.example.stitcher.models.Counter;
import com.example.stitcher.models.Project;
import com.example.stitcher.models.Url;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.function.Function;

public class UrlWebviewActivity extends AppCompatActivity implements ProjectCountersFragment.ProjectCountersFragmentHandler {
    WebView webview;
    Url url;
    Project parentProject;
    Button backBtn;
    ArrayList<Counter> counters;
    FloatingActionButton countersBtn;
    FloatingActionButton addToCounterBtn;
    FloatingActionButton subtractCounterBtn;
    FloatingActionButton saveBtn;
    FloatingActionButton editBtn;
    FloatingActionButton addCounterBtn;
    TextView counterValueTxt;
    TextView savedTxt;
    FrameLayout countersFrame;
    private boolean countersFragmentVisible;
    private Counter chosenCounter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);

        Intent intent = getIntent();
        url = intent.getParcelableExtra(ViewConstants.SELECTED_URL.getValue());
        parentProject = intent.getParcelableExtra(ViewConstants.PARENT_PROJECT.getValue());
        counters = intent.getParcelableArrayListExtra(ViewConstants.FRAGMENT_PROJECT_COUNTERS.getValue());

        checkCountersOrGet();

        setViewComponents();
        setListeners();

        webview = (WebView) findViewById(R.id.myWebView);
        webview.setWebViewClient(new MyWebViewClient(this));
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(url.getUrl());
    }

    private void checkCountersOrGet(){
        if(counters == null){
            CounterCollection.getInstance().getCountersWithIds(parentProject.getCounterIds())
                    .thenAccept(parentCounters ->
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    counters = parentCounters;
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
        addCounterBtn = findViewById(R.id.add_counter_webview_fab);
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

        countersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFragmentVisibility();

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
    }

    private void handleCountChange(int changeValue){
        boolean validCount = CounterHandler.handleCounterValueChange(chosenCounter, changeValue);

        if(validCount){
            handleCounterValue();
        }
    }

    private void toggleFragmentVisibility(){
        countersFragmentVisible = !countersFragmentVisible;

        if(countersFragmentVisible){
            chosenCounter = null;
        }

        countersFrame.setVisibility(countersFragmentVisible ? View.VISIBLE : View.GONE);
        addToCounterBtn.setVisibility(chosenCounter == null ? View.GONE : View.VISIBLE);
        subtractCounterBtn.setVisibility(chosenCounter == null ? View.GONE : View.VISIBLE);
        counterValueTxt.setVisibility(chosenCounter == null  ? View.GONE : View.VISIBLE);
        addCounterBtn.setEnabled(chosenCounter == null && !countersFragmentVisible);
        saveBtn.setVisibility(chosenCounter == null ? View.GONE : View.VISIBLE);
        editBtn.setVisibility(chosenCounter == null ? View.GONE : View.VISIBLE);
        handleCountersBtnImage();

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
        toggleFragmentVisibility();


    }
}
