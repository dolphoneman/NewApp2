package com.example.smason.newapp2;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.List;

/**
 * Loads a list of earthquakes by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class NewsItemLoader extends AsyncTaskLoader<List<NewsItem>> {
    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = NewsItemLoader.class.getName();
    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link NewsItemLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public NewsItemLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<NewsItem> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of News Articles.
        List<NewsItem> newstories = QueryData.fetchNewsItems(mUrl);
        return newstories;
    }
}
