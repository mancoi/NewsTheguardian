package com.example.mancoi.news;

import android.support.annotation.NonNull;

/**
 * Created by mancoi on 23/08/2017.
 */

public class News {

    private String mDate;
    private String mTitle;
    private String mAuthor;
    private String mTrailText;
    private String mSection;
    private String mImgUrl;

    private String mUrl;

    public News(String title, String author, String trailText, String date, String section, @NonNull String apiUrl, String imgUrl) {
        mTitle = title;
        mAuthor = author;
        mTrailText = trailText;
        mDate = date;
        mSection = section;
        mUrl = apiUrl;
        mImgUrl = imgUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    String getAuthor() {
        return mAuthor;
    }

    String getDate() {
        return mDate;
    }

    String getSection() {
        return mSection;
    }

    String getImgUrl() {
        return mImgUrl;
    }

    String getUrl() {
        return mUrl;
    }

    public String getTrailText() {
        return mTrailText;
    }
}
