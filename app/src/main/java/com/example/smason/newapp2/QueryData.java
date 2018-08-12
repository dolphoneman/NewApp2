package com.example.smason.newapp2;

import android.text.TextUtils;
import android.util.Log;
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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;



public final class QueryData {

    private static final String LOG_TAG = QueryData.class.getSimpleName();

    private QueryData() {
    }

    /**
     * Query the Guardian dataset and return a list of {@link NewsItem} objects.
     */
    public static List<NewsItem> fetchNewsItems(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of News Articles
        List<NewsItem> newstories = extractNewsItems(jsonResponse);

        // Return the list of News Articles
        return newstories;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
                Log.d(LOG_TAG, "Here is the URL " + url);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                Log.e(LOG_TAG, "Here is the URL " + url);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static List<NewsItem> extractNewsItems(String newsItemsJSON) {
        //If the data string is empty, return.
        if (TextUtils.isEmpty(newsItemsJSON)) {
            return null;
        }
        List<NewsItem> newstories = new ArrayList<>();

        try {
            //Create a JSON Object from the raw data
            JSONObject jsonResponse = new JSONObject(newsItemsJSON);
            //Create a new JSON Object from the response section of the raw data
            JSONObject newsObject = jsonResponse.getJSONObject("response");
            //Grab the JSON Array at the results section and create a new Array dataResults
            JSONArray dataResults = newsObject.getJSONArray("results");

            //Use a for loop to step through the dataResults Array to pull out info
            for (int i = 0; i < dataResults.length(); i++) {
                JSONObject currentNewsItem = dataResults.getJSONObject(i);

                String section = currentNewsItem.getString("sectionName");
                String pubDate = currentNewsItem.getString("webPublicationDate");
                String title = currentNewsItem.getString("webTitle");
                String url = currentNewsItem.getString("webUrl");
                //grab the fields JSONObject to pull the byline from to get the author
                JSONObject authorName = currentNewsItem.getJSONObject("fields");
                String author = authorName.getString("byline");


                //Parse the date from the webPublicationDate element, this removes the time stamp section
                String[] date = pubDate.split("T");

                NewsItem newstory = new NewsItem(date[0], title, section, url, author);
                newstories.add(newstory);
            }

        } catch (JSONException e) {
            Log.e("QueryData", "Problem parsing News results", e);

        }

        return newstories;
    }

}
