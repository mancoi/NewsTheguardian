package com.example.mancoi.news;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mancoi on 07/09/2017.
 */

public class Search_Activity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private final String HTTP_REQUEST = "https://content.guardianapis.com/search?";
    private final String API_KEY = "3d076462-19d6-4cae-8d80-c3353eee520c";

    private NewsAdapter mAdapter;

    private int LOADER_ID = 1;

    private String mQuery;

    private View rootView;

    // Because the API request with the parameter "q" will return results that
    // have been order by "relevance" by default, we set it to "relevance" at first.
    // So when user performed a search then set the order to "relevance", we don't have
    // to restart the Loader all again
    private String mOrderBy = "relevance";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_activity);

        //Set the toolbar to support actionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Search");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mQuery = query;
        }

        final ListView newsListItem = (ListView) findViewById(R.id.list);

        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        newsListItem.setAdapter(mAdapter);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getSupportLoaderManager();
        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(LOADER_ID, null, this);

        newsListItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News newsExtra = mAdapter.getItem(position);
                assert newsExtra != null;
                String apiUrl = newsExtra.getApiUrl();
                String title = newsExtra.getTitle();
                String author = newsExtra.getAuthor();
                String thumbnail = newsExtra.getImgUrl();
                Intent intent = new Intent(Search_Activity.this, Content_Reader.class);
                //Get the id string of the item's id, then pass it to Sidebar_OnClick_Activity
                intent.putExtra("apiUrl", apiUrl);
                intent.putExtra("headline", title);
                intent.putExtra("byline", author);
                intent.putExtra("thumbnail", thumbnail);
                startActivity(intent);
            }
        });

        rootView = findViewById(R.id.root_layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_button).getActionView();

        //Current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(false); // Expand the widget
        searchView.setQuery(mQuery, false);

        // Calculate the number of pixels according to a given value in the DIP metrics
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, getResources().getDisplayMetrics());
        // Set max width for the SearchView, if we don't do this,
        // when the SearchView expand, it will take up the whole Toolbar's width
        // and hide other menu items
        searchView.setMaxWidth(px);

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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        rootView.requestFocus();

        int itemId = item.getItemId();

        // If user just clicked on the filter icon, no need to do anything.
        if (itemId == R.id.filter_ic)
            return false;

        String itemIdString = getResources().getResourceEntryName(itemId);

        // Check the current order, if user have selected the same order, then we
        // no need to do anything else. Show the Toast message to inform user about that.
        // Else, set the order to what user have selected and restart the Loader
        // to show results with that order
        if (!mOrderBy.equals(itemIdString))
        {
            mOrderBy = itemIdString;
            getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
            Toast.makeText(this, mOrderBy + " results", Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
        }
        else
        {
            Toast.makeText(this, "Showing " + mOrderBy + " results", Toast.LENGTH_SHORT).show();
            return false;
        }
    }



    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {

        Uri baseUri = Uri.parse(HTTP_REQUEST);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("q", mQuery);
        uriBuilder.appendQueryParameter("show-fields", "headline,byline,thumbnail");
        uriBuilder.appendQueryParameter("order-by", mOrderBy);
        uriBuilder.appendQueryParameter("api-key", API_KEY);

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        //Clear the adapter of previous news data
        mAdapter.clear();

        // If there is a valid list of {@link News}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        rootView.requestFocus();
    }
}
