package com.anzym.android.kegdroidkiosk.controllers;

/**
 * Created by pcarff on 1/15/16.
 */
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.anzym.android.kegdroidkiosk.KegDroidKioskMainActivity;

import com.anzym.android.kegdroidkiosk.R;
import com.anzym.android.kegdroidlibrary.models.BeerOrder;

public class VolumeSelectionController {

    private static final String TAG = VolumeSelectionController.class.getSimpleName();
    private KegDroidKioskMainActivity kdA;
    private BeerTapController btc;
    View volumeSelector;
    private RadioGroup beerSelectorGroup;
    private RadioButton firstSelectorButton;
    private RadioButton secondSelectorButton;
    private RadioButton thirdSelectorButton;
    BeerOrder beerOrder;
    private Button pourButton;
    private Button cancelButton;
    AlphaAnimation alphaAniIn;
    AlphaAnimation alphaAniOut;

    int volumeToPour = 0;

    public VolumeSelectionController(KegDroidKioskMainActivity ma, BeerTapController btc, ViewGroup vg) {
        this.kdA = ma;
        this.btc = btc;
        this.volumeSelector = vg;
        this.attachToView();
        beerOrder = new BeerOrder();

    }

    public void attachToView() {

        alphaAniIn = new AlphaAnimation(1, 0);
        alphaAniIn.setDuration(1000);
        alphaAniOut = new AlphaAnimation(0, 1);
        alphaAniOut.setDuration(1000);

        firstSelectorButton = (RadioButton) volumeSelector.findViewById(R.id.first_selector_button);
        secondSelectorButton = (RadioButton) volumeSelector.findViewById(R.id.second_selector_button);
        thirdSelectorButton = (RadioButton) volumeSelector.findViewById(R.id.third_selector_button);

        beerSelectorGroup = (RadioGroup) volumeSelector.findViewById(R.id.beer_selector_group);
        beerSelectorGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.first_selector_button:
                        volumeToPour = kdA.kioskApp.SMALL_GLASS;
                        break;
                    case R.id.second_selector_button:
                        volumeToPour = kdA.kioskApp.MEDIUM_GLASS;
                        break;
                    case R.id.third_selector_button:
                        volumeToPour = kdA.kioskApp.LARGE_GLASS;
                        break;
                }
                beerOrder.setOrderSize(volumeToPour);
                Log.d(TAG, "Selected: " + beerOrder.getOrderSize() + " oz.");
            }

        });

        pourButton = (Button) volumeSelector.findViewById(R.id.pour_button);
        pourButton.setOnClickListener(new PourBeerClickListener());

        cancelButton = (Button) volumeSelector.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new CancelClickListener());
        hideVsfButtons();
    }

    public void showVsfButtons() {
        btc.showVsfButtons();
        volumeSelector.setVisibility(View.VISIBLE);
        //volumeSelector.startAnimation(alphaAniIn);
        //volumeSelector.setVisibility(View.VISIBLE);
    }

    public void hideVsfButtons() {
        //volumeSelector.startAnimation(alphaAniOut);
        volumeSelector.setVisibility(View.GONE);
    }

    private class PourBeerClickListener implements OnClickListener {
        public void onClick(View v) {
            if (((Button) v).isPressed()) {
                kdA.disableBeerSelectors();
                kdA.hideVsfButtons();
                Log.d(TAG, "Pouring " + beerOrder.getOrderSize());
                Log.d(TAG, "On the " + btc.mKeg.getTapNumber() + " tap.");
                beerOrder.setBeerId(btc.mKeg.getBeerId());
                beerOrder.setTapNumber(btc.mKeg.getTapNumber());
                //beerOrder.setOrderSize(volumeToPour);
                kdA.pouring = new PouringDialog(kdA, beerOrder);
                kdA.pouring.show();
                Log.d(TAG, "Pour Button pressed state: " + pourButton.isPressed());
                pourButton.setPressed(false);
                Log.d(TAG, "Pour Button pressed state: " + pourButton.isPressed());
            }
        }
    }

    private class CancelClickListener implements OnClickListener {
        public void onClick(View v) {
            if (((Button) v).isPressed()) {
                kdA.enableBeerSelectors();
                Log.d(TAG, "Canceling ");

                // <TODO>
                kdA.cancelPour();
                cancelButton.setPressed(false);
                kdA.hideVsfButtons();
            }
        }
    }
}
