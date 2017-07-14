package com.example.android.booklistingapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class ItemsListAdapter extends ArrayAdapter<ItemsList> {
    public static final String LOG_TAG = ItemsListAdapter.class.getName();

    //Constructor of ListAdapter
    public ItemsListAdapter(Activity context, ArrayList<ItemsList> items) {
        // The second argument is used when the ArrayAdapter is populating and it can be any value. So it is used 0.
        super(context, 0, items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;
        if (listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(R.layout.list, parent, false);
        }
        //this gets the item position
        if (position < getCount()) {
            ItemsList currentItem = getItem(position);
            //this displays the title of the book in the list
            TextView tw_title = (TextView) listView.findViewById(R.id.title_text_view);
            tw_title.setText(currentItem.getTitle());
            TextView tw_authors = (TextView) listView.findViewById(R.id.author_text_view);
            tw_authors.setText(currentItem.getAuthors());
            TextView tw_publishedDate = (TextView) listView.findViewById(R.id.publishedDate);
            // Display the date of publication
            tw_publishedDate.setText(formatDate(currentItem.getPublishedDate()));
            //this shows the price
            TextView tw_price = (TextView) listView.findViewById(R.id.price_text_view);
            //here the price is hidden when not necessary.
            if (currentItem.isFree()) {
                tw_price.setText(getContext().getString(R.string.isFree));
            } else if (currentItem.isForSale()) {
                String currency = currentItem.getCurrencyCode();
                switch (currency) {
                    case "USD":
                        currency = "$";
                        break;
                    case "EUR":
                        currency = "€";
                        break;
                    case "GBP":
                        currency = "£";
                        break;
                    default:
                        currency = currentItem.getCurrencyCode();
                        break;
                }
                String price_value = formatValue(currentItem.getPrice());
                String price = getContext().getString(R.string.price, currency, price_value);
                tw_price.setText(price);
                tw_price.setVisibility(View.VISIBLE);
            } else {
                tw_price.setVisibility(View.INVISIBLE);
            }
            //this shows an image representing the book using an AsyncTask for downloading the images
            ImageView imageView = (ImageView) listView.findViewById(R.id.image);
            new imageAsyncTask(imageView).execute(currentItem.getImageUrl());
            //this displays the rating value
            TextView rating_tw = (TextView) listView.findViewById(R.id.averageRating);
            RatingBar ratingBar = (RatingBar) listView.findViewById(R.id.ratingBar);
            //double rating = (currentItem.getRating() / currentItem.getComments()) * 5;
            if (currentItem.hasRating()) {
                rating_tw.setText(String.valueOf(currentItem.getRating()));
                rating_tw.setVisibility(View.VISIBLE);
                ratingBar.setRating((float) currentItem.getRating());
                ratingBar.setVisibility(View.VISIBLE);
            } else {
                rating_tw.setVisibility(View.INVISIBLE);
                ratingBar.setVisibility(View.INVISIBLE);
            }
        }
        return listView;
    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread,
     * and then update the UI allowing to display the image.
     */
    private class imageAsyncTask extends AsyncTask<String, Void, Bitmap> {
        ImageView mImage;

        //This method is called on a background thread.
        public imageAsyncTask(ImageView bmImage) {
            this.mImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap image = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                image = BitmapFactory.decodeStream(in); //this is used for decode the InputStream to image
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            }
            return image;
        }

        //This method is invoked on the main UI thread after the background work has been completed.
        protected void onPostExecute(Bitmap result) {
            //set the image obtained
            mImage.setImageBitmap(result);
        }
    }

    //for getting decimal values, based on the string, here can be shown decimal with different digits , 0.000000 and so on
    private String formatValue(double value) {
        DecimalFormat format = new DecimalFormat("0.00");
        return format.format(value);
    }

    //for getting the date conversion
    public String formatDate(String date) {
        String newFormatData = "";
        if (date.length() >= 10) {
            // Splits the string after 10 char, because the date obtained from server is like this "2017-07-15T21:30:35Z", so this method will give 2017-07-15
            CharSequence splittedDate = date.subSequence(0, 10);
            try {
                Date formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(splittedDate.toString());
                newFormatData = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(formatDate);
            } catch (ParseException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        } else {
            newFormatData = date;
        }
        return newFormatData;
    }
}
