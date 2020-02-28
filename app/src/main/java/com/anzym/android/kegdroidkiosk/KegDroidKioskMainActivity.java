package com.anzym.android.kegdroidkiosk;

/**
 * Created by pcarff on 1/15/16.
 */
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anzym.android.kegdroidkiosk.configurators.BeamOrderConfirmDialog;
import com.anzym.android.kegdroidkiosk.configurators.ConfigureKegDroidDialog;
import com.anzym.android.kegdroidkiosk.configurators.NFCEnableDialog;
import com.anzym.android.kegdroidkiosk.configurators.TapSetupDialog;
import com.anzym.android.kegdroidkiosk.controllers.BeerTapController;
import com.anzym.android.kegdroidkiosk.controllers.InputController;
import com.anzym.android.kegdroidkiosk.controllers.OutputController;
import com.anzym.android.kegdroidkiosk.controllers.PouringDialog;
import com.anzym.android.kegdroidkiosk.models.Keg;
import com.anzym.android.kegdroidkiosk.task.UpdateKegDroidCloud;
import com.anzym.android.kegdroidkiosk.ui.KegLevelGuage;
import com.anzym.android.kegdroidlibrary.models.BeerOrder;
import com.anzym.android.kegdroidlibrary.tasks.FetchBeerTask;


