package com.livelycoder.newsapp.network;

import android.content.Context;
import android.util.Log;

import com.livelycoder.newsapp.models.News;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

/**
 * Loads a list of news by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class NewsLoader extends AsyncTaskLoader<List<News>> {
    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = NewsLoader.class.getSimpleName();

    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Cache data for orientation changes
     */
    private List<News> mNews;

    public NewsLoader(@NonNull Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.d(LOG_TAG, "onStartLoading");
        if (mNews == null)
            forceLoad();
        else
            deliverResult(mNews);
    }

    @Override
    public void deliverResult(@Nullable List<News> data) {
        Log.d(LOG_TAG, "deliverResult");
        mNews = data;
        super.deliverResult(data);
    }

    @Nullable
    @Override
    public List<News> loadInBackground() {
        Log.d(LOG_TAG, "loadInBackground");
        if (mUrl == null)
            return null;
        mNews = QueryUtils.extractNews(mUrl);
        return mNews;
    }
}
