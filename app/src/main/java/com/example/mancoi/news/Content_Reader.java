package com.example.mancoi.news;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import static android.R.attr.description;

/**
 * Created by mancoi on 25/09/2017.
 */

public class Content_Reader extends AppCompatActivity {

    private final String API_KEY = "3d076462-19d6-4cae-8d80-c3353eee520c";
    private String mApiUrl = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_reader);

        //Set the toolbar to support actionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setContentView(R.layout.content_reader);
        final WebView webview = (WebView) findViewById(R.id.content_wv);
        webview.getSettings().setJavaScriptEnabled(true);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        mApiUrl = intent.getStringExtra("apiUrl");

        final Activity activity = this;
        activity.setTitle("Article");

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_indicator);
        progressBar.setIndeterminate(false);

        webview.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int progress) {

                progressBar.setProgress(progress);

                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                    activity.setTitle(webview.getTitle());
                }
            }
        });


        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });
        webview.loadUrl(mApiUrl);
    }

}
