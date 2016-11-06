package ng.cheo.android.booklisting;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<ArrayList<Book>> {

    ListView listView;
    BookAdapter mAdapter;
    EditText queryEditText;
    TextView emptyTextView;

    /** Tag for the log messages */
    public static final String LOG_TAG = "BookListing";
    public static final int BOOK_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.results);
        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        listView.setAdapter(mAdapter);

        queryEditText = (EditText) findViewById(R.id.query);
        emptyTextView = (TextView) findViewById(R.id.empty);

        // Setup loader
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(BOOK_LOADER_ID, null, this);

        // Toast setup
        final Context context = getApplicationContext();
        final int duration = Toast.LENGTH_SHORT;

        // Search
        Button search = (Button) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    // fetch data

                    if (queryEditText.getText().toString().equals("")) {
                        Toast toast = Toast.makeText(context, getString(R.string.toast_query_empty), duration);
                        toast.show();
                        return;
                    }

                    getLoaderManager().restartLoader(BOOK_LOADER_ID, null, MainActivity.this);

                    // Close soft keyboard
                    if (v != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }

                } else {
                    // No network connection

                    Toast toast = Toast.makeText(context, getString(R.string.toast_no_network), duration);
                    toast.show();

                    Log.e(LOG_TAG, "No network connection");
                }
            }
        });
    }

    @Override
    public Loader<ArrayList<Book>> onCreateLoader(int id, Bundle args) {
        String query = queryEditText.getText().toString();
        try {
            query = URLEncoder.encode(query, "UTF-8");
        }
        catch (IOException e) {
            Log.e(LOG_TAG, "Error url encoding: ", e);
            query = "";
        }

        String url = "https://www.googleapis.com/books/v1/volumes?q=" + query + "&maxResults=10";
        return new BookLoader(MainActivity.this, url);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Book>> loader, ArrayList<Book> books) {
        mAdapter.clear();

        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
            emptyTextView.setVisibility(View.GONE);
            listView.setAdapter(mAdapter);
            listView.setVisibility(View.VISIBLE);
        }
        else {
            emptyTextView.setText(getString(R.string.result_empty));
            emptyTextView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);

        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Book>> loader) {
        mAdapter.clear();
    }
}
