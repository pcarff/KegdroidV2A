package com.anzym.android.kegdroidlibrary.tasks;

/**
 * Created by pcarff on 1/15/16.
 */
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.anzym.android.kegdroidkiosk.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class LoadKegDroidUserImage extends AsyncTask<String, Void, Bitmap> {

    private String TAG = LoadKegDroidUserImage.class.getSimpleName();

    private ImageView userView;

    public LoadKegDroidUserImage(View view) {
        super();
        this.userView = (ImageView) view;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        // LoadBitmap
        Bitmap bitmap = null;
        InputStream in = null;
        String address = params[0];
        try {
            if (address != null) {
                URL url = new URL(address);
                Log.d(TAG, "URL = " + url);
                URLConnection urlConn = url.openConnection();
                HttpURLConnection httpConn = (HttpURLConnection) urlConn;
                httpConn.connect();
                in = httpConn.getInputStream();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (in != null) {
            bitmap = BitmapFactory.decodeStream(in);
        } else {
            bitmap = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.ic_launcher);
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap image) {
        try {

            ((ImageView) this.userView).setImageBitmap(image);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
