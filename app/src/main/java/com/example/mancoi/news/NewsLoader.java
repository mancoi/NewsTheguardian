package com.example.mancoi.news;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

/**
 * Created by mancoi on 27/08/2017.
 */

class NewsLoader extends AsyncTaskLoader<List<News>> {

    private String mUrl;

    NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {

        //Don't perform the request if the URL is null
        if (mUrl == null) {
            return null;
        }

        return QueryUtils.fetchNewsData(mUrl);
    }
}
