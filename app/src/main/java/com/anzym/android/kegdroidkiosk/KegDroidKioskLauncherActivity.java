package com.anzym.android.kegdroidkiosk;

/**
 * Created by pcarff on 1/15/16.
 */
import android.os.Bundle;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.util.Log;

public class KegDroidKioskLauncherActivity extends Activity {

    public static final String TAG = KegDroidKioskLauncherActivity.class.getSimpleName();

    static Intent createIntent(Activity activity) {

        Intent intent;
        Log.i(TAG, "starting kegdroid kiosk");
        intent = new Intent(activity, KegDroidKioskMainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = createIntent(this);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "unable to start KegDroidKioskMainActivity", e);
        }
        finish();
        /*
         * <TODO> Decide if I want to use a launcher at all.
         * Would check if Nexus 10 then provide option to
         * enable NFC MODE - otherwise would just go to base mode.
         */

    }


}
