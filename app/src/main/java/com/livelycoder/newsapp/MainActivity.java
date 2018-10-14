package com.livelycoder.newsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>, SwipeRefreshLayout.OnRefreshListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.news_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    TextView mEmptyView;
    @BindView(R.id.progress_bar)
    View mProgressBar;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    private NewsAdapter mAdapter;
    private final String API_URL = "https://content.guardianapis.com/search";
    private static final int NEWS_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        mAdapter = new NewsAdapter(new ArrayList<News>());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                Log.d(LOG_TAG, "initLoader");
                getSupportLoaderManager().initLoader(NEWS_LOADER_ID, null, this);
            } else {
                mProgressBar.setVisibility(View.GONE);
                mEmptyView.setText(R.string.no_internet);
            }
        }

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );
    }

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int i, @Nullable Bundle bundle) {
        Log.d(LOG_TAG, "onCreateLoader");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
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
        builder.appendQueryParameter("show-tags", "contributor")
                .appendQueryParameter("api-key", getString(R.string.api_key))
                .appendQueryParameter("page-size", pageSize)
                .appendQueryParameter("order-by", orderBy);
        Log.d(LOG_TAG, "url:" + builder.toString());
        return new NewsLoader(this, builder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, final List<News> news) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(LOG_TAG, "onLoadFinished");
                mProgressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                if (news != null && !news.isEmpty()) {
                    mEmptyView.setText("");
                    mAdapter.clear();
                    mAdapter.addAll(news);
                } else
                    mEmptyView.setText(R.string.no_news);
            }
        }, 5000);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {
        Log.d(LOG_TAG, "onLoaderReset");
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_credits:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.credits)
                        .setMessage(R.string.attribution)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        mAdapter.clear();
        getSupportLoaderManager()
                .restartLoader(NEWS_LOADER_ID, null, this);
    }
}
