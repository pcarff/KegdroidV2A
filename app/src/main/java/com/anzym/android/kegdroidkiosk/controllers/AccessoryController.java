package com.anzym.android.kegdroidkiosk.controllers;

/**
 * Created by pcarff on 1/15/16.
 */

import android.content.res.Resources;
import android.view.View;

import com.anzym.android.kegdroidkiosk.KegDroidKioskMainActivity;

public abstract class AccessoryController {

    protected KegDroidKioskMainActivity mHostActivity;

    public AccessoryController(KegDroidKioskMainActivity activity) {
        mHostActivity = activity;
    }

    protected View findViewById(int id) {
        return mHostActivity.findViewById(id);
    }

    protected Resources getResources() {
        return mHostActivity.getResources();
    }

    public void accessoryAttached() {
        onAccesssoryAttached();
    }

    abstract protected void onAccesssoryAttached();

}


