package com.example.android.booklistingapp;

public class ItemsList {
    /**
     * For hid the rating and the price, it were used this constant variables
     * Constant value was provided using -1 value because is out of range integer
     */
    private static final double NO_VALUE_PROVIDED = -1;
    private static final String FOR_SALE = "FOR_SALE";
    private static final String FREE = "FREE";
    private static final String NOT_FOR_SALE = "NOT_FOR_SALE";
    private String mTitle; //this is the title of the book
    private String mAuthors; //this gets the a list of authors
    private String mPublishedDate; //this gets the date of publication
    private String mImageUrl; //this is the image URL
    private double mPrice = NO_VALUE_PROVIDED; //this is the price of hotel per night
    private String mCurrencyCode; //this gets the currency code as EUR, USD and so on.
    private double mRating; //this is the respective rating
    private String mSaleability; //this gets if an item is free, for sale or not for sale,
    private String mBuyLink; //this get the buy link

    //Here Below the empty Constructor if it is needed
    public ItemsList() {
    }

    /*** Constructor for Book List
     *
     * @param title             Title used on the book
     * @param authors           Authors list of the book
     * @param publishedDate     this gets the date of publication
     * @param imageUrl          this is the image resource ID representing the book
     * @param price             Price of the book
     * @param currencyCode      this gets the currency code as EUR, USD and so on.
     * @param rating            Respective rating
     * @param saleability       this gets if an item is free, for sale or not for sale,
     * @param buyLink           this get the buy link
     */
    public ItemsList(String title, String authors, String publishedDate, String imageUrl, double price, String currencyCode, double rating, String saleability, String buyLink) {
        this.mTitle = title;
        this.mAuthors = authors;
        this.mPublishedDate = publishedDate;
        this.mImageUrl = imageUrl;
        this.mPrice = price;
        this.mCurrencyCode = currencyCode;
        this.mRating = rating;
        this.mSaleability = saleability;
        this.mBuyLink = buyLink;
    }

    //Getter
    public String getTitle() {
        return mTitle;
    }

    public String getAuthors() {
        return mAuthors;
    }

    public String getPublishedDate() {
        return mPublishedDate;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public double getPrice() {
        return mPrice;
    }

    public String getCurrencyCode() {
        return mCurrencyCode;
    }

    public double getRating() {
        return mRating;
    }

    public String getSaleability() {
        return mSaleability;
    }

    public String getBuyLink() {
        return mBuyLink;
    }

    /**
     * Returns whether or not there is an description or price in the list.
     */
    public Boolean hasPrice() {
        return mPrice != NO_VALUE_PROVIDED;
    }

    public Boolean hasRating() {
        return mRating != NO_VALUE_PROVIDED;
    }

    public Boolean isFree() {
        return mSaleability.equals(FREE);
    }

    public Boolean isForSale() {
        return mSaleability.equals(FOR_SALE);
    }

    public Boolean isNotForSale() {
        return mSaleability.equals(NOT_FOR_SALE);
    }

    //auto-generated
    @Override
    public String toString() {
        return "ItemsList{" +
                "mTitle='" + mTitle + '\'' +
                ", mAuthors='" + mAuthors + '\'' +
                ", mPublishedDate='" + mPublishedDate + '\'' +
                ", mImageUrl='" + mImageUrl + '\'' +
                ", mPrice=" + mPrice +
                ", mCurrencyCode='" + mCurrencyCode + '\'' +
                ", mRating=" + mRating +
                ", mSaleability='" + mSaleability + '\'' +
                ", mBuyLink='" + mBuyLink + '\'' +
                '}';
    }
}
