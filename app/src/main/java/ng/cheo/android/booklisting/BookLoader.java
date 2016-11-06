package ng.cheo.android.booklisting;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by mickey on 6/11/16.
 */

public class BookLoader extends AsyncTaskLoader<ArrayList<Book>> {

    private String mUrl;

    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Book> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        ArrayList<Book> books = QueryUtils.fetchBookData("android", mUrl);
        return books;
    }
}
