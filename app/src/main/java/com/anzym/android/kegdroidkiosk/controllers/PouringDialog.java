package com.anzym.android.kegdroidkiosk.controllers;

/**
 * Created by pcarff on 1/15/16.
 */
import android.app.Dialog;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.anzym.android.kegdroidkiosk.KegDroidKioskMainActivity;
import com.anzym.android.kegdroidkiosk.R;
import com.anzym.android.kegdroidkiosk.task.KegDroidTransaction;
import com.anzym.android.kegdroidlibrary.models.BeerOrder;

import java.io.IOException;
import java.text.DecimalFormat;

public class PouringDialog extends Dialog {

    private static String TAG = PouringDialog.class.getSimpleName();
    BeerOrder beerOrder;
    KegDroidKioskMainActivity kdA;
    KegDroidTransaction kdTrans;

    private TextView amountPoured;
    private ImageView glassPour;
    private Button dismissButton;

    private int pulsesReceived;

    private final DecimalFormat mFlowFormatter = new DecimalFormat(
            "##0.0");

    public PouringDialog(Context context, BeerOrder bo) {
        super(context);
        this.beerOrder = bo;
        this.kdA = (KegDroidKioskMainActivity) context;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pouring);

        Log.d(TAG, "Launching Pouring Dialog");

        amountPoured = (TextView) findViewById(R.id.amount_poured);
        glassPour = (ImageView) findViewById(R.id.amount_poured_image);

        dismissButton = (Button) findViewById(R.id.pouring_dialog_dismiss_button);
        dismissButton.setOnClickListener(new DismissButtonClickListener());
        getWindow().getDecorView().setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
        Log.d(TAG, "Starting Pour");
        startPouring();

        setTitle("Pouring...");
    }

    private class DismissButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // Save the data
            // <TODO> Figure out this process.
            //kdA.pourDismiss(pulsesReceived);
            closeTap();
            kdA.enableBeerSelectors();
            PouringDialog.this.dismiss();
        }
    }

    public void startPouring() {
        Log.d(TAG, "Opening Tap");
        Log.d(TAG, "Pouring "+ beerOrder.getOrderSize() + " oz on tap: "
                + beerOrder.getTapNumber());
        openTap(beerOrder.getTapNumber());
    }

    public void openTap(int tn) {
        byte [] buffer = new byte[3];
        Log.d(TAG, "openTap - sending " + beerOrder.getOrderSize() + " to tap " + tn);
        if (tn != -1) {
            buffer[0] = 0xA;
            buffer[1] = (byte) tn;
            buffer[2] = (byte) beerOrder.getOrderSize();

            if (kdA.mOutputStream != null) {
                try {
                    kdA.mOutputStream.write(buffer);
                } catch (IOException e) {
                    Log.e(TAG, "write failed ", e);
                }
            }
        }else {
            Log.e(TAG, "wrong tap selected");
        }
    }

    public void closeTap() {
        byte [] buffer = new byte[3];
        buffer[0] = 0xC;
        buffer[1] = (byte)-1;

        if (kdA.mOutputStream != null) {
            try {
                kdA.mOutputStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "write failed ", e);
            }
        }
    }

    public void completePour(int volume) {
        updateStatus(volume);
        kdA.mBeerTapController[beerOrder.getTapNumber()]
                .updateKegAfterPour(volume/kdA.kioskApp.PULSES_PER_OZ);
        kdA.enableBeerSelectors();
        kdTrans = new KegDroidTransaction(kdA, beerOrder);
        kdTrans.send();
        PouringDialog.this.dismiss();
    }

    public void updateStatus(int pulses) {

        Log.d(TAG,"updating pour dialog status");
        this.pulsesReceived = pulses;
        // Log.d(TAG, "pouring volume = " + volume);
        double vol = (double) pulsesReceived / kdA.kioskApp.PULSES_PER_OZ;
        // Log.d(TAG, "pouring vol = " + vol);
        // insert method to update image
        double glassLevel = (vol / new Integer(beerOrder.getOrderSize())) * 10;
        Log.d(TAG, "glasslevel = " + glassLevel);
        glassPour.setImageResource(getPourProgressResource((int) glassLevel));
        amountPoured.setText(mFlowFormatter.format(vol) + "oz");

    }

    protected static int getPourProgressResource(int number) {
        int mResource = R.drawable.progress_0;
        // <TODO> Update with a better set of images and more resolution
        switch (number) {
            case 0:
                mResource = R.drawable.progress_0;
                break;
            case 1:
                mResource = R.drawable.progress_1;
                break;
            case 2:
                mResource = R.drawable.progress_2;
                break;
            case 3:
                mResource = R.drawable.progress_3;
                break;
            case 4:
                mResource = R.drawable.progress_4;
                break;
            case 5:
                mResource = R.drawable.progress_5;
                break;
            case 6:
                mResource = R.drawable.progress_6;
                break;
            case 7:
                mResource = R.drawable.progress_7;
                break;
            case 8:
                mResource = R.drawable.progress_8;
                break;
            case 9:
                mResource = R.drawable.progress_9;
                break;
            case 10:
                mResource = R.drawable.progress_10;
                break;
        }
        return mResource;
    }

}
