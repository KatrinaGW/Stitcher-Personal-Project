package com.example.stitcher;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stitcher.models.Url;

public class UrlWebviewActivity extends AppCompatActivity {
    WebView webview;
    Url url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);

        Intent intent = getIntent();
        url = intent.getParcelableExtra("selectedUrl");

        webview = (WebView) findViewById(R.id.myWebView);
        //next line explained below
        webview.setWebViewClient(new MyWebViewClient(this));
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(url.getUrl());
    }
}
