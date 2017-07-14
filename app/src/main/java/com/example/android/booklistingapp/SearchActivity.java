package com.example.android.booklistingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    /**
     * URL for book list data from the Google Books API using a specific query
     */
    private static final String REQUEST_URL = "https://www.googleapis.com/books/v1/volumes";
    private ListView listView;
    private ItemsListAdapter adapter;
    private TextView emptyStateTextView;
    private ProgressBar mLoader;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // This add a logo in ActionBar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //Find the id for the following views
        listView = (ListView) findViewById(R.id.list);
        emptyStateTextView = (TextView) findViewById(R.id.empty_view);
        //Load a circle progress bar as loading bar
        mLoader = (ProgressBar) findViewById(R.id.loading_indicator);
        //this retrieves data from research field in the MainActvity
        if (getIntent() != null && getIntent().getExtras() != null) {
            query = getIntent().getExtras().getString("query");
        }
        if (isConnected()) { //if there is internet connection
            //this make the request in background
            booksAsyncTask task = new booksAsyncTask();
            task.execute(REQUEST_URL);
        } else {
            //hide the loading bar
            mLoader.setVisibility(View.GONE);
            //display that there is no internet connection
            emptyStateTextView.setText(R.string.no_internet);
        }
        // set the view to show when the list is empty
        listView.setEmptyView(emptyStateTextView);
        //Shows the items list using an ListView with a custom adapter
        adapter = new ItemsListAdapter(this, new ArrayList<ItemsList>());
        listView.setAdapter(adapter);
        // Set a click listener to open an activity for the respective position
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //load the buy link in the Browser
                ItemsList itemsList = (ItemsList) adapterView.getAdapter().getItem(position);
                Uri webpage;
                String title = itemsList.getTitle();
                if (itemsList.getBuyLink() != null) {
                    webpage = Uri.parse(itemsList.getBuyLink());
                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                } else { //if the items have no buy link, a toast message is displayed
                    Toast.makeText(getApplicationContext(), "\"" + title + "\" " + getString(R.string.not_available), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread.
     */
    private class booksAsyncTask extends AsyncTask<String, Void, List<ItemsList>> {
        /**
         * This method is invoked (or called) on a background thread, so we can perform
         * long-running operations like making a network request. It is NOT okay to update the UI from a background thread.
         */
        @Override
        protected List<ItemsList> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            //this call the connection on server in base of preference
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            //here get the preferences
            String maxResults = sharedPrefs.getString(
                    getString(R.string.settings_max_results_key),
                    getString(R.string.settings_max_results_default));
            String orderBy = sharedPrefs.getString(
                    getString(R.string.settings_order_by_key),
                    getString(R.string.settings_order_by_default)
            );
            //get the URL of the google server
            Uri baseUri = Uri.parse(urls[0]);
            //this is used to build the URL with the query parameters
            Uri.Builder uriBuilder = baseUri.buildUpon();
            uriBuilder.appendQueryParameter("q", query); //this adds the query in the search box
            uriBuilder.appendQueryParameter("maxResults", maxResults); //this adds the max books listed
            uriBuilder.appendQueryParameter("orderBy", orderBy); //this defines the order of the list
            // Perform the HTTP request for data and process the response.
            return QueryUtils.fetchData(uriBuilder.toString());
        }

        /**
         * This method is invoked on the main UI thread after the background work has been
         * completed.
         */
        @Override
        protected void onPostExecute(List<ItemsList> data) {
            if (isConnected()) {
                // Clear the adapter of previous data
                adapter.clear();
                //hide the loading bar
                mLoader.setVisibility(View.GONE);
                // Set empty state text to display "No books found."
                String message = getString(R.string.no_books, query);
                emptyStateTextView.setText(message);
                // If there is a valid list of books, it add them to the adapter's data set.
                if (data != null && !data.isEmpty()) {
                    adapter.addAll(data);
                }
            } else {
                mLoader.setVisibility(View.GONE);
                emptyStateTextView.setText(R.string.no_internet);
            }
        }
    }

    //determine if connection is active
    private Boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        Boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}