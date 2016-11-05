package ng.cheo.android.booklisting;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mickey on 4/11/16.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    static class ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView authorsTextView;
    }

    public BookAdapter(Activity context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.name);
            viewHolder.authorsTextView = (TextView) convertView.findViewById(R.id.authors);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Book book = getItem(position);

        viewHolder.imageView.setVisibility(View.INVISIBLE);
        viewHolder.nameTextView.setText(book.getName());
        viewHolder.authorsTextView.setText("by " + book.getAuthors());
        new DownloadImageAsyncTask(viewHolder.imageView)
                .execute(book.getImageUrl());

        return convertView;
    }

    private class DownloadImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

        ImageView bmImage;

        public DownloadImageAsyncTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];

            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            bmImage.setVisibility(View.VISIBLE);
        }
    }
}
