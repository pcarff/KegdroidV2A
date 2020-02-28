package com.anzym.android.kegdroidkiosk.controllers;

/**
 * Created by pcarff on 1/15/16.
 */
import com.anzym.android.kegdroidkiosk.KegDroidKioskMainActivity;

public class OutputController extends AccessoryController {

    public OutputController(KegDroidKioskMainActivity hostActivity) {
        super(hostActivity);
    }

    @Override
    protected void onAccesssoryAttached() {
        // TODO look at seting up flow sensors/solenoid valves
        // setupLedController(1, R.id.leds1);

    }

    /*
     * TODO Need to look at this as model for implementing comms to arduino i.e
     * sending messages.... - Tap Solenoid Valves - Flow Sensors
     */
    /*
     * private void setupLedController(int index, int viewId) {
     * ColorLEDController ledC = new ColorLEDController(mHostActivity, index,
     * getResources(), mVertical); ledC.attachToView((ViewGroup)
     * findViewById(viewId)); }
     */

}