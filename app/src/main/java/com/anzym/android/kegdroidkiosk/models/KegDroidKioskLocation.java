package com.anzym.android.kegdroidkiosk.models;

/**
 * Created by pcarff on 1/15/16.
 */
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.anzym.android.kegdroidkiosk.KegDroidKioskApplication;

public class KegDroidKioskLocation {

    private static String TAG = KegDroidKioskLocation.class.getSimpleName();
    private double lat;
    private double lon;
    LocationManager locManager;

    public KegDroidKioskLocation() {
        LocationManager locManager;
        locManager = (LocationManager) KegDroidKioskApplication.getInstance()
                .getSystemService(Context.LOCATION_SERVICE);
        Location location = locManager.getLastKnownLocation(
                LocationManager.NETWORK_PROVIDER);
        Log.d(TAG, "location " + location);
        if (location != null) {
            this.setLat(location.getLatitude());
            this.setLon(location.getLongitude());
        }
        else {
            Log.e(TAG, "location was NULL!");
        }
    }

    public void setLat(double l) {
        this.lat = l;
    }

    public void setLon(double l) {
        this.lon = l;
    }

    public double getLat() {
        return this.lat;
    }

    public double getLon() {
        return this.lon;
    }

}
