package com.example.stitcher.views;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.stitcher.R;
import com.example.stitcher.constants.ViewConstants;
import com.example.stitcher.controllers.CounterCollection;
import com.example.stitcher.controllers.NotesCollection;
import com.example.stitcher.controllers.UrlCollection;
import com.example.stitcher.controllers.array_adapters.CounterArrayAdapter;
import com.example.stitcher.controllers.array_adapters.NotesArrayAdapter;
import com.example.stitcher.controllers.array_adapters.UrlsArrayAdapter;
import com.example.stitcher.controllers.handlers.NotesHandler;
import com.example.stitcher.controllers.handlers.ProjectHandler;
import com.example.stitcher.controllers.handlers.UrlHandler;
import com.example.stitcher.models.Counter;
import com.example.stitcher.models.Notes;
import com.example.stitcher.models.Project;
import com.example.stitcher.models.Url;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DisplayProject extends AppCompatActivity implements EnterTextFragment.EnterTextFragmentHandler,
StatusesFragment.StatusesFragmentHandler, NotesFragment.NotesFragmentHandler {
    private UrlsArrayAdapter urlsArrayAdapter;
    private CounterArrayAdapter counterArrayAdapter;
    private ListView urlsListView;
    private ListView countersListView;
    private ArrayList<Counter> counters;
    private ArrayList<Url> urls;
    private ArrayList<Notes> notes;
    private Project project;
    private Button backBtn;
    private Button newCounterBtn;
    private Button newUrlBtn;
    private Button statusBtn;
    private Button notesBtn;
    private FloatingActionButton deleteUrlBtn;
    private TextView countersHeader;
    private TextView urlsHeader;
    private TextView titleTxtView;
    private boolean deletingURL;
    private FrameLayout fragmentFrame;

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

        if(project.getUrlIds().size()==0){
            urls = new ArrayList<>();
            urlsArrayAdapter = new UrlsArrayAdapter(displayProject, urls);
            urlsListView.setAdapter(urlsArrayAdapter);
        }else{
            UrlCollection.getInstance().getUrlsWithIds(project.getUrlIds())
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

        if(project.getNotesIds().size()==0){
            notes = new ArrayList<>();
        }else{
            NotesCollection.getInstance().getNotesWithIds(project.getNotesIds())
                    .thenAccept(newNotes -> {
                        runOnUiThread(() -> notes = newNotes);
                    }).exceptionally(throwable -> {
                        Log.w(TAG, throwable.getMessage());
                        return null;
                    });
        }

        CounterCollection.getInstance().getCountersWithIds(project.getCounterIds())
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

        notesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentVisible();

                fragmentFrame.setVisibility(View.VISIBLE);

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(ViewConstants.NOTES_FIELD.getValue(), notes);
                bundle.putParcelable(ViewConstants.PARENT_PROJECT.getValue(), project);
                NotesFragment fragment = new NotesFragment();
                fragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.display_project_fragment_container, NotesFragment.class, null)
                        .replace(R.id.display_project_fragment_container, fragment, null)
                        .commit();
            }
        });

        statusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentVisible();

                fragmentFrame.setVisibility(View.VISIBLE);

                Bundle bundle = new Bundle();
                StatusesFragment fragment = new StatusesFragment();
                fragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.display_project_fragment_container, StatusesFragment.class, null)
                        .replace(R.id.display_project_fragment_container, fragment, null)
                        .commit();
            }
        });

        newUrlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentVisible();

                Bundle bundle = new Bundle();
                bundle.putInt(ViewConstants.FRAGMENT_ERROR_MSG.getValue(), R.string.url_error_msg);
                bundle.putInt(ViewConstants.FRAGMENT_HINT_MSG.getValue(), R.string.add_url_txt_msg);
                EnterTextFragment fragment = new EnterTextFragment();
                fragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.add_url_fragment_container, EnterTextFragment.class, null)
                        .replace(R.id.add_url_fragment_container, fragment, null)
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
                    urlIntent.putParcelableArrayListExtra(ViewConstants.FRAGMENT_PROJECT_COUNTERS.getValue(), counters);

                    startActivity(urlIntent);
                }else{
                    UrlHandler.deleteUrl(clickedUrl, project)
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

    private void fragmentVisible(){
        countersListView.setVisibility(View.GONE);
        urlsListView.setVisibility(View.GONE);
        backBtn.setEnabled(false);
        newCounterBtn.setEnabled(false);
        countersHeader.setVisibility(View.GONE);
        urlsHeader.setVisibility(View.GONE);
        deleteUrlBtn.setVisibility(View.GONE);
        newUrlBtn.setEnabled(false);
        statusBtn.setEnabled(false);
        notesBtn.setEnabled(false);
    }

    private void toggleDeletingUrl(){
        deletingURL = !deletingURL;
        countersListView.setEnabled(!deletingURL);
        countersListView.setVisibility(deletingURL ? View.GONE : View.VISIBLE);
        countersHeader.setVisibility(deletingURL ? View.GONE : View.VISIBLE);
        newCounterBtn.setEnabled(!deletingURL);
        newUrlBtn.setEnabled(!deletingURL);
        statusBtn.setEnabled(!deletingURL);
        notesBtn.setEnabled(!deletingURL);
    }

    private void init(){
        urlsListView = findViewById(R.id.urls_listview);
        countersListView = findViewById(R.id.counters_listview);
        backBtn = findViewById(R.id.display_projs_back_btn);
        newCounterBtn = findViewById(R.id.add_counter_to_proj_btn);
        newUrlBtn = findViewById(R.id.add_url_to_proj_btn);
        deleteUrlBtn = findViewById(R.id.delete_url_fab);
        countersHeader = findViewById(R.id.counters_header_text);
        urlsHeader = findViewById(R.id.urls_header_txt);
        statusBtn = findViewById(R.id.project_status_button);
        notesBtn = findViewById(R.id.project_notes_button);
        titleTxtView = findViewById(R.id.title_txtView);
        fragmentFrame = findViewById(R.id.display_project_fragment_frame);

        statusBtn.setText(project.getStatus());
        titleTxtView.setText(project.getName());
        fragmentFrame.setVisibility(View.GONE);

        setAdapters();
        setListeners();
    }

    @Override
    public void dismissFragment() {
        countersListView.setVisibility(View.VISIBLE);
        urlsListView.setVisibility(View.VISIBLE);
        backBtn.setEnabled(true);
        newCounterBtn.setEnabled(true);
        deleteUrlBtn.setVisibility(View.VISIBLE);
        newUrlBtn.setEnabled(true);
        statusBtn.setEnabled(true);
        notesBtn.setEnabled(true);
        countersHeader.setVisibility(View.VISIBLE);
        urlsHeader.setVisibility(View.VISIBLE);
        fragmentFrame.setVisibility(View.GONE);

        ArrayList<Fragment> fragments = (ArrayList<Fragment>) getSupportFragmentManager().getFragments();

        for(int i = 0; i < fragments.size(); i++) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragments.get(i)).commit();
        }
    }

    @Override
    public void createNew(String input){
        Url newUrl = new Url(UUID.randomUUID().toString(), input);

        UrlHandler.createNewUrl(newUrl, project)
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

    public void statusChosen(String status){
        ProjectHandler.updateProjectStatus(project, status)
                .thenAccept(success ->
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(success){
                                    statusBtn.setText(status);
                                    dismissFragment();
                                }else{
                                    Log.e(TAG, "Something went wrong when updating the status");
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

    @Override
    public void closed() {
        dismissFragment();
    }

    @Override
    public void noteCreated(String noteTitle, String noteBody) {

        NotesHandler.createNewNote(noteBody, noteTitle, project)
                .thenAccept(note ->
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissFragment();
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
                                dismissFragment();
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
}
