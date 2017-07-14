package com.example.android.booklistingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    /**
     * URL for book list data from the Google Books API using a specific query
     */
    private Button searchButton = null;
    private EditText searchField = null;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // This add a logo in ActionBar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //Set the Awesome Font
        Typeface fontAW = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        searchButton = (Button) findViewById(R.id.search);
        searchButton.setTypeface(fontAW);
        //this finds the id of search field
        searchField = (EditText) findViewById(R.id.et_search);
        // The code in this method will be executed when the button search on keyboard is clicked on.
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });
        // The code in this method will be executed when the button search is clicked on.
        searchButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
    }

    private void search() {
        if (isConnected()) {
            query = searchField.getText().toString();
            if (!query.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("query", query);  // pass the values of query and retrieve them in the SearchActivity using query variable
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.search_field_empty), Toast.LENGTH_LONG).show();
            }
        } else {
            String message = getString(R.string.no_internet);
            new AlertDialog.Builder(this).setMessage(message).show();
        }
    }

    //this allows to open menu of search preference
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

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

    //determine if connection is active
    private Boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        Boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
