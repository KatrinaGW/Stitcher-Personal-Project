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

import com.example.stitcher.controllers.CounterCollection;
import com.example.stitcher.controllers.array_adapters.CounterArrayAdapter;
import com.example.stitcher.models.Counter;
import com.example.stitcher.models.DatabaseObject;

import java.util.ArrayList;

public class CountersActivity extends AppCompatActivity {
    private ListView countersListView;
    private CounterArrayAdapter counterArrayAdapter;
    private ArrayList<Counter> counters;
    private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_projects);

    }

    private void setCountersArrayAdapter() {
        CountersActivity countersActivity = this;

        if(counters != null){
            counters.clear();
        }else{
            counters = new ArrayList<>();
        }

        CounterCollection counterCollectionConnection = new CounterCollection();

        counterCollectionConnection.getAll()
                .thenAccept(newCounters -> {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(DatabaseObject counter : newCounters){
                                counters.add((Counter) counter);
                            }
                            counterArrayAdapter = new CounterArrayAdapter(countersActivity, counters);
                            countersListView.setAdapter(counterArrayAdapter);
                        }
                    });
                })
                .exceptionally(throwable -> {
                    Log.w(TAG, throwable.getMessage());
                    return null;
                });
    }

    private void setButtons(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CountersActivity.this, MainActivity.class));
            }
        });
    }

    private void setOnItemClickedListener(){
        countersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Counter clickedCounter = counters.get(position);

                Intent counterIntent = new Intent(CountersActivity.this, StitchCounterActivity.class);
                counterIntent.putExtra("selectedCounter", clickedCounter);

                startActivity(counterIntent);
            }
        });
    }

    private void init(){
        countersListView = findViewById(R.id.strings_listview);
        setCountersArrayAdapter();
        setButtons();
        setOnItemClickedListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }
}
