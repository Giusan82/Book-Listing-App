package com.example.android.booklistingapp;

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

/**
 * Helper methods related to requesting and receiving data from GOOGLE BOOKS API.
 */
public final class QueryUtils {
    public static final String LOG_TAG = QueryUtils.class.getName();

    //this have a private constructor because no one should create an instance of this class.
    private QueryUtils() {
    }

    /**
     * Query the Google Books database and return an {@link QueryUtils} object to represent a single item.
     */
    public static List<ItemsList> fetchData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        // Extract relevant fields from the JSON response and create a list of items
        List<ItemsList> items = extractData(jsonResponse);
        return items;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, e.getMessage());
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
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the InputStream into a String which contains the
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

    /**
     * Return a list of {@link ItemsList} objects that has been built up from parsing a JSON response.
     */
    public static List<ItemsList> extractData(String jsonResponse) {
        ArrayList<ItemsList> bookList = new ArrayList<>();
        // Try to parse the JSON Response.
        try {
            //This creates the root JSONObject by calling jsonResponse
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            //this get the objects contained in the a items Array
            if (baseJsonResponse.has("items")) {
                JSONArray itemsArray = baseJsonResponse.getJSONArray("items");
                //the data are obtained for each item
                for (int i = 0; i < itemsArray.length(); i++) {
                    double averageRating = -1; //this is the average rating of the single book
                    double amount = 0; //this is the price of the single book
                    String buyLink = null; //this is the link for buy the book
                    String currencyCode = ""; //this is the currency code used for display the price
                    String authors = ""; //this is the list of authors
                    String publishedDate = "";
                    //here there is pulled out the JSON object at the specified position
                    JSONObject singleItem = itemsArray.getJSONObject(i);
                    //here there is extracted out the JSON object associated with the volumeInfo key.
                    JSONObject volumeInfo = singleItem.getJSONObject("volumeInfo");
                    //here there is the title extracted as string from volumeInfo key
                    String title = volumeInfo.getString("title");
                    //here there are a list of authors extracted as an array from volumeInfo key
                    if (volumeInfo.has("authors")) {
                        JSONArray authorsList = volumeInfo.getJSONArray("authors");
                        if (authorsList.length() > 1) {
                            authors = authorsList.join(", ").replaceAll("\"", "");
                        } else if (authorsList.length() == 1) {
                            authors = authorsList.getString(0);
                        } else if (authorsList.length() == 0) {
                            authors = "";
                        }
                    }
                    //this get the publication date
                    if (volumeInfo.has("publishedDate")) {
                        publishedDate = volumeInfo.getString("publishedDate");
                    }
                    //this get the link of image representing the book
                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    String smallThumbnail = imageLinks.getString("smallThumbnail");
                    //this is the average rating extracted from volumeInfo
                    if (volumeInfo.has("averageRating")) {
                        averageRating = volumeInfo.getDouble("averageRating");
                    }
                    //this get the saleInfo object which contain information on price, currency code and the buy link
                    JSONObject saleInfo = singleItem.getJSONObject("saleInfo");
                    String saleability = saleInfo.getString("saleability");
                    if (saleInfo.has("retailPrice")) {
                        JSONObject retailPrice = saleInfo.getJSONObject("retailPrice");
                        amount = retailPrice.getDouble("amount");
                        currencyCode = retailPrice.getString("currencyCode");
                    }
                    if (saleInfo.has("buyLink")) {
                        buyLink = saleInfo.getString("buyLink");
                    }
                    //here it is create a new ItemsList object with all data extracted from JSON
                    ItemsList books = new ItemsList(title, authors, publishedDate, smallThumbnail, amount, currencyCode, averageRating, saleability, buyLink);
                    //add the ItemsList object to the Array
                    bookList.add(books);
                }
            }
        } catch (JSONException e) {
            //If there's a problem with the way the JSON is formatted, a JSONException exception object will be thrown.
            // Catch the exception so the app doesn't crash, and print the following error message to the logs.
            Log.e(LOG_TAG, e.getMessage());
        }
        return bookList;
    }
}
