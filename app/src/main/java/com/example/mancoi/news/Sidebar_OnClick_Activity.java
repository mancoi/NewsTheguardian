package com.example.mancoi.news;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mancoi on 23/09/2017.
 */

public class Sidebar_OnClick_Activity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private final String HTTP_REQUEST = "https://content.guardianapis.com/";
    private final String API_KEY = "3d076462-19d6-4cae-8d80-c3353eee520c";
    LoaderManager loaderManager;
    ProgressBar loaddingIndicator;
    private NewsAdapter mAdapter;
    private int LOADER_ID = 1;
    private String mIdToRetrieve;
    private String mGroupId;
    private TextView emptyStateTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_sidebar_onclick);

        final ListView newsListItem = (ListView) findViewById(R.id.list);

        emptyStateTextView = (TextView) findViewById(R.id.empty_state_tv);
        loaddingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);

        mAdapter = new NewsAdapter(Sidebar_OnClick_Activity.this, new ArrayList<News>());
        newsListItem.setAdapter(mAdapter);
        newsListItem.setEmptyView(emptyStateTextView);

        Intent intent = getIntent();
        mIdToRetrieve = intent.getStringExtra("whatToRetrieve");
        mGroupId = intent.getStringExtra("groupID");

        //Set the toolbar to support actionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(intent.getStringExtra("itemName"));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        loaderManager = getSupportLoaderManager();
        if (QueryUtils.hasInternetConnection(Sidebar_OnClick_Activity.this)) {

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(LOADER_ID, null, this);

        } else {
            loaddingIndicator.setVisibility(View.GONE);
            emptyStateTextView.setText(getResources().getString(R.string.no_internet));
        }

        newsListItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News newsExtra = mAdapter.getItem(position);

                QueryUtils.StartIntent(Sidebar_OnClick_Activity.this, newsExtra);
            }
        });

        emptyStateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loaddingIndicator.setVisibility(View.VISIBLE);
                if (QueryUtils.hasInternetConnection(Sidebar_OnClick_Activity.this)) {
                    loaderManager.restartLoader(LOADER_ID, null, Sidebar_OnClick_Activity.this);
                } else {
                    loaddingIndicator.setVisibility(View.GONE);
                    Toast.makeText(Sidebar_OnClick_Activity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {

        Uri baseUri = Uri.parse(HTTP_REQUEST);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        if (mGroupId.equals("edition")) {
            uriBuilder.appendPath(mIdToRetrieve);
        } else {
            uriBuilder.appendPath("search");
            uriBuilder.appendQueryParameter("section", mIdToRetrieve);
        }
        uriBuilder.appendQueryParameter("show-fields", "headline,byline,thumbnail,trailText");
        uriBuilder.appendQueryParameter("show-editors-picks", "true");
        //uriBuilder.appendQueryParameter("order-by", "relevance");
        uriBuilder.appendQueryParameter("api-key", API_KEY);

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        loaddingIndicator.setVisibility(View.GONE);
        //Clear the adapter of previous news data
        mAdapter.clear();

        // If there is a valid list of {@link News}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        } else {
            if (QueryUtils.hasInternetConnection(Sidebar_OnClick_Activity.this)) {
                emptyStateTextView.setText(R.string.found_nothing);
            } else {
                emptyStateTextView.setText(R.string.no_internet);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }
}
