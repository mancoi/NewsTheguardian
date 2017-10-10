package com.example.mancoi.news;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by mancoi on 25/09/2017.
 */

public class Content_Reader extends AppCompatActivity {

    private final String HTTP_REQUEST_PARAM =
            "?shouldHideAdverts=true&show-fields=thumbnail,trailText,body&show-elements=image&api-key=3d076462-19d6-4cae-8d80-c3353eee520c";
    private WebView webview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.read_content);

        //webview = new WebView(this);

        //Set the toolbar to support actionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Article");
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        String mApiUrl = intent.getStringExtra("apiUrl");
        mApiUrl += HTTP_REQUEST_PARAM;
        //Execute the AsyncTask to retrieve data
        NewsAsyncTask newsAsyncTask = new NewsAsyncTask();
        newsAsyncTask.execute(mApiUrl);

        //Get the title and set it to headline_tv
        TextView title = (TextView) findViewById(R.id.headline_tv);
        title.setText(intent.getStringExtra("headline"));
        //Get the author and set it to byline_tv
        TextView byline = (TextView) findViewById(R.id.byline_tv);
        byline.setText(intent.getStringExtra("byline"));

        //Get the thumbnail's Url, load it and set it to thumbnail_imgView
        ImageView thumbnail = (ImageView) findViewById(R.id.thumbnail_imgView);
        String thumbnailUrl = intent.getStringExtra("thumbnail");
        //Check if there is an image or not, it not, leave it as default
        //Not show broken image here because it's not an error
        if (!TextUtils.isEmpty(thumbnailUrl)) {
            // Display the thumbnail of the current News in that TextView
            Picasso.with(this)
                    .load(thumbnailUrl)
                    .error(R.drawable.img_broken)
                    .placeholder(R.drawable.img_holder)
                    .into(thumbnail);
        } else {
            thumbnail.setVisibility(View.GONE);
        }

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
                TextView trailText = (TextView) findViewById(R.id.trailText_tv);
                trailText.setText(QueryContent.fromHtml(data.getTrailText(),null));

                TextView body = (TextView) findViewById(R.id.body_tv);

                PicassoImageGetter imageGetter = new PicassoImageGetter(body);
                body.setText(QueryContent.fromHtml(data.getBody(), imageGetter));
//                webview = (WebView) findViewById(R.id.body_wv);
//                webview.loadData(data.getBody(), "text/html", null);

            }

        }

    }


}
