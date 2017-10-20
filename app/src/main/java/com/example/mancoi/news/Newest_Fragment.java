package com.example.mancoi.news;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Newest_Fragment extends Fragment implements LoaderManager.LoaderCallbacks<List<News>> {

    View rootView;
    TextView emptyStateTextView;
    ProgressBar loaddingIndicator;
    private NewsAdapter mAdapter;
    private int NEWEST_LOADER_ID = 1;

    public Newest_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.news_main_list, container, false);

        final ListView newsListItem = rootView.findViewById(R.id.list);

        emptyStateTextView = rootView.findViewById(R.id.empty_state_tv);
        loaddingIndicator = rootView.findViewById(R.id.loading_indicator);

        mAdapter = new NewsAdapter(getContext(), new ArrayList<News>());
        newsListItem.setAdapter(mAdapter);
        newsListItem.setEmptyView(emptyStateTextView);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();
        if (QueryUtils.hasInternetConnection(getContext())) {

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWEST_LOADER_ID, null, this);

        } else {
            loaddingIndicator.setVisibility(View.GONE);
            emptyStateTextView.setText(getResources().getString(R.string.no_internet));
        }


        newsListItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                News newsExtra = mAdapter.getItem(position);
                QueryUtils.StartIntent(getContext(), newsExtra);
            }
        });


        emptyStateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (QueryUtils.hasInternetConnection(getContext())) {
                    QueryUtils.setUpOnEmptyStateTextViewClick(
                            getContext()
                            , getActivity()
                    );
                } else {
                    Toast.makeText(getContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        String NEWS_HTTP_REQUEST =
                "http://content.guardianapis.com/search?show-fields=headline,byline,thumbnail,trailText&api-key=3d076462-19d6-4cae-8d80-c3353eee520c";
        return new NewsLoader(getContext(), NEWS_HTTP_REQUEST);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {

        getLoaderManager().destroyLoader(NEWEST_LOADER_ID);

        loaddingIndicator.setVisibility(View.GONE);

        //Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid data, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }

    // If this Activity resume after lost network connection, at that time
    // the adapter will have cleared already, so we peform a reload
    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter == null || mAdapter.isEmpty()) {
            loaddingIndicator.setVisibility(View.VISIBLE);
            getLoaderManager().restartLoader(NEWEST_LOADER_ID, null, Newest_Fragment.this);
        }
    }
}
