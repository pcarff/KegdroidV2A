package com.anzym.android.kegdroidkiosk.controllers;

/**
 * Created by pcarff on 1/15/16.
 */

import android.widget.TextView;

import com.anzym.android.kegdroidkiosk.KegDroidKioskMainActivity;
import com.anzym.android.kegdroidkiosk.R;
import java.text.DecimalFormat;

public class InputController extends AccessoryController {

    private static String TAG = InputController.class.getSimpleName();

    private TextView mTempView;

    private final DecimalFormat mTemperatureFormatter = new
            DecimalFormat("###");

    public InputController(KegDroidKioskMainActivity hostActivity) {
        super(hostActivity);
        mTempView = (TextView) findViewById(R.id.temperature_value);
    }

    @Override
    protected void onAccesssoryAttached() {
    }

    public void setTemperature(int temperatureFromArduino) {
        // rcvd temp in celsius, need to convert to farenheit
        double tempF = (9.0 / 5.0) * temperatureFromArduino + 32;
        //Hack to limit size of temp field. Need to fix temp sensor (or remove)
        if (tempF < 1000) {
            mTempView.setText(mTemperatureFormatter.format(tempF));
        } else {
            mTempView.setText(mTemperatureFormatter.format(00));
        }
    }

    public void onTemperature(int temperature) {
        setTemperature(temperature);
    }

}
