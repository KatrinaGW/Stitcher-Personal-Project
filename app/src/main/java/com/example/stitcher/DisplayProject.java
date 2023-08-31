package com.example.stitcher;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stitcher.controllers.CounterCollection;
import com.example.stitcher.controllers.UrlCollection;
import com.example.stitcher.controllers.array_adapters.CounterArrayAdapter;
import com.example.stitcher.controllers.array_adapters.UrlsArrayAdapter;
import com.example.stitcher.controllers.handlers.UrlHandler;
import com.example.stitcher.models.Counter;
import com.example.stitcher.models.Project;
import com.example.stitcher.models.Url;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.function.Function;

public class DisplayProject extends AppCompatActivity implements AddUrlFragment.AddUrlFragmentHandler {
    UrlsArrayAdapter urlsArrayAdapter;
    CounterArrayAdapter counterArrayAdapter;
    ListView urlsListView;
    ListView countersListView;
    ArrayList<Counter> counters;
    ArrayList<Url> urls;
    Project project;
    Button backBtn;
    Button newCounterBtn;
    Button newUrlBtn;
    FloatingActionButton deleteUrlBtn;
    TextView countersHeader;
    private boolean deletingURL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_project);

        Intent intent = getIntent();
        project = intent.getParcelableExtra(ViewConstants.SELECTED_PROJECT.getValue());
    }

    @Override
    protected void onResume() {
        super.onResume();
        deletingURL = false;
        init();
    }

    private void setAdapters(){
        DisplayProject displayProject = this;
        UrlCollection urlCollection = new UrlCollection();
        CounterCollection counterCollection = new CounterCollection();

        if(project.getUrlIds().size()==0){
            urls = new ArrayList<>();
            urlsArrayAdapter = new UrlsArrayAdapter(displayProject, urls);
            urlsListView.setAdapter(urlsArrayAdapter);
        }else{
            urlCollection.getUrlsWithIds(project.getUrlIds())
                    .thenAccept(newUrls -> {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                urls = newUrls;
                                urlsArrayAdapter = new UrlsArrayAdapter(displayProject, urls);
                                urlsListView.setAdapter(urlsArrayAdapter);
                            }
                        });
                    })
                    .exceptionally(throwable -> {
                        Log.w(TAG, throwable.getMessage());
                        return null;
                    });
        }

        counterCollection.getCountersWithIds(project.getCounterIds())
                .thenAccept(newCounters -> {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            counters = newCounters;
                            counterArrayAdapter = new CounterArrayAdapter(displayProject, counters);
                            countersListView.setAdapter(counterArrayAdapter);
                        }
                    });
                })
                .exceptionally(throwable -> {
                    Log.w(TAG, throwable.getMessage());
                    return null;
                });
    }

    private void setListeners(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DisplayProject.this, DisplayProjectsActivity.class));
            }
        });

        newCounterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent counterIntent = new Intent(DisplayProject.this, StitchCounterActivity.class);
                counterIntent.putExtra(ViewConstants.PARENT_PROJECT.getValue(), project);

                startActivity(counterIntent);
            }
        });

        newUrlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countersListView.setVisibility(View.GONE);
                urlsListView.setVisibility(View.GONE);
                backBtn.setVisibility(View.GONE);
                newCounterBtn.setVisibility(View.GONE);
                deleteUrlBtn.setVisibility(View.GONE);
                newUrlBtn.setVisibility(View.GONE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.add_url_fragment_container, AddUrlFragment.class, null)
                        .commit();
            }
        });

        urlsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Url clickedUrl = urls.get(position);

                if(!deletingURL){
                    Intent urlIntent = new Intent(DisplayProject.this, UrlWebviewActivity.class);
                    urlIntent.putExtra(ViewConstants.SELECTED_URL.getValue(), clickedUrl);
                    urlIntent.putExtra(ViewConstants.PARENT_PROJECT.getValue(), project);

                    startActivity(urlIntent);
                }else{
                    UrlHandler urlHandler = new UrlHandler();

                    urlHandler.deleteUrl(clickedUrl, project)
                            .thenAccept(success ->
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(success){
                                                setAdapters();
                                                toggleDeletingUrl();
                                            }else{
                                                Log.e(TAG, "Something went wrong when deleting the URL");
                                            }
                                        }
                                    }))
                            .exceptionally(new Function<Throwable, Void>() {
                                @Override
                                public Void apply(Throwable throwable) {
                                    Log.e(TAG, "ERROR", throwable);
                                    return null;
                                }
                            });
                }
            }
        });

        countersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Counter clickedCounter = counters.get(position);

                Intent counterIntent = new Intent(DisplayProject.this, StitchCounterActivity.class);
                counterIntent.putExtra(ViewConstants.SELECTED_COUNTER.getValue(), clickedCounter);
                counterIntent.putExtra(ViewConstants.PARENT_PROJECT.getValue(), project);

                startActivity(counterIntent);
            }
        });

        deleteUrlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDeletingUrl();
            }
        });
    }

    private void toggleDeletingUrl(){
        deletingURL = !deletingURL;
        countersListView.setEnabled(!deletingURL);
        countersListView.setVisibility(deletingURL ? View.GONE : View.VISIBLE);
        countersHeader.setVisibility(deletingURL ? View.GONE : View.VISIBLE);
        newCounterBtn.setEnabled(!deletingURL);
        newUrlBtn.setEnabled(!deletingURL);
    }

    private void init(){
        urlsListView = findViewById(R.id.urls_listview);
        countersListView = findViewById(R.id.counters_listview);
        backBtn = findViewById(R.id.display_projs_back_btn);
        newCounterBtn = findViewById(R.id.add_counter_to_proj_btn);
        newUrlBtn = findViewById(R.id.add_url_to_proj_btn);
        deleteUrlBtn = findViewById(R.id.delete_url_fab);
        countersHeader = findViewById(R.id.counters_header_text);
        setAdapters();
        setListeners();
    }

    @Override
    public void dismissFragment() {
        countersListView.setVisibility(View.VISIBLE);
        urlsListView.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.VISIBLE);
        newCounterBtn.setVisibility(View.VISIBLE);
        deleteUrlBtn.setVisibility(View.VISIBLE);
        newUrlBtn.setVisibility(View.VISIBLE);

        getSupportFragmentManager().beginTransaction().
                remove(getSupportFragmentManager().findFragmentById(R.id.add_url_fragment_container)).commit();
    }

    @Override
    public void createNewUrl(Url newUrl){
        UrlHandler urlHandler = new UrlHandler();

        urlHandler.createNewUrl(newUrl, project)
                .thenAccept(success -> {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(success){
                                setAdapters();
                                dismissFragment();
                            }else{
                                Log.w(TAG, "Something went wrong when creating the new URL");
                            }
                        }
                    });
                })
                .exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {
                        Log.e(TAG, "ERROR", throwable);
                        return null;
                    }
                });
    }
}
