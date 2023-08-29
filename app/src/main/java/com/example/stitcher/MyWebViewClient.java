package com.example.stitcher;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class MyWebViewClient extends WebViewClient {
    public MyWebViewClient(AppCompatActivity activity) {
        super();
        //start anything you need to
    }

    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        //Do something to the urls, views, etc.
    }
}
