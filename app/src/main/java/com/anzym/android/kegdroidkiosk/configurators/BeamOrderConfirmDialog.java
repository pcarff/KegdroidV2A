package com.anzym.android.kegdroidkiosk.configurators;

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
import com.anzym.android.kegdroidkiosk.controllers.PouringDialog;
import com.anzym.android.kegdroidlibrary.database.BeerDataSource;
import com.anzym.android.kegdroidlibrary.models.Beer;
import com.anzym.android.kegdroidlibrary.models.BeerOrder;
import com.anzym.android.kegdroidlibrary.tasks.FetchKegDroidUser;
import com.anzym.android.kegdroidlibrary.tasks.OnTaskCompleted;
import com.anzym.android.kegdroidlibrary.tasks.LoadKegDroidUserImage;
import com.anzym.android.kegdroidlibrary.tasks.Utils;


public class BeamOrderConfirmDialog extends Dialog {

    private static final String TAG = BeamOrderConfirmDialog.class.getSimpleName();

    private FetchKegDroidUser mFetchUserTask;

    TextView orderView;
    TextView userName;
    ImageView userImage;
    ImageView directionArrow;
    Button confirmButton;
    Button cancelButton;

    String orderSize;
    String order;

    Beer beer;

    LoadKegDroidUserImage mLoadImageTask;
    OnTaskCompleted fetchCompleteListener;

    PouringDialog mPouringDialog;

    KegDroidKioskMainActivity kdA;
    private BeerOrder beerOrder;

    public BeamOrderConfirmDialog(Context context, BeerOrder bo) {
        super(context);
        this.kdA = (KegDroidKioskMainActivity) context;
        this.beerOrder = bo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Received BEAM ORDER");
        setContentView(R.layout.dialog_beam_order_confirm);
        userName = (TextView) findViewById(R.id.beam_user_name);
        userName.setText(kdA.kioskApp.getUser().getGivenName());
        userImage = (ImageView) findViewById(R.id.beam_user_image);
        orderView = (TextView) findViewById(R.id.order);
        Log.d(TAG, "beer id: " + beerOrder.getBeerId());
        beer = getBeer(beerOrder.getBeerId());
        order = beerOrder.getOrderSize() + " oz " + beer.getName() + " on tap "
                + beerOrder.getTapNumber();
        orderView.setText(order);

        fetchCompleteListener = new OnTaskCompleted() {

            @Override
            public void onTaskCompleted() {
                mLoadImageTask = new LoadKegDroidUserImage(userImage);
                mLoadImageTask.execute(kdA.kioskApp.getUser().getImageUrl());
            }
        };
        directionArrow = (ImageView) findViewById(R.id.which_tap_direction_arrow);
        /*
         * <TODO> Add logic to show which tap to go to. Maybe eventually turn on
         * a light
         */
        mFetchUserTask = new FetchKegDroidUser(kdA.kioskApp.getUser(), fetchCompleteListener);
        mFetchUserTask.execute(Utils.fetchUrl(kdA,
                new String(kdA.kioskApp.getUser().getGPlusId())));

        confirmButton = (Button) findViewById(R.id.dialog_confirm_button);
        confirmButton.setOnClickListener(new ConfirmButtonClickListener());
        cancelButton = (Button) findViewById(R.id.dialog_cancel_button);
        cancelButton.setOnClickListener(new CancelButtonClickListener());

        getWindow().getDecorView().setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
    }

    private class ConfirmButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // Launch pouring dialog and then dismiss
            kdA.pouring = new PouringDialog(kdA, beerOrder);
            kdA.pouring.show();
            BeamOrderConfirmDialog.this.dismiss();
        }

    }

    private class CancelButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            kdA.cancelPour();
            BeamOrderConfirmDialog.this.dismiss();
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
}
