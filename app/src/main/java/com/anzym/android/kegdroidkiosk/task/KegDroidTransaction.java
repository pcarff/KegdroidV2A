package com.anzym.android.kegdroidkiosk.task;

/**
 * Created by pcarff on 1/15/16.
 */
import android.os.AsyncTask;
import android.util.Log;

import com.anzym.android.kegdroidkiosk.KegDroidKioskApplication;
import com.anzym.android.kegdroidkiosk.KegDroidKioskMainActivity;
import com.anzym.android.kegdroidlibrary.models.BeerOrder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KegDroidTransaction {

    private static final String TAG = KegDroidTransaction.class.getSimpleName();

    private String kegDroidId;
    private String kegDroidName;
    private String userGPlusId;
    private BeerOrder beerOrder;

    KegDroidKioskApplication kioskApp;

    //<TODO> CHANGE THIS URL
    //private static final String pourUrl = "http://kegdroidstats.anzym.com/pours?";
    private static final String pourUrl = "http://kegdroidcloud.anzym.com/pours/add";
    private KegDroidKioskMainActivity kdA;

    public KegDroidTransaction (KegDroidKioskMainActivity ma, BeerOrder bo) {
        this.kdA = ma;
        this.beerOrder = bo;
        this.kioskApp = KegDroidKioskApplication.getInstance();
    }

    public void send() {

        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(7);
        nameValuePairs.add(new BasicNameValuePair("android_id", kioskApp.getAndroidId()));
        nameValuePairs.add(new BasicNameValuePair("kegdroid_name", kioskApp.getName()));
        nameValuePairs.add(new BasicNameValuePair("beer_id",
                new Long(this.beerOrder.getBeerId()).toString()));
        nameValuePairs.add(new BasicNameValuePair("volume_poured",
                new Integer(this.beerOrder.getOrderSize()).toString()));
        nameValuePairs.add(new BasicNameValuePair("gplus_id", kioskApp.getUser().getGPlusId()));
        nameValuePairs.add(new BasicNameValuePair("tap_number",
                new Integer(this.beerOrder.getTapNumber()).toString()));
        nameValuePairs.add(new BasicNameValuePair("remaining_keg_volume",
                new Float(kioskApp.kdKegs[beerOrder.getTapNumber()]
                        .getCurrentVolume()).toString()));
        PostToAppEngine post = new PostToAppEngine(KegDroidTransaction.pourUrl);

        Log.d(TAG, "SENDING TRANSACTION");
        Log.d(TAG, "URL: " + KegDroidTransaction.pourUrl);
        Log.d(TAG, " valuePairs: " + nameValuePairs);
        Log.d(TAG, "posting: " + post);
        post.execute(nameValuePairs);

        Log.d(TAG,"Resetting Drinker data");
        kdA.resetDrinker();
        kdA.updatePrefs();

    }

    private class PostToAppEngine extends AsyncTask<List<BasicNameValuePair>, Void, String>{

        private String url;
        public PostToAppEngine(String url){
            super();
            Log.d(TAG, "sending url: " + url);
            this.url = url;
        }

        @Override
        protected String doInBackground(List<BasicNameValuePair>... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(this.url);
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
