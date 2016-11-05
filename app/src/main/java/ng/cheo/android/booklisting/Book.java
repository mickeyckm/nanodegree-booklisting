package ng.cheo.android.booklisting;

import android.graphics.Bitmap;

/**
 * Created by mickey on 4/11/16.
 */

public class Book {

    private String mName;
    private String mAuthors;
    private String mImageUrl;
    private Bitmap mBitmapImage;

    public Book(String name) {
        mName = name;
        mAuthors = "";
        mImageUrl = "";
        mBitmapImage = null;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public Bitmap getBitmapImage() {
        return mBitmapImage;
    }

    public void setBitmapImage(Bitmap bitmap) {
        mBitmapImage = bitmap;
    }

    public Boolean hasBitmap() {
        return mBitmapImage != null;
    }

    public String getAuthors() {
        return mAuthors;
    }

    public void setAuthors(String authors) {
        mAuthors = authors;
    }
}
