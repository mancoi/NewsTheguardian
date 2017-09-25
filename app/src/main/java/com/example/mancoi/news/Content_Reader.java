package com.example.mancoi.news;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * Created by mancoi on 25/09/2017.
 */

public class Content_Reader extends AppCompatActivity {

    private String mApiUrl= "";
    private final String API_KEY = "3d076462-19d6-4cae-8d80-c3353eee520c";

    WebView webview;

    private TextView mContentTextView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.content_reader);
        webview = new WebView(this);
        setContentView(webview);


        mContentTextView = (TextView) findViewById(R.id.content_tv);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        mApiUrl = intent.getStringExtra("apiUrl");

        Uri baseUri = Uri.parse(mApiUrl);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("show-fields", "body");
        uriBuilder.appendQueryParameter("api-key", API_KEY);

        ContentLoaderAsync contentLoaderAsync = new ContentLoaderAsync();
        contentLoaderAsync.execute(uriBuilder.toString());

    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the list of earthquakes in the response.
     *
     * AsyncTask has three generic parameters: the input type, a type used for progress updates, and
     * an output type. Our task will take a String URL, and return an Earthquake. We won't do
     * progress updates, so the second generic is just Void.
     *
     * We'll only override two of the methods of AsyncTask: doInBackground() and onPostExecute().
     * The doInBackground() method runs on a background thread, so it can run long-running code
     * (like network activity), without interfering with the responsiveness of the app.
     * Then onPostExecute() is passed the result of doInBackground() method, but runs on the
     * UI thread, so it can use the produced data to update the UI.
     */
    private class ContentLoaderAsync extends AsyncTask<String, Void, String> {

        /**
         * This method runs on a background thread and performs the network request.
         * We should not update the UI from a background thread, so we return a list of
         * {@link News}s as the result.
         */
        @Override
        protected String doInBackground(String... urls) {

            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            String body = QueryContent.fetchNewsData(urls[0]);
            return body;


        }

        /**
         * This method runs on the main UI thread after the background work has been
         * completed. This method receives as input, the return value from the doInBackground()
         * method. First we clear out the adapter, to get rid of earthquake data from a previous
         * query to USGS. Then we update the adapter with the new list of earthquakes,
         * which will trigger the ListView to re-populate its list items.
         */
        @Override
        protected void onPostExecute(final String data) {

            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                //mContentTextView.setText(Html.fromHtml(data));
                webview.loadData(data, "text/html", null);
            }

        }

    }

}
