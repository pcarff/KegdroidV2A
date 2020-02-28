package com.anzym.android.kegdroidlibrary.tasks;

/**
 * Created by pcarff on 1/15/16.
 */
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anzym.android.kegdroidlibrary.database.BeerDBHelper;
import com.anzym.android.kegdroidlibrary.database.BeerDataSource;
import com.anzym.android.kegdroidlibrary.models.Beer;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import android.content.ContentValues;
import android.content.Context;

public class FetchBeerTask extends AsyncTask<Void, Void, String> {

    private static String TAG = "UpdateBeers.class";

    private Context mCtx;

    private String beersUrl = "http://kegdroidcloud.anzym.com/beers";
    private String fetchImageUrl = "http://kegdroidcloud.appspot.com/serve/";
    private BeerDataSource beerDbSource;

    public FetchBeerTask(Context ctx) {
        this.mCtx = ctx;
    }

    @Override
    protected String doInBackground(Void... params) {
        String status = "";
        try {
            if (fetchBeerData(true)) {
                status = "COMPLETE";
            } else {
                status = "FAILED";
            }
        } catch (IOException e) {
            Log.e(TAG, "Transient error", e);
        }
        return status;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Toast.makeText(mCtx, "Data load complete!", Toast.LENGTH_LONG).show();
    }

    private boolean fetchBeerData(boolean b) throws IOException {
        boolean success = false;
        JSONObject beerList = null;
        String result = "{}";
        System.out.println("Fetching Beer Data");
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(beersUrl);
        HttpResponse response;
        // String address = null;

        try {
            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                result = convertStreamToString(instream);
                instream.close();
            }
            beerList = new JSONObject(result);
        } catch (Exception e) {
        }
        // NEED TO PARSE THE OBJECT AND ADD TO DATABASE>
        success = parseBeerList(beerList);
        return success;
    }

    private boolean parseBeerList(JSONObject bl) {
        Log.d(TAG, "Creating beerDbSource");
        beerDbSource = new BeerDataSource(mCtx);
        Log.d(TAG, "Opening beerDbSource");
        beerDbSource.open();

        try {
            JSONArray beers = bl.getJSONArray("beers");
            for (int i = 0; i < beers.length(); i++) {
                JSONObject b = beers.getJSONObject(i);
                boolean add = false;
                Log.d(TAG, "Adding/Updating " + b.getString("name"));
                Beer beer = beerDbSource.getBeer(b.getLong("ID"));
                if (beer == null) {
                    add = true;
                }
                ContentValues values = new ContentValues();
                if (add) {
                    Log.d(TAG, "Putting ID: " + b.getLong("ID"));

                    values.put(BeerDBHelper.COL_ID, b.getLong("ID"));
                }
                //Log.d(TAG, "Putting Name: " + b.getString("name"));
                values.put(BeerDBHelper.COL_NAME, b.getString("name"));

//                Log.d(TAG, "Putting brewery_id" + b.getLong("brewery_id"));
                values.put(BeerDBHelper.COL_BREWERY_ID, b.getLong("brewery_id"));

//                Log.d(TAG, "Putting style: " + b.getString("style"));
                values.put(BeerDBHelper.COL_STYLE, b.getString("style"));

//                Log.d(TAG, "Putting abv: " + b.getDouble("abv"));
                values.put(BeerDBHelper.COL_ABV, b.getDouble("abv"));

//                Log.d(TAG, "Putting ibu " + b.getInt("ibu"));
                values.put(BeerDBHelper.COL_IBU, b.getInt("ibu"));

//                Log.d(TAG, "Putting description " + b.getString("description"));
                values.put(BeerDBHelper.COL_DESCRIPTION, b.getString("description"));

                // <TODO> fix this to Double
//                Log.d(TAG, "Putting rating " + b.getString("rating"));
                values.put(BeerDBHelper.COL_RATING, b.getString("rating"));

//                Log.d(TAG, "Putting brewery name " + b.getString("brewery_name"));
                values.put(BeerDBHelper.COL_BREWERY_NAME, b.getString("brewery_name"));

                URL url = new URL(fetchImageUrl + b.getString("image").trim());
                try {
                    URLConnection ucon = url.openConnection();
                    InputStream is = ucon.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is, 128);
                    ByteArrayBuffer baf = new ByteArrayBuffer(128);
                    int current = 0;
                    while ((current = bis.read()) != -1) {
                        baf.append((byte) current);
                    }
//                    Log.d(TAG, "Putting image");
                    values.put(BeerDBHelper.COL_IMAGE, baf.toByteArray());
                } catch (IOException e) {

                    e.printStackTrace();
                }

                if (add) {
                    Log.d(TAG, "Adding Beer");
                    beerDbSource.addBeer(values);
                } else {
                    Log.d(TAG, "Updating Beer");
                    beerDbSource.updateBeer(b.getLong("ID"), values);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        beerDbSource.close();

        return true;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
