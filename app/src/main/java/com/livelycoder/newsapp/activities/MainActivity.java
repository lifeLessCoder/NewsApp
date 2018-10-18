package com.livelycoder.newsapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.livelycoder.newsapp.R;
import com.livelycoder.newsapp.adapters.NewsAdapter;
import com.livelycoder.newsapp.models.News;
import com.livelycoder.newsapp.network.NewsLoader;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>, SwipeRefreshLayout.OnRefreshListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private TextView emptyView;
    private View progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NewsAdapter adapter;
    private final String API_URL = "https://content.guardianapis.com/search";
    private static final int NEWS_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);
        findAllViews();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false));
        adapter = new NewsAdapter(this, new ArrayList<News>());
        recyclerView.setAdapter(adapter);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                Log.d(LOG_TAG, "initLoader");
                getSupportLoaderManager().initLoader(NEWS_LOADER_ID, null, this);
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
    }

    /**
     * Finds all the views by id
     */
    private void findAllViews() {
        recyclerView = findViewById(R.id.news_recycler_view);
        emptyView = findViewById(R.id.empty_view);
        progressBar = findViewById(R.id.progress_bar);
        swipeRefreshLayout = findViewById(R.id.swipe_container);
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
        Toast.makeText(this, "page size : " + pageSize, Toast.LENGTH_SHORT).show();
        String orderBy = sharedPreferences.getString(getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));
        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(API_URL);
        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder builder = baseUri.buildUpon();
        // Append query parameter and its value.
        builder.appendQueryParameter("show-tags", "contributor")
                .appendQueryParameter("api-key", getString(R.string.api_key))
                .appendQueryParameter("show-fields", "thumbnail")
                .appendQueryParameter("page-size", pageSize)
                .appendQueryParameter("order-by", orderBy);
        Log.d(LOG_TAG, "url:" + builder.toString());
        return new NewsLoader(this, builder.toString());
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
        adapter.clear();
        getSupportLoaderManager()
                .restartLoader(NEWS_LOADER_ID, null, this);
    }
}
