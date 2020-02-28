package com.anzym.android.kegdroidkiosk.task;

/**
 * Created by pcarff on 1/15/16.
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.anzym.android.kegdroidkiosk.KegDroidKioskMainActivity;
import com.anzym.android.kegdroidkiosk.KegDroidKioskApplication;

public class UpdateKegDroidCloud {

    private static String TAG = UpdateKegDroidCloud.class.getSimpleName();
    KegDroidKioskApplication kioskApp;
    private static String update_kegdroid_url = "http://kegdroidcloud.anzym.com/kegdroids";

    // private static String update_kegdroid_url =
    // "http://kegdroidcloud.anzym.com/kegdroids/register";

    private String kegDroidId;
    // <TODO> add send name
    private String kegDroidName;

    public UpdateKegDroidCloud(KegDroidKioskApplication kApp) {
        this.kioskApp = kApp;
        this.kegDroidId = kioskApp.getAndroidId();
        this.kegDroidName = kioskApp.getName();
    }

    public UpdateKegDroidCloud(String kdId, String name) {
        this.kegDroidId = kdId;
        this.kegDroidName = name;
    }

    public String getKegDroidName() {
        return kegDroidName;
    }

    public void setKegDroidName(String kegDroidName) {
        this.kegDroidName = kegDroidName;
    }

    public String getKegDroidId() {
        return kegDroidId;
    }

    public void setKegDroidId(String kegDroidId) {
        this.kegDroidId = kegDroidId;
    }

    @SuppressWarnings("unchecked")
    public void register() {
        /*
         * Build data to send to app engine
         */
        String kLat = Double.toString(kioskApp.getLat());
        String kLon = Double.toString(kioskApp.getLon());
        Log.d(TAG, "lat: " + kLat);
        Log.d(TAG, "lon: " + kLon);
        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(4);
        nameValuePairs
                .add(new BasicNameValuePair("android_id", kioskApp.getAndroidId()));
        nameValuePairs.add(new BasicNameValuePair("kegdroid_name", kioskApp.getName()));
        nameValuePairs.add(new BasicNameValuePair("kegdroid_lat", kLat));
        nameValuePairs.add(new BasicNameValuePair("kegdroid_lon", kLon));

        PostToAppEngine post = new PostToAppEngine(update_kegdroid_url);
        Log.d(TAG, "nameValuePairs " + nameValuePairs);
        post.execute(nameValuePairs);

    }

    @SuppressWarnings("unchecked")
    public void update() {
        /*
         * Build data to send to app engine
         */
        String kLat = Double.toString(kioskApp.getLat());
        String kLon = Double.toString(kioskApp.getLon());
        Log.d(TAG, "lat: " + kLat);
        Log.d(TAG, "lon: " + kLon);
        String beerIdTapOne = Long.toString(kioskApp.kdKegs[1].getBeerId());
        String beerIdTapZero = Long.toString(kioskApp.kdKegs[0].getBeerId());
        Log.d(TAG, "beer on zero: " + beerIdTapZero);
        Log.d(TAG, "beer on one: " + beerIdTapOne);
        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(6);
        nameValuePairs
                .add(new BasicNameValuePair("android_id", kioskApp.getAndroidId()));
        nameValuePairs.add(new BasicNameValuePair("kegdroid_name", kioskApp.getName()));
        nameValuePairs.add(new BasicNameValuePair("beer_id_tap_zero", beerIdTapZero));
        nameValuePairs.add(new BasicNameValuePair("beer_id_tap_one", beerIdTapOne));
        nameValuePairs.add(new BasicNameValuePair("kegdroid_lat", kLat));
        nameValuePairs.add(new BasicNameValuePair("kegdroid_lon", kLon));

        // Add Beer data
        // nameValuePairs.add(new BasicNameValuePair("user", this.getUserId()));
        // nameValuePairs.add(new BasicNameValuePair("beer", this.getBeerId()));
        // nameValuePairs.add(new BasicNameValuePair("volume",
        // this.getVolume()));

        PostToAppEngine post = new PostToAppEngine(update_kegdroid_url);
        post.execute(nameValuePairs);

    }

    private class PostToAppEngine extends AsyncTask<List<BasicNameValuePair>, Void, String> {
        private String url;

        public PostToAppEngine(String url) {
            super();
            this.url = url;
        }

        @Override
        protected String doInBackground(List<BasicNameValuePair>... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(this.url);

            Log.d(TAG, "register kegdroid url: " + this.url);

            HttpResponse response;

            try {
                // Add your data
                HttpEntity testEntity = new UrlEncodedFormEntity(params[0]);
                System.out.println(testEntity.toString());
                httppost.setEntity(testEntity);
                // Execute HTTP Post Request
                response = httpclient.execute(httppost);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
                // TODO Auto-generated catch block
            } catch (IOException e) {
                e.printStackTrace();
                // TODO Auto-generated catch block
            }

            return "200";
        }

    }

}
