package com.livelycoder.newsapp.network;

import android.text.TextUtils;
import android.util.Log;

import com.livelycoder.newsapp.models.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class QueryUtils {
    private static final String RESPONSE = "response";
    private static final String RESULTS = "results";
    private static final String WEB_PUBLICATION_DATE = "webPublicationDate";
    private static final String SECTION_NAME = "sectionName";
    private static final String WEB_TITLE = "webTitle";
    private static final String WEB_URL = "webUrl";
    private static final String TAGS = "tags";
    private static final String FIELDS = "fields";
    private static final String THUMBNAIL = "thumbnail";

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
        // This class should not be instantiated
    }

    /**
     * Extracts news from the news API in the form of a list
     *
     * @param stringUrl the URL to extract news from
     * @return the List of news
     */
    static List<News> extractNews(String stringUrl) {
        if (TextUtils.isEmpty(stringUrl))
            return null;
        URL url = createUrl(stringUrl);
        String jsonResponse = null;
        if (url != null) {
            jsonResponse = makeHttpRequest(url);
        }
        return extractNewsFromJson(jsonResponse);
    }

    /**
     * Creates URL object from the given string
     *
     * @param urlString to be converted to URL object
     * @return the URL object
     */
    private static URL createUrl(String urlString) {
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem creating URL object");
            return null;
        }
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) {
        String jsonResponse = "";
        if (url == null)
            return jsonResponse;
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(10000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else
                Log.e(LOG_TAG, "Error response code" + urlConnection.getResponseCode());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results", e);
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing the input stream", e);
                }
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        if (inputStream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null)
                builder.append(line);
        }
        return builder.toString();
    }

    /**
     * Return a List of {@link News} objects by parsing out information
     * about the list of news from the input jsonResponse string.
     */
    private static List<News> extractNewsFromJson(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse))
            return null;
        List<News> news = new ArrayList<>();

        try {
            JSONObject rootJsonObject = new JSONObject(jsonResponse);
            JSONObject response = rootJsonObject.getJSONObject(RESPONSE);
            JSONArray results = response.getJSONArray(RESULTS);
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                String publishDate = result.getString(WEB_PUBLICATION_DATE);
                String sectionName = result.getString(SECTION_NAME);
                String title = result.getString(WEB_TITLE);
                String webUrl = result.getString(WEB_URL);
                JSONArray tags = result.getJSONArray(TAGS);
                JSONObject tag = tags.optJSONObject(0);
                String authorName;
                if (tag != null) {
                    authorName = tag.getString(WEB_TITLE);
                } else {
                    authorName = "";
                }
                JSONObject fields = result.optJSONObject(FIELDS);
                String thumbnail = null;
                if (fields != null) {
                    thumbnail = fields.getString(THUMBNAIL);
                }
                news.add(new News(publishDate, sectionName, title, authorName, webUrl, thumbnail));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON results", e);
        }
        return news;
    }
}
