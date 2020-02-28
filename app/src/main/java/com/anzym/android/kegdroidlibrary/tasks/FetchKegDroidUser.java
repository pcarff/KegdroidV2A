package com.anzym.android.kegdroidlibrary.tasks;

/**
 * Created by pcarff on 1/15/16.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.anzym.android.kegdroidlibrary.models.KegDroidUser;


public class FetchKegDroidUser extends AsyncTask<String, Void, KegDroidUser> {

    private String TAG = FetchKegDroidUser.class.getSimpleName();
    private OnTaskCompleted listener;


    private KegDroidUser kUser;
    private String imageSize = "250";

    public FetchKegDroidUser(KegDroidUser kUser, OnTaskCompleted listener) {
        super();
        this.kUser = kUser;
        this.listener=listener;
    }

    @Override
    protected KegDroidUser doInBackground(String... params) {
        String result = "{}";
        System.out.println("Fetching user");
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(params[0]);
        Log.d(TAG, "params[0]: " + params[0]);
        HttpResponse response;

        try {
            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                result = convertStreamToString(instream);
                instream.close();
                //Log.d(TAG, "Result: " + result);
            }
            if (result != null) {
                JSONObject userInfo = new JSONObject(result);
                JSONObject imageInfo = new JSONObject(userInfo.getString("image"));
                JSONObject userNameInfo = new JSONObject(userInfo.getString("name"));
                String imageUrl = imageInfo.getString("url");

                imageUrl = imageUrl.substring(0, imageUrl.length() - 2);
                imageUrl += imageSize;
                kUser.setImageUrl(imageUrl);
                kUser.setDisplayName(userInfo.getString("displayName"));
                kUser.setGivenName(userNameInfo.getString("givenName"));
                kUser.setGPlusId(userInfo.getString("id"));
            } else {
                Log.d(TAG, "result = null!");
            }
            Log.d(TAG, "User: " + kUser.getDisplayName());

        } catch (Exception e) {
            Log.e(TAG, "Error" + e);
        }

        return kUser;
    }

    protected void onPostExecute(KegDroidUser kdUser){

        listener.onTaskCompleted();
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