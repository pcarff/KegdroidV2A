package com.anzym.android.kegdroidlibrary.tasks;

/**
 * Created by pcarff on 1/15/16.
 */
//import com.anzym.android.kegdroidlibrary.R;


import android.content.Context;

import com.anzym.android.kegdroidkiosk.R;

public class Utils {

    public static String fetchUrl(Context context, String id) {
        String apiKey = context.getResources().getString(R.string.gplus_api_key);

        String fetchUrl = "https://www.googleapis.com/plus/dogfood/people/" + id
                + "?key=" + apiKey;
        return fetchUrl;
    }

}
