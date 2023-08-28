package com.example.stitcher;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CountersActivity extends AppCompatActivity {
    private ListView commentsList;
    private CommentsDataArrayAdapter commentsDataArrayAdapter;
    private ArrayList<Comment> comments;
    private Button addCommentButton;
    private boolean userHasScanned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_counters);

    }

    private void init(){

    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }
}
