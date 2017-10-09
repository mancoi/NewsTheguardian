package com.example.mancoi.news;

import android.support.annotation.NonNull;

/**
 * Created by mancoi on 09/10/2017.
 */

public class NewsContent {

    private String mTrailText;
    private String mBody;
    private String mUrl;

    public NewsContent(String trailText, @NonNull String url, String body) {
        mTrailText = trailText;
        mBody = body;
        mUrl = url;
    }

    public String getBody() {
        return mBody;
    }

    public String getTrailText() {
        return mTrailText;
    }

    public String getUrl() {
        return mUrl;
    }
}
