package com.anzym.android.kegdroidkiosk;

/**
 * Created by pcarff on 1/15/16.
 */


import android.app.Application;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;
import android.util.Log;

import com.anzym.android.kegdroidkiosk.models.Keg;
import com.anzym.android.kegdroidkiosk.models.KegDroidKioskLocation;
import com.anzym.android.kegdroidlibrary.database.BeerDataSource;
import com.anzym.android.kegdroidlibrary.models.KegDroidUser;
import com.anzym.android.kegdroidlibrary.tasks.FetchBeerTask;

public class KegDroidKioskApplication extends Application {
    private static final String TAG = KegDroidKioskApplication.class.getSimpleName();

    private static KegDroidKioskApplication instance;
    private static String name;
    private static String android_id;
    private static final String KEGDROID_PREFS = "KegDroidPrefs";

    private static boolean nfcEnabled;

    public static int PULSES_PER_OZ = 173;

    public static int LARGE_GLASS = 12;
    public static int MEDIUM_GLASS = 8;
    public static int SMALL_GLASS = 2;

    public int LEFT_TAP = 0;
    public int RIGHT_TAP = 1;
    public int NUM_KEGS = 2; // Should this be configurable in a setup?
    public Keg[] kdKegs;
    public KegDroidUser kdUser;

    private KegDroidKioskLocation kdLoc;

    public static KegDroidKioskApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        KegDroidKioskApplication.instance = this;

        // Initialization
        kdKegs = new Keg[NUM_KEGS];
        for (int k = 0; k < NUM_KEGS; k++) {
            kdKegs[k] = new Keg(k);
        }
        kdUser = new KegDroidUser();
        // Get previous setting that were saved.
        Log.d(TAG, "Updating from Shared Preferences");
        SharedPreferences settings = getSharedPreferences(KEGDROID_PREFS, 0);
        KegDroidKioskApplication.name = settings.getString("kegdroid_name",
                getResources().getString(R.string.default_kegdroid_name));
        KegDroidKioskApplication.android_id = settings.getString("android_id",
                Secure.getString(getApplicationContext().getContentResolver(),
                        Secure.ANDROID_ID));
        kdKegs[LEFT_TAP].setBeerId(settings.getLong("left_keg_beer_id", 5006));
        kdKegs[RIGHT_TAP].setBeerId(settings.getLong("right_keg_beer_id", 5006));
        kdKegs[LEFT_TAP].setCurrentVolume(settings.getFloat("left_keg_volume", Keg.KEG_SIZE_5));
        kdKegs[RIGHT_TAP].setCurrentVolume(settings.getFloat("right_keg_volume", Keg.KEG_SIZE_5));
        kdKegs[LEFT_TAP].setKegSize(settings.getInt("left_keg_size", Keg.KEG_SIZE_5));
        kdKegs[RIGHT_TAP].setKegSize(settings.getInt("right_keg_size", Keg.KEG_SIZE_5));

        setNfcEnabled(settings.getBoolean("nfc_mode", false));

        // Setting location
        kdLoc = new KegDroidKioskLocation();

        BeerDataSource beerDBS = new BeerDataSource(this);
        beerDBS.open();
        if (beerDBS.checkDBEmpty()) {
            FetchBeerTask fetchBeerTask = new FetchBeerTask(getApplicationContext());
            fetchBeerTask.execute();
        }
        beerDBS.close();
    }

    public KegDroidUser getUser() {
        return kdUser;
    }

    public void setUser(KegDroidUser kdUser) {
        this.kdUser = kdUser;
    }

    public boolean isNfcEnabled() {
        return nfcEnabled;
    }

    public void setNfcEnabled(boolean nfc) {
        this.nfcEnabled = nfc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        KegDroidKioskApplication.name = name;
    }

    public String getAndroidId() {
        return android_id;
    }

    public void setAndroidId(String android_id) {
        KegDroidKioskApplication.android_id = android_id;
    }

    public double getLat() {
        return kdLoc.getLat();
    }

    public double getLon() {
        return kdLoc.getLon();
    }

}
