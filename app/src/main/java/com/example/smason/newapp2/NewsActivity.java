package com.example.smason.newapp2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.widget.TextView;

public class NewsActivity extends AppCompatActivity implements LoaderCallbacks<List<NewsItem>> {

    /**
     * URL for News information from the from The Guardian API with the byline added to get the author
     */
    private static final String GUARDIAN_URL = "https://content.guardianapis.com/search?";

    //Only really needed if using more than one loader
    private static final int NEWSITEM_LOADER_ID = 1;

    @BindView(R.id.list)
    ListView newsListView;

    @BindView(R.id.emptyView)
    TextView emptyTextView;

    @BindView(R.id.progress_bar)
    View progressBar;

    private static final String LOG_TAG = NewsActivity.class.getName();

    //Adapter for the list of articles
    private NewsItemAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);

        // bind the view using butterknife
        ButterKnife.bind(this);

        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWSITEM_LOADER_ID, null, this);
        } else{
            //If there is no connectivity set the empty view to the correct text
            progressBar.setVisibility(View.GONE);
            emptyTextView.setText(R.string.no_connection);
        }

        //Create a new adapter that takes the list of news stories as input
        mAdapter = new NewsItemAdapter(this, new ArrayList<NewsItem>());

        //If the there is no data to pull an empty view is displayed
        newsListView.setEmptyView(emptyTextView);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Find the current news item that was clicked
                NewsItem currentNewsItem = mAdapter.getItem(position);

                //Take the Url String and turn it into a Uri Object
                Uri newsItemUri = Uri.parse(currentNewsItem.getUrl());

                //Create a new Intent to view the web page
                Intent openWebPage = new Intent(Intent.ACTION_VIEW, newsItemUri);

                //launch the intent to open the web page
                startActivity(openWebPage);
            }
        });

    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //This method passes the MenuItem that is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    // onCreateLoader instantiates and returns a new Loader for the given ID
    public Loader<List<NewsItem>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String subjectSearch = sharedPrefs.getString(
                getString(R.string.settings_search_term_key),
                getString(R.string.settings_search_term_default));

        String orderBy  = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value. For example, the search subject`q=UFC`
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("q", subjectSearch);
        uriBuilder.appendQueryParameter("show-fields", "byline");
        uriBuilder.appendQueryParameter("api-key", "fa889bfa-3afd-443b-bf4a-46e363835da5");


        // Return the completed uri
        return new NewsItemLoader(this, uriBuilder.toString());

    }

    @Override
    public void onLoadFinished(Loader<List<NewsItem>> loader, List<NewsItem> newstories) {
        //Hide the progress bar after the data load is complete
        progressBar.setVisibility(View.GONE);

        //Display text if no data is pulled from the Guardian API
        emptyTextView.setText(R.string.whoops);

        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of articles, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (newstories != null && !newstories.isEmpty()) {
            mAdapter.addAll(newstories);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();

    }
}
