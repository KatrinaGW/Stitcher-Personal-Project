package com.example.stitcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stitcher.models.Counter;
import com.example.stitcher.models.Project;
import com.example.stitcher.models.Url;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class UrlWebviewActivity extends AppCompatActivity implements ProjectCountersFragment.ProjectCountersFragmentHandler {
    WebView webview;
    Url url;
    Project parentProject;
    Button backBtn;
    ArrayList<Counter> counters;
    FloatingActionButton countersBtn;
    FrameLayout countersFrame;
    private boolean countersFragmentVisible;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);

        Intent intent = getIntent();
        url = intent.getParcelableExtra(ViewConstants.SELECTED_URL.getValue());
        parentProject = intent.getParcelableExtra(ViewConstants.PARENT_PROJECT.getValue());
        counters = intent.getParcelableArrayListExtra(ViewConstants.FRAGMENT_PROJECT_COUNTERS.getValue());

        backBtn = findViewById(R.id.webview_back_btn);
        countersBtn = findViewById(R.id.counters_fab);
        countersFrame = findViewById(R.id.project_counters_frame);
        countersFrame.setVisibility(View.GONE);
        countersFragmentVisible = false;

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

        webview = (WebView) findViewById(R.id.myWebView);
        webview.setWebViewClient(new MyWebViewClient(this));
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(url.getUrl());
    }

    private void toggleFragmentVisibility(){
        countersFragmentVisible = !countersFragmentVisible;
        countersFrame.setVisibility(countersFragmentVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void counterChosen(Counter counter) {
        toggleFragmentVisibility();


    }
}
