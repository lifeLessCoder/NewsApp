package com.livelycoder.newsapp.fragments;


import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.livelycoder.newsapp.R;
import com.livelycoder.newsapp.adapters.NewsAdapter;
import com.livelycoder.newsapp.models.News;
import com.livelycoder.newsapp.network.NewsLoader;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class BusinessNewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<List<News>> {
    private static final String LOG_TAG = BusinessNewsFragment.class.getSimpleName();
    private static final String section = "business";
    private static int NEWS_LOADER_ID = 2;
    private final String API_URL = "https://content.guardianapis.com/search";
    private RecyclerView recyclerView;
    private TextView emptyView;
    private View progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NewsAdapter adapter;
    private AppCompatActivity activity;

    public BusinessNewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        activity = (AppCompatActivity) getActivity();
        findAllViews(view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity,
                RecyclerView.VERTICAL, false));
        adapter = new NewsAdapter(activity, new ArrayList<News>());
        recyclerView.setAdapter(adapter);
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                Log.d(LOG_TAG, "initLoader");
                activity.getSupportLoaderManager().initLoader(NEWS_LOADER_ID, null, this);
            } else {
                progressBar.setVisibility(View.GONE);
                emptyView.setText(R.string.no_internet);
            }
        }

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );
        return view;
    }

    /**
     * Finds all the views in a root view
     *
     * @param view the root view in which all views are to be found
     */
    private void findAllViews(View view) {
        recyclerView = view.findViewById(R.id.news_recycler_view);
        emptyView = view.findViewById(R.id.empty_view);
        progressBar = view.findViewById(R.id.progress_bar);
        swipeRefreshLayout = view.findViewById(R.id.swipe_container);
    }

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int i, @Nullable Bundle bundle) {
        Log.d(LOG_TAG, "onCreateLoader");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        // getString retrieves a String value from the preferences. The second parameter is the
        // default value for this preference.
        String pageSize = sharedPreferences.getString(getString(R.string.settings_page_size_key),
                getString(R.string.settings_page_size_default));
        String orderBy = sharedPreferences.getString(getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));
        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(API_URL);
        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder builder = baseUri.buildUpon();
        // Append query parameter and its value.
        builder.appendQueryParameter("section", section)
                .appendQueryParameter("show-tags", "contributor")
                .appendQueryParameter("api-key", getString(R.string.api_key))
                .appendQueryParameter("show-fields", "thumbnail")
                .appendQueryParameter("page-size", pageSize)
                .appendQueryParameter("order-by", orderBy);
        Log.d(LOG_TAG, "url:" + builder.toString());
        return new NewsLoader(activity, builder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, final List<News> news) {
        Log.d(LOG_TAG, "onLoadFinished");
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        if (news != null && !news.isEmpty()) {
            emptyView.setText("");
            adapter.clear();
            adapter.addAll(news);
        } else
            emptyView.setText(R.string.no_news);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {
        Log.d(LOG_TAG, "onLoaderReset");
        adapter.clear();
    }

    @Override
    public void onRefresh() {
        adapter.clear();
        activity.getSupportLoaderManager()
                .restartLoader(NEWS_LOADER_ID, null, this);
    }

}
