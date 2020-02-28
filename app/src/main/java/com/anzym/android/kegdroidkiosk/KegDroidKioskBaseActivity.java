package com.anzym.android.kegdroidkiosk;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;

import com.anzym.android.kegdroidlibrary.database.BeerDataSource;
import com.anzym.android.kegdroidlibrary.tasks.FetchBeerTask;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class KegDroidKioskBaseActivity extends Activity implements Runnable {

    private static final String TAG = KegDroidKioskBaseActivity.class.getSimpleName();

    private static final String ACTION_USB_PERMISSION =
            "com.anzym.android.kegdroid.action.USB_PERMISSION";
    private static final String KEGDROID_PREFS = "KegDroidPrefs";
    SharedPreferences settings;
    public KegDroidKioskApplication kioskApp;
    private UsbManager mUsbManager;
    private PendingIntent mPermissionIntent;
    private boolean mPermissionRequestPending;

    UsbAccessory mAccessory;
    ParcelFileDescriptor mFileDescriptor;
    public FileInputStream mInputStream;
    public FileOutputStream mOutputStream;

    private static final int MESSAGE_TEST = 1;
    private static final int MESSAGE_TEMPERATURE = 2;
    private static final int MESSAGE_FLOW = 3;
    private static final int MESSAGE_VOLUME = 4;

    protected class TestMsg {
        private int msgTest;

        public TestMsg(int i) {
            this.msgTest = i;
        }

        public int getTest() {
            return msgTest;
        }
    }

    protected class TemperatureMsg {
        private int temperature;

        public TemperatureMsg(int temperature) {
            this.temperature = temperature;
        }

        public int getTemperature() {
            return temperature;
        }
    }

    protected class FlowMsg {
        private int tap;
        private int flow;

        public FlowMsg(int tap, int flow) {
            this.tap = tap;
            this.flow = flow;
        }

        public int getFlow() {
            return flow;
        }

        public int getTap() {
            return tap;
        }
    }

    protected class VolumeMsg {
        private int tap;
        private int volume;

        public VolumeMsg(int tap, int volume) {
            this.tap = tap;
            this.volume = volume;
        }

        public int getVolume() {
            return volume;
        }

        public int getTap() {
            return tap;
        }
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbAccessory accessory = (UsbAccessory) intent
                            .getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        openAccessory(accessory);
                    } else {
                        Log.d(TAG, "permission denied for accessory "
                                + accessory);
                    }
                    mPermissionRequestPending = false;
                }
            } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                UsbAccessory accessory = (UsbAccessory) intent
                        .getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                if (accessory != null && accessory.equals(mAccessory)) {
                    closeAccessory();
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        kioskApp = (KegDroidKioskApplication) getApplication();
        settings = getSharedPreferences(KEGDROID_PREFS, 0);
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        registerReceiver(mUsbReceiver, filter);

        if (getLastNonConfigurationInstance() != null) { // need to use
            // Fragement API
            // setRetainInstance(boolean)
            mAccessory = (UsbAccessory) getLastNonConfigurationInstance();
            openAccessory(mAccessory);
        }

        //<TODO> enable NFC
        BeerDataSource beerDS = new BeerDataSource(this);
        beerDS.open();
        if (beerDS.checkDBEmpty()) {
            FetchBeerTask fetchBeerTask = new FetchBeerTask(getApplicationContext());
            fetchBeerTask.execute();
        }
        beerDS.close();
        Log.d(TAG, "Creating KegDroidKioskBaseActivity");
        enableControls(false);
        setContentView(R.layout.activity_kegdroid_kiosk_main);
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        if (mAccessory != null) {
            return mAccessory;
        } else {
            return super.onRetainNonConfigurationInstance(); // Use the new
            // Fragment API
            // setRetainInstance(boolean)
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        Intent intent = getIntent();
        if (mInputStream != null && mOutputStream != null) {
            return;
        }

        UsbAccessory[] accessories = mUsbManager.getAccessoryList();
        UsbAccessory accessory = (accessories == null ? null : accessories[0]);
        if (accessory != null) {
            if (mUsbManager.hasPermission(accessory)) {
                openAccessory(accessory);
            } else {
                synchronized (mUsbReceiver) {
                    if (!mPermissionRequestPending) {
                        mUsbManager.requestPermission(accessory,
                                mPermissionIntent);
                        mPermissionRequestPending = true;
                    }
                }
            }
        } else {
            Log.d(TAG, "mAccessory is null");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //closeAccessory();
        updatePrefs();
    }

    @Override
    public void onStop() {
        super.onStop();
        updatePrefs();

    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mUsbReceiver);
        updatePrefs();
        super.onDestroy();
    }

    public void updatePrefs() {
        KegDroidKioskApplication kApp = KegDroidKioskApplication.getInstance();
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("kegdroid_name", kApp.getName());
        Log.d(TAG, "kegdroid_name: " + kApp.getName());
        editor.putString("android_id", kApp.getAndroidId());
        editor.putLong("left_keg_beer_id", kApp.kdKegs[kApp.LEFT_TAP].getBeerId());
        editor.putLong("right_keg_beer_id", kApp.kdKegs[kApp.RIGHT_TAP].getBeerId());
        editor.putFloat("left_keg_volume",
                (float) kApp.kdKegs[kApp.LEFT_TAP].getCurrentVolume());
        editor.putFloat("right_keg_volume",
                (float) kApp.kdKegs[kApp.RIGHT_TAP].getCurrentVolume());
        editor.putInt("left_keg_size", (int) kApp.kdKegs[kApp.LEFT_TAP].getKegSize());
        editor.putInt("right_keg_size", (int) kApp.kdKegs[kApp.RIGHT_TAP].getKegSize());
        editor.putBoolean("nfc_mode", kApp.isNfcEnabled());
        editor.commit();
    }

    private void openAccessory(UsbAccessory accessory) {
        mFileDescriptor = mUsbManager.openAccessory(accessory);
        if (mFileDescriptor != null) {
            mAccessory = accessory;
            FileDescriptor fd = mFileDescriptor.getFileDescriptor();
            mInputStream = new FileInputStream(fd);
            mOutputStream = new FileOutputStream(fd);
            Thread thread = new Thread(null, this, "KegDroid");
            thread.start();
            Log.d(TAG, "accessory opened");
            enableControls(true);
        } else {
            Log.d(TAG, "accessory open fail");
        }
    }

    private void closeAccessory() {
        enableControls(false);
        try {
            if (mFileDescriptor != null) {
                mFileDescriptor.close();
            }
        } catch (IOException e) {
        } finally {
            mFileDescriptor = null;
            mAccessory = null;
        }
    }

    protected void enableControls(boolean enable) {
    }


    private int composeInt(byte hi, byte lo) {
        int val = (int) hi & 0xff;
        val *= 256;
        val += (int) lo & 0xff;
        return val;
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TEST:
                    TestMsg tm = (TestMsg) msg.obj;
                    //Log.d(TAG, "TestMsg: " + tm);
                    handleTestMessage(tm);
                    break;
                case MESSAGE_TEMPERATURE:
                    TemperatureMsg t = (TemperatureMsg) msg.obj;
                    handleTemperatureMessage(t);
                    break;

                /*
                 * case MESSAGE_RFID: RfidMsg r = (RfidMsg) msg.obj;
                 * handleRfidMessage(r); break;
                 */
                case MESSAGE_FLOW:
                    FlowMsg f = (FlowMsg) msg.obj;
                    handleFlowMessage(f);
                    break;

                case MESSAGE_VOLUME:
                    VolumeMsg v = (VolumeMsg) msg.obj;
                    handleVolumeMessage(v);
                    break;

            }
        }
    };

    /*
     * These are overridden in KegDroidKioskMainActivity
     */

    protected void handleTestMessage(TestMsg t) {
    }

    protected void handleTemperatureMessage(TemperatureMsg t) {
    }

    // protected void handleRfidMessage(RfidMsg r) {
    // }

    protected void handleFlowMessage(FlowMsg f) {
    }

    protected void handleVolumeMessage(VolumeMsg v) {
    }

    @Override
    public void run() {
        int ret = 0;
        byte[] buffer = new byte[32768];
        int i;

        while (ret >= 0) { // read data
            try {
                ret = mInputStream.read(buffer);
                // Log.e(TAG, "Buffer ret : " + buffer[0]);
            } catch (IOException e) {
                Log.e(TAG, "Buffer exception (reading inputStream) : " + e);
                break;
            }

            i = 0;
            while (i < ret) {
                int len = ret - i;
                switch (buffer[i]) {

                /*
                 * case 0x7: //NFC Message if (len >= 5) { Message m =
                 * Message.obtain(mHandler, MESSAGE_RFID); Log.d(TAG,
                 * "process rfid msg " + buffer[i]); byte[]rfTag = new
                 * byte[len-1]; int ctr = 0; for (int l = (i+1); l < len; l++) {
                 * rfTag[ctr++] = buffer[l]; } m.obj = new RfidMsg(rfTag);
                 * mHandler.sendMessage(m); } i += 5; break;
                 */

                    case 0x2: // Test Message
                        if (len >= 3) {
                            //Log.d(TAG, "process test msg " + buffer[i]);
                            Message m = Message.obtain(mHandler, MESSAGE_TEST);
                            m.obj = new TestMsg(composeInt(buffer[i + 1], buffer[i + 2]));
                            mHandler.sendMessage(m);
                        }
                        i += 3;
                        break;

                    case 0x4: // Temperature Message
                        if (len >= 3) {
                            // Log.d(TAG, "process temp msg " + buffer[i]);
                            Message m = Message.obtain(mHandler, MESSAGE_TEMPERATURE);
                            m.obj = new TemperatureMsg(composeInt(buffer[i + 1], buffer[i + 2]));
                            mHandler.sendMessage(m);
                        }
                        i += 3;
                        break;

                    case 0x5: // Flow Message
                        if (len >= 4) {
                            Log.d(TAG, "process flow msg " + buffer[i]);
                            Message m = Message.obtain(mHandler, MESSAGE_FLOW);
                            m.obj = new FlowMsg(buffer[i + 1], composeInt(buffer[i + 2],
                                    buffer[i + 3]));
                            mHandler.sendMessage(m);
                        }
                        i += 4;
                        break;

                    case 0x6: // Final Volume Message
                        if (len >= 4) {
                            Log.d(TAG, "process final flow msg " + buffer[i]);
                            Message m = Message.obtain(mHandler, MESSAGE_VOLUME);
                            m.obj = new VolumeMsg(buffer[i + 1], composeInt(buffer[i + 2],
                                    buffer[i + 3]));
                            mHandler.sendMessage(m);
                        }
                        i += 4;
                        break;

                    default:
                        Log.d(TAG, "unknown msg: " + buffer[i]);
                        i = len;
                        break;
                }
            }

        }
    }

}
