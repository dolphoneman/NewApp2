package com.example.smason.newapp2;

//contains items for a single news article
public class NewsItem {

    //Date of the News Article
    private String mDate;

    //Title of the News Article
    private String mTitle;

    //Section the article came from
    private String mSection;

    //Web page of the article
    private String mUrl;

    //Who wrote the article
    private String mAuthor;

    /**
     * Builds a new News Item object
     *
     * @param date    the article was published
     * @param title   of the article
     * @param section the article came from
     * @param url the web address of the article
     */
    public NewsItem(String date, String title, String section, String url, String author) {
        mDate = date;
        mTitle = title;
        mSection = section;
        mUrl = url;
        mAuthor = author;
    }

    //public get methods used to acquire private data
    public String getDate() {
        return mDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSection() {
        return mSection;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getAuthor() {
        return mAuthor;
    }
}
