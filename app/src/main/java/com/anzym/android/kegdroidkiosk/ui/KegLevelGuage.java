package com.anzym.android.kegdroidkiosk.ui;

/**
 * Created by pcarff on 1/15/16.
 */

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

//import com.anzym.android.kegdroidkiosk.R;

import com.anzym.android.kegdroidkiosk.KegDroidKioskApplication;
import com.anzym.android.kegdroidkiosk.R;
import com.anzym.android.kegdroidkiosk.models.Keg;

public class KegLevelGuage {

    private static final String TAG = KegLevelGuage.class.getSimpleName();
    private static final int NUM_KEG_LEVELS = 5;

    View kegLevelView;
    Context ctx;
    Keg keg;
    ImageView kegLevelImageView;

    public KegLevelGuage(ImageView klv) {
        this.kegLevelImageView = klv;
        ctx = KegDroidKioskApplication.getInstance();

    }

    public void attachToView(ViewGroup kegLevelGroup) {
        kegLevelImageView = (ImageView) kegLevelGroup.findViewById(R.id.keg_level);
        setKegLevelImage(5, 0);
    }

    public void setKegLevelImage(int kegSize, double volume) {
        int kegLevel = 0;
        int mResource = R.drawable.keg_level_0;
        kegLevel = (int) ((volume / kegSize) * this.NUM_KEG_LEVELS);
        if (kegLevel < 0) {
            kegLevel = 0;
        }

        switch (kegLevel) {
            case 0:
                mResource = R.drawable.keg_level_0;
                break;
            case 1:
                mResource = R.drawable.keg_level_1;
                break;
            case 2:
                mResource = R.drawable.keg_level_2;
                break;
            case 3:
                mResource = R.drawable.keg_level_3;
                break;
            case 4:
                mResource = R.drawable.keg_level_4;
                break;
            case 5:
                mResource = R.drawable.keg_level_4;
                break;
        }
        Log.d(TAG, "Updating Image " + kegLevel);
        kegLevelImageView.setImageResource(mResource);
        kegLevelImageView.invalidate();
    }

}
