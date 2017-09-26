package com.example.mancoi.news;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by mancoi on 25/09/2017.
 */

public class Content_Reader extends AppCompatActivity {

    private String mApiUrl= "";
    private final String API_KEY = "3d076462-19d6-4cae-8d80-c3353eee520c";

    private TextView mContentTextView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Let's display the progress in the activity title bar, like the
        // browser app does.
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.content_reader);

        //Set the toolbar to support actionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //setContentView(R.layout.content_reader);
        final WebView webview = (WebView) findViewById(R.id.content_wv);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        mApiUrl = intent.getStringExtra("apiUrl");

        webview.loadUrl(mApiUrl);

        webview.getSettings().setJavaScriptEnabled(true);

        final Activity activity = this;

        webview.setWebChromeClient(new WebChromeClient(){

            public void onProgressChanged(WebView view, int progress) {
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_indicator);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setIndeterminate(false);
                progressBar.setProgress(progress);
                if(progress == 100)
                    activity.setTitle(webview.getTitle());
            }
        });

    }
}
