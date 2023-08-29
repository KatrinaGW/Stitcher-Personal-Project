package com.example.stitcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stitcher.models.Project;
import com.example.stitcher.models.Url;

public class UrlWebviewActivity extends AppCompatActivity {
    WebView webview;
    Url url;
    Project parentProject;
    Button backBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);

        Intent intent = getIntent();
        url = intent.getParcelableExtra(ViewConstants.SELECTED_URL.getValue());
        parentProject = intent.getParcelableExtra(ViewConstants.PARENT_PROJECT.getValue());

        backBtn = findViewById(R.id.webview_back_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent projectIntent = new Intent(UrlWebviewActivity.this, DisplayProject.class);
                projectIntent.putExtra(ViewConstants.SELECTED_PROJECT.getValue(), parentProject);
                startActivity(projectIntent);
            }
        });

        webview = (WebView) findViewById(R.id.myWebView);
        //next line explained below
        webview.setWebViewClient(new MyWebViewClient(this));
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(url.getUrl());
    }
}
