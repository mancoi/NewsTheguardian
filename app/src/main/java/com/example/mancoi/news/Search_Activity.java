package com.example.mancoi.news;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mancoi on 07/09/2017.
 */

public class Search_Activity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private final String HTTP_REQUEST = "https://content.guardianapis.com/search?";
    private final String API_KEY = "3d076462-19d6-4cae-8d80-c3353eee520c";

    private NewsAdapter mAdapter;

    private int INTERNATIONAL_LOADER_ID = 1;

    private String mQuery;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        //Set the toolbar to support actionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            TextView tv = (TextView) findViewById(R.id.tv_search_query);
            tv.setText(query);

            mQuery = query;
        }

        final ListView newsListItem = (ListView) findViewById(R.id.list_search_result);

        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        newsListItem.setAdapter(mAdapter);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getSupportLoaderManager();
        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(3, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_button).getActionView();

        //Current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(false); // expand the widget
        searchView.clearFocus(); //Don't focus to the SearchView when user not want it so

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                // Clear this current activity from the task stack and perform the search,
                // because the search will create new activity, so we call finish() every time
                // user perform the search to do that,
                // then after the user has searched for multiple time and press the BACK button,
                // we go to the main activity directly but not the search activity before
                finish();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {

        Uri baseUri = Uri.parse(HTTP_REQUEST);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("q", mQuery);
        uriBuilder.appendQueryParameter("show-fields", "headline,byline,thumbnail");
        uriBuilder.appendQueryParameter("order-by", "relevance");
        uriBuilder.appendQueryParameter("api-key", API_KEY);

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        //Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }

}