import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class KegDroidKioskMainActivity extends KegDroidKioskBaseActivity {

    private static final String TAG = KegDroidKioskMainActivity.class.getSimpleName();

    private InputController mInputController;
    private OutputController mOutputController;
    public BeerTapController[] mBeerTapController;

    TextView kdName;

    TapSetupDialog beerTapSetup;
    ConfigureKegDroidDialog configureKegDroid;
    public PouringDialog pouring;
    NFCEnableDialog nfcd;

    PendingIntent pendingIntent;
    NfcAdapter mNfcAdapter;
    IntentFilter[] intentFiltersArray;
    String[][] techListsArray;
    BeamOrderConfirmDialog beamOrderConfirmDailog;

    private KegLevelGuage[] mKegLevelGuage;

    public int LEFT_TAP = 0;
    public int RIGHT_TAP = 1;

    public KegDroidKioskMainActivity() {
        super();
        kioskApp = KegDroidKioskApplication.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Starting KegDroid");
        Log.d(TAG, "mAccessory: " + mAccessory);
        if (mAccessory != null) {
            showControls();
        } else {
            hideControls();
            // showControls(); //Used for testing without an arduino attached.
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_keg_droid_kiosk_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.update_database:
                FetchBeerTask fetchBeerTask = new FetchBeerTask(getApplicationContext());
                fetchBeerTask.execute();
                return true;
            case R.id.setup_left_tap:
                setupTap(LEFT_TAP);
                return true;
            case R.id.setup_right_tap:
                setupTap(RIGHT_TAP);
                return true;
            case R.id.update_kegdroid_to_cloud:
                updateKegDroidInCloud();
                return true;
            case R.id.configure_kegdroid:
                configureKegDroid();
                return true;
            case R.id.enable_nfc:
                enableNfc();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void enableControls(boolean enable) {
        if (enable) {
            showControls();
        } else {
            hideControls();
        }
    }

    protected void hideControls() {
        Log.d(TAG, "Hiding Controls");
        setContentView(R.layout.no_kegdroid_arduino);
        mInputController = null;
        mOutputController = null;
        // <TODO> Hide the menu.

    }

    protected void showControls() {
        Log.d(TAG, "Showing Controls");
        setContentView(R.layout.activity_kegdroid_kiosk_main);
        updateKegDroidNameView();

        mBeerTapController = new BeerTapController[kioskApp.NUM_KEGS];
        mKegLevelGuage = new KegLevelGuage[kioskApp.NUM_KEGS];
        mKegLevelGuage[LEFT_TAP] = new KegLevelGuage((ImageView) findViewById(R.id.left_keg_level));
        mKegLevelGuage[RIGHT_TAP] = new KegLevelGuage(
                (ImageView) findViewById(R.id.right_keg_level));
        kioskApp.kdKegs[LEFT_TAP].setKegLevelGuage(mKegLevelGuage[LEFT_TAP]);
        kioskApp.kdKegs[RIGHT_TAP].setKegLevelGuage(mKegLevelGuage[RIGHT_TAP]);
        mBeerTapController[LEFT_TAP] = setUpBeerTapController(kioskApp.kdKegs[LEFT_TAP],
                R.id.left_pane_container);
        mBeerTapController[RIGHT_TAP] = setUpBeerTapController(kioskApp.kdKegs[RIGHT_TAP],
                R.id.right_pane_container);

        if (kioskApp.isNfcEnabled()) {
            prepNFC();
        }

        mInputController = new InputController(this);
        mInputController.accessoryAttached();
        mOutputController = new OutputController(this);
        mOutputController.accessoryAttached();
    }

    public void updateKegDroidNameView() {
        kdName = (TextView) findViewById(R.id.kegdroid_name);
        kdName.setText(kioskApp.getName());
    }

    /*
     * NFC SETUP
     */

    public void prepNFC() {
        Log.d(TAG, "prepNFC");
        /*
         * <TODO> disable beer selector buttons (may add a color change so that
         * it is clear they are not selectable - or a dialog/toast that appears
         * when one is pressed informing the drinker they need to use NFC tag.
         */
        disableBeerSelectors();
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        Log.d(TAG, "setting IntentFilter");
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        Log.d(TAG, "IntentFilter: " + ndef);
        try {
            ndef.addDataType("*/*"); /*
                                      * Handles all MIME based dispatches. You
                                      * should specify only the ones that you
                                      * need.
                                      */
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        intentFiltersArray = new IntentFilter[] {
                ndef,
        };
        techListsArray = new String[][] {
                new String[] {
                        NfcF.class.getName()
                }
        };
    }

    public void onNewIntent(Intent intent) {
        Log.d(TAG, "RECEIVED BEAM DATA");
        BeerOrder beerOrder = new BeerOrder();
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        String type = null;
        try {
            type = new String(msg.getRecords()[0].getType(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "received type: " + type);
        Log.d(TAG, "expecting type: " + getString(R.string.nfc_type));
        if (type.equals(getString(R.string.nfc_type))) {

            // NdefRecord emailRecord = msg.getRecords()[0];
            NdefRecord nameRecord = msg.getRecords()[0];
            NdefRecord gPlusIdRecord = msg.getRecords()[1];
            NdefRecord androidIdRecord = msg.getRecords()[2];
            NdefRecord tapNumberRecord = msg.getRecords()[3];
            NdefRecord beerIdRecord = msg.getRecords()[4];
            NdefRecord orderSizeRecord = msg.getRecords()[5];
            NdefRecord imageUrlRecord = msg.getRecords()[6];

            try {
                // kioskApp.getUser().setEmailAddress(new String(
                // emailRecord.getPayload(), "UTF-8"));
                kioskApp.getUser().setGivenName(new String(
                        nameRecord.getPayload(), "UTF-8"));
                kioskApp.getUser().setGPlusId(new String(gPlusIdRecord
                        .getPayload(), "UTF-8"));
                beerOrder.setAndroidId(new String(androidIdRecord
                        .getPayload(), "UTF-8"));
                beerOrder.setTapNumber(new Integer(new String(
                        tapNumberRecord.getPayload(), "UTF-8")));
                beerOrder.setBeerId(new Long(new String(
                        beerIdRecord.getPayload(), "UTF-8")));
                beerOrder.setOrderSize(new Integer(new String(
                        orderSizeRecord.getPayload(), "UTF-8")));
                kioskApp.getUser().setImageUrl(new String(
                        imageUrlRecord.getPayload(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ;

            /*
             * <TODO< Verify user has access to this KegDroid OR do we need to
             * do that as user should have KegDroid in list in the app to begin
             * with. - FETCH USER DATA - CHECK ACCESS - CONFIRM ORDER -
             * "PREPARE GLASS"
             */
        }
        if (beerOrder.getAndroidId().equals(kioskApp.getAndroidId())) {
            setBeamOrderConfirmDialog(beerOrder);
        } else {
            Toast.makeText(this,
                    "You are ordering from the wrong KegDroid!!",
                    Toast.LENGTH_LONG).show();
        }

    }

    public void resetDrinker() {
        kioskApp.getUser().setGPlusId("");
    }

    private void setBeamOrderConfirmDialog(BeerOrder bo) {
        beamOrderConfirmDailog = new BeamOrderConfirmDialog(this, bo);
        beamOrderConfirmDailog.show();
    }

    public void secureNfc() {
        Log.d(TAG, "secureNfc");
        mNfcAdapter = null;
        enableBeerSelectors();
        ViewGroup vg = (ViewGroup) findViewById(R.id.application_pane_container);
        vg.invalidate();
    }

    /*
     * BEER CONTROLLER CONNECTORS
     */
    public void showVsfButtons(int tn) {
        if (tn == LEFT_TAP) {
            mBeerTapController[RIGHT_TAP].vsfController.hideVsfButtons();
        } else {
            mBeerTapController[LEFT_TAP].vsfController.hideVsfButtons();
        }
    }

    public void hideVsfButtons() {
        mBeerTapController[RIGHT_TAP].vsfController.hideVsfButtons();
        mBeerTapController[LEFT_TAP].vsfController.hideVsfButtons();
    }

    public void disableBeerSelectors() {
        mBeerTapController[LEFT_TAP].disableBeerSelector();
        mBeerTapController[RIGHT_TAP].disableBeerSelector();
    }

    public void enableBeerSelectors() {
        mBeerTapController[LEFT_TAP].enableBeerSelector();
        mBeerTapController[RIGHT_TAP].enableBeerSelector();

    }

    public BeerTapController getBeerTapController(int tn) {
        return mBeerTapController[tn];
    }

    private BeerTapController setUpBeerTapController(Keg kg, int beerPaneViewId) {
        BeerTapController btc = new BeerTapController(this, kg);
        btc.attachToView((ViewGroup) findViewById(beerPaneViewId));
        return btc;
    }

    // DIALOG CONNECTORS

    public void cancelPour() {
        hideVsfButtons();

        // <TODO closeTap();

        byte[] buffer = new byte[3];
        buffer[0] = 0xD; // Message "D"
        buffer[1] = (byte) -1;
        if (mOutputStream != null) {
            try {
                mOutputStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "write failed ", e);
            }
        }
        Log.d(TAG, "Canceled Pour");
    }

    /*
     * MENU Methods
     */
    protected void setupTap(int side) {
        beerTapSetup = new TapSetupDialog(this, side);
        beerTapSetup.show();
    }

    protected void configureKegDroid() {
        configureKegDroid = new ConfigureKegDroidDialog((KegDroidKioskMainActivity) this);
        configureKegDroid.show();
    }

    protected void updateKegDroidInCloud() {
        UpdateKegDroidCloud rKd = new UpdateKegDroidCloud(kioskApp);
        rKd.update();
    }

    protected void enableNfc() {
        Log.d(TAG, "setting up NFC");
        nfcd = new NFCEnableDialog(this);
        nfcd.show();
    }

    // MESSAGE HANDLERS

    protected void handleFlowMessage(FlowMsg fm) {
        if (pouring != null) {
            pouring.updateStatus(fm.getFlow());
        } else {
            Log.d(TAG, "Pouring is NULL");
        }
    }

    protected void handleVolumeMessage(VolumeMsg vm) {
        if (pouring != null) {
            pouring.completePour(vm.getVolume());
        } else {
            Log.d(TAG, "Pouring is NULL");
        }
    }

    protected void handleTemperatureMessage(TemperatureMsg tm) {
        if (mInputController != null) {
            mInputController.setTemperature(tm.getTemperature());
        }
    }
}
