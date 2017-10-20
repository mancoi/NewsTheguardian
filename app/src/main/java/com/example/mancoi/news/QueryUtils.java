package com.example.mancoi.news;

/**
 * Created by mancoi on 24/08/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving Earthquake data from USGS.
 */
public final class QueryUtils extends FragmentActivity {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();


    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the News dataset and return an {@link News} object to represent a single News.
     */
    public static List<News> fetchNewsData(String requestURL) {

        String jsonResponse = null;

        // Create URL object
        URL url = createURL(requestURL);

        // Perform HTTP request to the URL and receive a JSON response back
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing inputStream", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link News}s
        // Return the list of {@link News}s
        return extractNewsFromJson(jsonResponse);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createURL(String stringURL) {
        URL url = null;
        try {
            url = new URL(stringURL);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("Utf-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();

            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }

        return output.toString();
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<News> extractNewsFromJson(String newsJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        //Create empty ArrayList to insert news
        List<News> newses = new ArrayList<News>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            //Create JSON object from the response String
            JSONObject root = new JSONObject(newsJSON);

            //Get the response JSONObject
            JSONObject response = root.getJSONObject("response");

            int total = response.getInt("total");
            if (total == 0) {
                return null;
            }
            //Get the results JSONArray
            //If it not exits, it because the array return is the result of what we search,
            //so get it
            JSONArray results = response.optJSONArray("mostViewed");
            if (results == null) {
                results = response.optJSONArray("results");

                if (results == null) {
                    results = response.optJSONArray("editorsPicks");
                }
            }

            for (int i = 0; i < results.length(); i++) {
                // Get a single news at position i within the list of newses
                JSONObject currentNews = results.getJSONObject(i);

                //Get the name of the section
                String section = currentNews.getString("sectionName");

                //Get the public date
                String date = currentNews.getString("webPublicationDate");

                //Get the webUrl
                String apiUrl = currentNews.getString("apiUrl");

                //Get the fields object that contain headline and byline
                JSONObject fields = currentNews.getJSONObject("fields");

                //Get the headline
                String headline = fields.getString("headline");
                //Get the byline
                //If there is no byline, then it will be set to null
                String byline = fields.optString("byline");
                //Get the trailText
                String trailText = fields.optString("trailText");

                //Get the thumbnail link
                String thumbnail = fields.optString("thumbnail");

                //Add what we just got to the newses ArrayList
                newses.add(new News(headline, byline, trailText, date, section, apiUrl, thumbnail));

            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the Earthquake JSON results", e);
        }

        // Return the list of newses
        return newses;
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    static void StartIntent(Context context, News newsExtra) {
        assert newsExtra != null;

        String apiUrl = newsExtra.getUrl();
        String title = newsExtra.getTitle();
        String author = newsExtra.getAuthor();
        String thumbnail = newsExtra.getImgUrl();
        String trailText = newsExtra.getTrailText();

        Intent intent = new Intent(context, Content_Reader.class);

        //Get the id string of the item's id, then pass it to Content_Reader Activity
        intent.putExtra("apiUrl", apiUrl);
        intent.putExtra("headline", title);
        intent.putExtra("byline", author);
        intent.putExtra("thumbnail", thumbnail);
        intent.putExtra("trailText", trailText);
        intent.putExtra("thumbnail", thumbnail);

        context.startActivity(intent);
    }

    static boolean hasInternetConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    static void setUpOnEmptyStateTextViewClick(
            Context context
            , Activity activity
    ) {
        if (hasInternetConnection(context)) {

            ViewPager viewPage = activity.findViewById(R.id.viewpager);
            viewPage.getAdapter().notifyDataSetChanged();
        }
    }
}
