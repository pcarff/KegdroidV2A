package com.anzym.android.kegdroidkiosk.controllers;

/**
 * Created by pcarff on 1/15/16.
 */
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anzym.android.kegdroidkiosk.KegDroidKioskMainActivity;
import com.anzym.android.kegdroidkiosk.R;
import com.anzym.android.kegdroidkiosk.models.Keg;
import com.anzym.android.kegdroidlibrary.database.BeerDataSource;
import com.anzym.android.kegdroidlibrary.models.Beer;

public class BeerTapController {

    private static final String TAG = BeerTapController.class.getSimpleName();
    KegDroidKioskMainActivity kdA;
    Keg mKeg;
    Beer mBeer;

    View beerSelector;
    public VolumeSelectionController vsfController;
    ViewGroup volumeSelector;

    private TextView fBeerName;
    private TextView breweryName;
    private TextView styleView;
    private TextView abvValue;
    private TextView ibuValue;
    private TextView descView;
    private ImageView beerView;
    private RatingBar beerRatingBar;
    private ImageView emptyFlag;


    public BeerTapController(KegDroidKioskMainActivity ma, Keg kg) {
        this.kdA = ma;
        this.mKeg = kg;

    }

    public void attachToView( ViewGroup paneContainer) {
        fBeerName = (TextView) paneContainer.findViewById(R.id.beer_name);
        breweryName = (TextView) paneContainer.findViewById(R.id.brewery_name);
        styleView = (TextView) paneContainer.findViewById(R.id.beer_product_type);
        abvValue = (TextView) paneContainer.findViewById(R.id.beer_abv);
        ibuValue = (TextView) paneContainer.findViewById(R.id.beer_ibu);
        descView = (TextView) paneContainer.findViewById(R.id.beer_product_description);
        beerView = (ImageView) paneContainer.findViewById(R.id.beer_product_icon);
        beerRatingBar = (RatingBar) paneContainer.findViewById(R.id.beer_rating_bar);
        emptyFlag = (ImageView) paneContainer.findViewById(R.id.empty_image);

        //<TODO>  Need to check this one!!
        beerSelector = (RelativeLayout) paneContainer.getChildAt(0);
        beerSelector.setOnClickListener(new BeerSelectorListener());
        beerSelector.setClickable(true);
        volumeSelector = (ViewGroup) paneContainer.getChildAt(1);
        vsfController = new VolumeSelectionController(kdA, this, volumeSelector);

        setBeerData(mKeg.getBeerId());
    }

    private void setBeerData(long beer_id) {
        Beer mBeer = getBeer(beer_id);
        Log.d(TAG, "setBeerData beer= " + mBeer);

        if (mBeer != null) {
            fBeerName.setText(mBeer.getName());
            breweryName.setText(mBeer.getBrewery_name());
            styleView.setText(mBeer.getStyle());
            abvValue.setText("ABV: " + mBeer.getAbv());
            ibuValue.setText("IBU: " + mBeer.getIbu());
            descView.setText(mBeer.getDescription());

            beerView.setImageBitmap(mBeer.getImage());

            /*
             * <TODO> fix rating. String rateString = mBeer.rating; Log.d(TAG,
             * "Beer Rating: " + rateString); if (mBeer.rating != null) { rating
             * = Integer.parseInt(mBeer.rating); }
             * beerRatingBar.setRating(rating);
             */
            updateEmptyImage(emptyFlag);
        }
    }


    public Beer getBeer(Long id) {
        BeerDataSource mBDS = new BeerDataSource(kdA);
        mBDS.open();
        Beer beer = mBDS.getBeer(id);
        Log.d(TAG, "beer= " + beer);
        mBDS.close();
        return beer;
    }

    private class BeerSelectorListener implements OnClickListener {
        public void onClick(View v) {
            if (beerSelector.isClickable()) {
                Log.d(TAG, "Tap # " + mKeg.getTapNumber() + " was pressed.");
                //if (!isEmpty) {
                vsfController.showVsfButtons();
                //}
            }
        }
    }

    public void showVsfButtons() {
        kdA.showVsfButtons(this.mKeg.getTapNumber());
    }

    public void disableBeerSelector() {
        this.beerSelector.setClickable(false);
    }

    public void enableBeerSelector() {
        if(!mKeg.checkIfEmpty()) {
            this.beerSelector.setClickable(true);
        }
    }

    public void refreshKeg() {
        setBeerData(mKeg.getBeerId());
        mKeg.refreshKeg();
        updateEmptyImage(emptyFlag);
        beerSelector.invalidate();
    }

    public void updateKegAfterPour(int vol) {
        mKeg.updateKegAfterPour(vol);
        updateEmptyImage(emptyFlag);
    }

    public void updateEmptyImage(ImageView ef) {
        if (mKeg.checkIfEmpty()) {
            ef.setVisibility(View.VISIBLE);
            beerSelector.setClickable(false);
        } else {
            ef.setVisibility(View.GONE);
            beerSelector.setClickable(true);
        }
    }
}
