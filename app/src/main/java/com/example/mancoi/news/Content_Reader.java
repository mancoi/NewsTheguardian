package com.example.mancoi.news;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by mancoi on 25/09/2017.
 */

public class Content_Reader extends AppCompatActivity {

    //Set up some variables for Chrome Custom Tab
    CustomTabsClient mClient;
    CustomTabsSession mCustomTabsSession;
    CustomTabsServiceConnection mCustomTabsServiceConnection;
    CustomTabsIntent customTabsIntent;

    private WebView mWebview;

    // Make the newsAsyncTask as Global variable because we need to cancel it when this activity
    // pause
    private NewsAsyncTask newsAsyncTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.read_content);

        //Set the toolbar to support actionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Article");
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();

        String apiUrl = intent.getStringExtra("apiUrl");

        apiUrl += "?shouldHideAdverts=true&show-fields=main,body&show-elements=all&api-key=3d076462-19d6-4cae-8d80-c3353eee520c";

        newsAsyncTask = new NewsAsyncTask();
        newsAsyncTask.execute(apiUrl);

        //Get the title and set it to headline_tv
        TextView title = (TextView) findViewById(R.id.headline_tv);
        title.setText(intent.getStringExtra("headline"));

        //Get the author and set it to byline_tv
        TextView byline = (TextView) findViewById(R.id.byline_tv);
        String author = intent.getStringExtra("byline");
        //If there are no author(s) present, hide this TextView
        if (!TextUtils.isEmpty(author)) {
            byline.setText(author);
        } else {
            byline.setVisibility(View.GONE);
        }

        //Get the trailText and set it to trailText_tv
        TextView trailText = (TextView) findViewById(R.id.trailText_tv);
        trailText.setText(QueryUtils.fromHtml(intent.getStringExtra("trailText")));

        //Get the thumbnail's Url, load it and set it to thumbnail_imgView
        ImageView thumbnail = (ImageView) findViewById(R.id.thumbnail_imgView);
        String thumbnailUrl = intent.getStringExtra("thumbnail");
        //Check if there is an image or not, it not, leave it as default
        //Not show broken image here because it's not an error
        if (!TextUtils.isEmpty(thumbnailUrl)) {

            // Display the thumbnail of the current News in that TextView
            Picasso.with(this)
                    .load(thumbnailUrl)
                    .into(thumbnail);

        } else {

            thumbnail.setVisibility(View.GONE);

        }

        // Find the WebView in case of user not installed Chrome
        mWebview = (WebView) findViewById(R.id.content_wv);

        // This should be set to true because we have content that require Javascript to play,
        // such as Youtube Embedded Video
        mWebview.getSettings().setJavaScriptEnabled(true);

        mWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            /*
            The WebView should let user choose what app to send E-mail when
            that user click on a mailto: link.
            This WebView also should open the other in Chrome Custom Tab for better user experience
             */
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                // When user click on a mailto: link, send an implicitly intent
                // With this, the application will ask user what mail application should handle it.
                if (url.startsWith("mailto:")) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                } else {
                    //Open Link in a Custom Tab
                    Uri uri = Uri.parse(url);
                    customTabsIntent.launchUrl(Content_Reader.this, uri);
                }
                return true;
            }

        });

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_indicator);
        progressBar.setIndeterminate(false);

        mWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        final Button butReadMore = (Button) findViewById(R.id.read_more_but);
        butReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                butReadMore.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                mWebview.setVisibility(View.VISIBLE);

            }
        });

        //----------Install And Optimize Chrome Custom Tab---------------------------------------//

        // Package name for the Chrome channel the client wants to connect to. This
        // depends on the channel name.
        // Stable = com.android.chrome
        // Beta = com.chrome.beta
        // Dev = com.chrome.dev
        final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";  // Change when in stable

        mCustomTabsServiceConnection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
                mClient = client;
                mClient.warmup(0);
                mCustomTabsSession = mClient.newSession(null);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mClient = null;
            }
        };

        CustomTabsClient.bindCustomTabsService(Content_Reader.this, CUSTOM_TAB_PACKAGE_NAME, mCustomTabsServiceConnection);
        customTabsIntent = new CustomTabsIntent
                .Builder(mCustomTabsSession)
                .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setShowTitle(true)
                .build();

        //------------------------------DONE-----------------------------------------------------//

    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the list of earthquakes in the response.
     * <p>
     * AsyncTask has three generic parameters: the input type, a type used for progress updates, and
     * an output type. Our task will take a String URL, and return an Earthquake. We won't do
     * progress updates, so the second generic is just Void.
     * <p>
     * We'll only override two of the methods of AsyncTask: doInBackground() and onPostExecute().
     * The doInBackground() method runs on a background thread, so it can run long-running code
     * (like network activity), without interfering with the responsiveness of the app.
     * Then onPostExecute() is passed the result of doInBackground() method, but runs on the
     * UI thread, so it can use the produced data to update the UI.
     */
    private class NewsAsyncTask extends AsyncTask<String, Integer, NewsContent> {

        /**
         * This method runs on a background thread and performs the network request.
         * We should not update the UI from a background thread, so we return a list of
         * {@link NewsContent}s as the result.
         */
        @Override
        protected NewsContent doInBackground(String... urls) {

            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            return QueryContent.fetchNewsData(urls[0]);

        }

        /**
         * This method runs on the main UI thread after the background work has been
         * completed. This method receives as input, the return value from the doInBackground()
         * method. Then we make the WebView load that content
         */
        @Override
        protected void onPostExecute(final NewsContent data) {

            // If there is a valid list of {@link News}s, then add them to the adapter's
            // data set. This will trigger the content to update.
            if (data != null) {
                mWebview.loadDataWithBaseURL(
                        null,
                        // Style for the <img> and the <iframe> tag
                        "<style>"
                                + "img{display: inline;height: auto;max-width: 100%;}"
                                + "iframe{width: 100%}"
                                + "</style>"
                                + data.getContent(),

                        "text/html",
                        "UTF-8",
                        null
                );

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // Cancel the AsyncTask when user leave this Activity or the App will crash
        if (!newsAsyncTask.isCancelled()) {
            newsAsyncTask.cancel(true);
        }

        finish();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        // Destroy the WebView so we won't get memory leak
        if (mWebview != null) {
            ViewGroup viewGroup = (ViewGroup) mWebview.getParent();
            viewGroup.removeAllViews();
            mWebview.onPause();
            mWebview.removeAllViews();
            mWebview.destroyDrawingCache();
            mWebview.destroy();
            mWebview = null;
        }

        //Unbind the ServiceConnection to not get memory leak
        this.unbindService(mCustomTabsServiceConnection);
    }

    @Override
    public boolean onNavigateUp() {

        // Cancel the AsyncTask when user leave this Activity or the App will crash
        if (!newsAsyncTask.isCancelled()) {
            newsAsyncTask.cancel(true);
        }

        finish();

        return super.onNavigateUp();
    }

}
