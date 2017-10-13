package com.example.mancoi.news;

/**
 * Created by mancoi on 09/10/2017.
 */

public class NewsContent {

    private String mMain;
    private String mBody;

    public NewsContent(String main, String body) {
        mMain = main;
        mBody = body;
    }

    public String getContent()
    {
        return mMain + mBody;
    }

}
