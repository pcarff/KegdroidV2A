package com.anzym.android.kegdroidkiosk.configurators;

/**
 * Created by pcarff on 1/15/16.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.anzym.android.kegdroidkiosk.KegDroidKioskApplication;
import com.anzym.android.kegdroidkiosk.KegDroidKioskMainActivity;

import com.anzym.android.kegdroidkiosk.R;
import com.anzym.android.kegdroidkiosk.models.Keg;
import com.anzym.android.kegdroidkiosk.task.UpdateKegDroidCloud;

public class NFCEnableDialog extends Dialog {

    private static final String TAG = NFCEnableDialog.class.getSimpleName();
    KegDroidKioskMainActivity kdA;

    ToggleButton enableNfcButton;

    public NFCEnableDialog(Context context) {
        super(context);
        this.kdA = (KegDroidKioskMainActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enable_nfc);

        Log.d(TAG, "prepping nfc dialog");
        enableNfcButton = (ToggleButton) findViewById(R.id.enable_nfc_button);
        enableNfcButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (enableNfcButton.isChecked()) {
                    kdA.kioskApp.setNfcEnabled(true);
                    Log.d(TAG, "NFC On");
                    //<TODO> Call method in kdA to turn on nfc (PREP?)
                } else {
                    kdA.kioskApp.setNfcEnabled(false);
                    Log.d(TAG, "NFC Off");
                    //<TODO> Call method in kdA to to OFF nfc
                }
                NFCEnableDialog.this.dismiss();
            }
        });
        setCurrentState();
    }

    private void setCurrentState() {
        enableNfcButton.setChecked(kdA.kioskApp.isNfcEnabled());
    }

}
