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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by mancoi on 25/09/2017.
 */

public class Content_Reader extends AppCompatActivity {

    private CustomTabsClient mClient;
    private CustomTabsServiceConnection connection;
    private WebView mWebview;

    private String url;

    private final String HTTP_REQUEST_PARAM =
            "?shouldHideAdverts=true&show-fields=main,body&show-elements=all&api-key=3d076462-19d6-4cae-8d80-c3353eee520c";

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
        final String webUrl = intent.getStringExtra("apiUrl");

//        if (mClient != null)
//        {
//            // With a valid mClient. Set the Url that user may launch
//            CustomTabsSession session = mClient.newSession(new CustomTabsCallback());
//            session.mayLaunchUrl(Uri.parse(webUrl), null, null);
//        }
//        else
//        {
//
//        }

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
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setMixedContentMode(mWebview.getSettings().MIXED_CONTENT_COMPATIBILITY_MODE);
        mWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                //Open Link in a Custom Tab
                Uri uri = Uri.parse(url);

                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));

                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(Content_Reader.this, uri);

                return true;

            }


        });

        final Button butReadmore = (Button) findViewById(R.id.read_more_but);
        butReadmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (mClient != null)
//                {
//                    // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
//                    // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
//                    // and launch the desired Url with CustomTabsIntent.launchUrl()
//                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
//                    builder.setToolbarColor(ContextCompat.getColor(Content_Reader.this, R.color.colorPrimary));
//                    CustomTabsIntent customTabsIntent = builder.build();
//                    customTabsIntent.launchUrl(Content_Reader.this, Uri.parse(webUrl));
//
//                    finish();
//                }
//                else
//                {
                String apiUrl = webUrl + HTTP_REQUEST_PARAM;
                url = apiUrl;
                butReadmore.setVisibility(View.GONE);
                mWebview.setVisibility(View.VISIBLE);
                NewsAsyncTask newsAsyncTask = new NewsAsyncTask();
                newsAsyncTask.execute(apiUrl);
//                }

            }
        });

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
    private class NewsAsyncTask extends AsyncTask<String, Void, NewsContent> {

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

            NewsContent newsContent = QueryContent.fetchNewsData(urls[0]);
            return newsContent;


        }

        /**
         * This method runs on the main UI thread after the background work has been
         * completed. This method receives as input, the return value from the doInBackground()
         * method. First we clear out the adapter, to get rid of earthquake data from a previous
         * query to USGS. Then we update the adapter with the new list of earthquakes,
         * which will trigger the ListView to re-populate its list items.
         */
        @Override
        protected void onPostExecute(final NewsContent data) {

            // If there is a valid list of {@link News}s, then add them to the adapter's
            // data set. This will trigger the content to update.
            if (data != null) {

                //PicassoImageGetter imageGetter = new PicassoImageGetter(body);
                TextView body = (TextView) findViewById(R.id.body_tv);

//                PicassoImageGetter imageGetter = new PicassoImageGetter(body);
//                body.setText(QueryContent.fromHtml(data.getContent(), imageGetter));

                //mWebview.loadData(data.getContent(), "text/html", null);
                String dataa = data.getContent();
                //mWebview.clearCache(true);
                mWebview.loadDataWithBaseURL(
                        null,

                        "<style>"
                                + "img{display: inline;height: auto;max-width: 100%;}"
                                + "iframe{width: 100%}"
                                + "</style>"
                                + dataa,

                        "text/html",
                        "UTF-8",
                        null
                );

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Package name for the Chrome channel the client wants to connect to. This
        // depends on the channel name.
        // Stable = com.android.chrome
        // Beta = com.chrome.beta
        // Dev = com.chrome.dev
        final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";  // Change when in stable

        connection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
                mClient = client;
                mClient.warmup(0);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mClient = null;
            }
        };

        CustomTabsClient.bindCustomTabsService(this, CUSTOM_TAB_PACKAGE_NAME, connection);

    }

    @Override
    protected void onPause() {

        super.onPause();
        this.unbindService(connection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebview != null) {
            ViewGroup viewGroup = (ViewGroup) mWebview.getParent();
            viewGroup.removeAllViews();
            mWebview.onPause();
            mWebview.removeAllViews();
            mWebview.destroyDrawingCache();
            mWebview.destroy();
            mWebview = null;
        }
    }
}
