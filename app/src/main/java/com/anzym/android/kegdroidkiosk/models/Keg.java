package com.anzym.android.kegdroidkiosk.models;

/**
 * Created by pcarff on 1/15/16.
 */
import android.util.Log;
import android.view.View;

import com.anzym.android.kegdroidkiosk.ui.KegLevelGuage;

public class Keg {

    private static final String TAG = Keg.class.getSimpleName();

    View kegGuageView;

    public static final int KEG_SIZE_15 = 15; // 15 Gal sanke keg
    public static final int KEG_SIZE_5 = 5; // 5 Gal "corny" keg - homebrews
    static final double PULSES_PER_OZ = 172.9;
    static final double OZ_PER_GAL = 128.0;

    static final int LARGE_GLASS = 12;
    static final int MEDIUM_GLASS = 8;
    static final int SMALL_GLASS = 2;

    private boolean isEmpty = true;

    protected long beer_id = 0;  // Which beer is in this keg.
    protected int tapNumber = -1;  // Which tap this keg is connected to.
    protected double currentVolume = 5;  //Current volume (gallons) of beer left in keg.
    protected int kegSize = KEG_SIZE_5;
    protected KegLevelGuage klg;

    public long getBeerId() {
        return beer_id;
    }
    public void setBeerId(long mBeer) {
        this.beer_id = mBeer;
    }
    public int getTapNumber() {
        return tapNumber;
    }
    public void setTapNumber(int tapNumber) {
        this.tapNumber = tapNumber;
    }
    public double getCurrentVolume() {
        return currentVolume;
    }
    public void setCurrentVolume(double currentVolume) {
        this.currentVolume = currentVolume;
    }

    public int getKegSize() {
        return kegSize;
    }
    public void setKegSize(int kegSize) {
        this.kegSize = kegSize;
    }

    public Keg(int tn) {
        this.tapNumber = tn;
    }

    public Keg(int tn, int ks) {
        this(tn);
        this.kegSize = ks;
    }
    public Keg(int tn, int ks, long br) {
        this(tn, ks);
        this.beer_id = br;
    }

    public Keg(int tn, int ks, long br, float cv) {
        this(tn, ks, br);
        this.currentVolume = cv;
    }

    public void setKegLevelGuage(KegLevelGuage k) {
        this.klg = k;
        refreshKeg();
    }

    public KegLevelGuage getKegLevelGuage() {
        return this.klg;
    }


    public boolean checkIfEmpty() {
        return ((getCurrentVolume() * OZ_PER_GAL) < LARGE_GLASS) ? true:false;
    }

    public void refreshKeg() { // volume int gallons.
        this.klg.setKegLevelImage(getKegSize(), getCurrentVolume());
        this.isEmpty = checkIfEmpty();
        //Update EMPTY status.
    }

    public void updateKegAfterPour(float vol) {
        Log.d(TAG, "Updating keg " + this.tapNumber + " volume post pour:");
        Log.d(TAG, " " + currentVolume);
        this.currentVolume -= (vol/OZ_PER_GAL);

        Log.d(TAG, " after pour " + currentVolume);
        Log.d(TAG, "calling refreshKeg");
        refreshKeg();
    }

}
