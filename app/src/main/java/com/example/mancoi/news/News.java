package com.example.mancoi.news;

/**
 * Created by mancoi on 23/08/2017.
 */

public class News {

    private String mDate;
    private String mTitle;
    private String mAuthor;
    private String mSection;
    private String mImgUrl;

    public News(String title, String author, String date, String section)
    {
        mTitle = title;
        mAuthor = author;
        mDate = date;
        mSection = section;;
    }

    public News(String title, String author, String date, String section, String imgUrl)
    {
        mTitle = title;
        mAuthor = author;
        mDate = date;
        mSection = section;
        mImgUrl = imgUrl;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public String getAuthor()
    {
        return mAuthor;
    }

    public String getDate()
    {
        return mDate;
    }

    public String getSection()
    {
        return mSection;
    }

    public String getImgUrl()
    {
        return mImgUrl;
    }
}
