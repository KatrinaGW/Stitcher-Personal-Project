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

import com.example.stitcher.controllers.UrlCollection;
import com.example.stitcher.controllers.array_adapters.UrlsArrayAdapter;
import com.example.stitcher.models.DatabaseObject;
import com.example.stitcher.models.Url;

import java.util.ArrayList;

public class DisplayUrlsActivity extends AppCompatActivity {
    Button backBtn;
    ArrayList<Url> urls;
    UrlsArrayAdapter urlsArrayAdapter;
    ListView urlsListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_projects);

    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void setListeners(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DisplayUrlsActivity.this, MainActivity.class));
            }
        });

        urlsListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Url clickedUrl = urls.get(position);

                Intent urlIntent = new Intent(DisplayUrlsActivity.this, UrlWebviewActivity.class);
                urlIntent.putExtra("selectedUrl", clickedUrl);

                startActivity(urlIntent);
            }
        });
    }

    private void init(){
        urlsListview = findViewById(R.id.strings_listview);
        setUrlsArrayAdapter();
        setListeners();
    }

    private void setUrlsArrayAdapter() {
        DisplayUrlsActivity urlsActivity = this;

        if(urls != null){
            urls.clear();
        }else{
            urls = new ArrayList<>();
        }

        UrlCollection urlsCollection = new UrlCollection();

        urlsCollection.getAll()
                .thenAccept(newUrls -> {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(DatabaseObject url : newUrls){
                                urls.add((Url) url);
                            }
                            urlsArrayAdapter = new UrlsArrayAdapter(urlsActivity, urls);
                            urlsListview.setAdapter(urlsArrayAdapter);
                        }
                    });
                })
                .exceptionally(throwable -> {
                    Log.w(TAG, throwable.getMessage());
                    return null;
                });
    }
}
