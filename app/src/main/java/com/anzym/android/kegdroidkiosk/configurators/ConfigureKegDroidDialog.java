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
import android.widget.EditText;

import com.anzym.android.kegdroidkiosk.KegDroidKioskMainActivity;
import com.anzym.android.kegdroidkiosk.R;

public class ConfigureKegDroidDialog extends Dialog {

    private static String TAG = ConfigureKegDroidDialog.class.getSimpleName();
    KegDroidKioskMainActivity kdA;
    private EditText nameView;
    private Button setNameButton;
    public ConfigureKegDroidDialog(Context ctx) {
        super(ctx);
        this.kdA = (KegDroidKioskMainActivity) ctx;
    }

    //<TODO>  Set up configure secure/non-secure mode (ie use NFC)

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_configure_kegdroid);
        nameView = (EditText) findViewById(R.id.set_name_text);
        Log.d(TAG, "Name: " + kdA.kioskApp.getName());
        nameView.setText(kdA.kioskApp.getName());
        setNameButton = (Button) findViewById(R.id.set_name_button);
        setNameButton.setOnClickListener(new SetNameButtonClickListener());
    }

    private class SetNameButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Setting KegDroid Name: " + nameView.getText().toString());
            kdA.kioskApp.setName(nameView.getText().toString());
            kdA.updateKegDroidNameView();
            ConfigureKegDroidDialog.this.dismiss();
        }
    }

}
