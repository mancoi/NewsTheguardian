package com.example.mancoi.news;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Newest_Fragment extends Fragment implements LoaderManager.LoaderCallbacks<List<News>>{

    private String NEWS_HTTP_REQUEST =
            "http://content.guardianapis.com/search?show-fields=headline,byline,thumbnail&api-key=3d076462-19d6-4cae-8d80-c3353eee520c";

    private NewsAdapter mAdapter;

    private int NEWEST_LOADER_ID = 1;

    View rootView;

    public Newest_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.news_main_list, container, false);

        final ListView newsListItem = (ListView) rootView.findViewById(R.id.list);

        mAdapter = new NewsAdapter(getContext(), new ArrayList<News>());
        newsListItem.setAdapter(mAdapter);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();
        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(NEWEST_LOADER_ID, null, this);





        return rootView;
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        return new NewsLoader(getContext(), NEWS_HTTP_REQUEST);
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
