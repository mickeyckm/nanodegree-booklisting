package ng.cheo.android.booklisting;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /** Tag for the log messages */
    public static final String LOG_TAG = "BookListing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = getApplicationContext();
        final int duration = Toast.LENGTH_SHORT;

        Button search = (Button) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    // fetch data
                    EditText query = (EditText) findViewById(R.id.query);

                    if (query.getText().toString().equals("")) {
                        Toast toast = Toast.makeText(context, getString(R.string.toast_query_empty), duration);
                        toast.show();
                        return;
                    }

                    SearchBookAsyncTask task = new SearchBookAsyncTask();
                    task.setKeywords(query.getText().toString());
                    task.execute();

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

    private class SearchBookAsyncTask extends AsyncTask<String, Void, ArrayList<Book>> {

        private String mKeywords;

        @Override
        protected ArrayList<Book> doInBackground(String... urls) {
            String jsonResponse = "";
            try {
                mKeywords = URLEncoder.encode(mKeywords, "UTF-8");
                jsonResponse = makeHttpRequest(createUrl("https://www.googleapis.com/books/v1/volumes?q=" + mKeywords + "&maxResults=10"));
            }
            catch (IOException e) {
                Log.e(LOG_TAG, "Error with connecting: ", e);
            }

            ArrayList<Book> books = extractFromJSON(jsonResponse);

            return books;
        }

        @Override
        protected void onPostExecute(ArrayList<Book> data) {
            ListView listView = (ListView) findViewById(R.id.results);
            TextView emptyTextView = (TextView) findViewById(R.id.empty);

            if (data.isEmpty()) {
                emptyTextView.setText(getString(R.string.result_empty));
                emptyTextView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }
            else {
                BookAdapter adapter = new BookAdapter(MainActivity.this, data);
                emptyTextView.setVisibility(View.GONE);
                listView.setAdapter(adapter);
                listView.setVisibility(View.VISIBLE);
            }
        }

        public void setKeywords(String keywords) {
            mKeywords = keywords;
        }

        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            }
            catch (Exception e) {
                Log.e(LOG_TAG, "Error with creating URL: ", e);
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            // If the URL is null, then return early.
            if (url == null) {
                return jsonResponse;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
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
                Log.e(LOG_TAG, "Problem retrieving the search JSON results.", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }

            return jsonResponse;
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private String readFromStream(InputStream inputStream) throws IOException {
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

        private ArrayList<Book> extractFromJSON(String jsonResponse) {

            ArrayList<Book> books = new ArrayList<Book>();

            try {
                JSONObject baseJsonResponse = new JSONObject(jsonResponse);
                JSONArray itemsArray = baseJsonResponse.getJSONArray("items");

                // If there are results in the features array
                if (itemsArray.length() > 0) {

                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject item = itemsArray.getJSONObject(i);
                        JSONObject volumeInfo = item.getJSONObject("volumeInfo");

                        // Title
                        String title = volumeInfo.getString("title");

                        // Authors
                        JSONArray authorsJSON = volumeInfo.getJSONArray("authors");
                        String authors = authorsJSON.join(", ").replace("\"", "");

                        // Image
                        JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                        String smallThumbnail = imageLinks.getString("smallThumbnail");

                        Book book = new Book(title);
                        book.setImageUrl(smallThumbnail);
                        book.setAuthors(authors);
                        books.add(book);
                    }

                    return books;
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the search JSON results", e);
            }

            return null;
        }
    }
}
