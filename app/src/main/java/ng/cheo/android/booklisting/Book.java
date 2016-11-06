package ng.cheo.android.booklisting;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mickey on 4/11/16.
 */

public class Book implements Parcelable {

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

    public Book(Parcel in) {
        readFromParcel(in);
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

    public Boolean hasAuthors() {
        return !mAuthors.equals("");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(mName);
        dest.writeString(mAuthors);
        dest.writeString(mImageUrl);

    }

    private void readFromParcel(Parcel in) {

        mName = in.readString();
        mAuthors = in.readString();
        mImageUrl = in.readString();

    }

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public Book createFromParcel(Parcel in) {
                    return new Book(in);
                }

                public Book[] newArray(int size) {
                    return new Book[size];
                }
            };
}
