package com.anzym.android.kegdroidkiosk.configurators;

/**
 * Created by pcarff on 1/15/16.
 */

import android.app.Dialog;
import android.content.Context;
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

import com.anzym.android.kegdroidkiosk.KegDroidKioskMainActivity;
import com.anzym.android.kegdroidkiosk.R;
import com.anzym.android.kegdroidkiosk.models.Keg;
import com.anzym.android.kegdroidkiosk.task.UpdateKegDroidCloud;

public class KegSetupDialog extends Dialog {

    private static final String TAG = KegSetupDialog.class.getSimpleName();
    KegDroidKioskMainActivity kdA;
    Keg keg;
    int tapNumber;
    long beer_id;

    private RadioGroup mRadioGroup;
    EditText kegVolumeView;
    Button updateKegButton;

    public KegSetupDialog(Context context, int tn, long beerId) {
        super(context);
        this.kdA = (KegDroidKioskMainActivity) context;
        this.tapNumber = tn;
        this.beer_id = beerId;
        this.keg = kdA.kioskApp.kdKegs[tapNumber];
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_keg_setup);
        // Keg Size
        mRadioGroup = (RadioGroup) findViewById(R.id.kegradiogroup);
        mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.keg_size_5:
                        keg.setKegSize(keg.KEG_SIZE_5);
                        break;
                    case R.id.keg_size_15:
                        keg.setKegSize(keg.KEG_SIZE_15);
                        break;
                }
            }

        });
        // Volume currently in Keg
        kegVolumeView = (EditText) findViewById(R.id.keg_volume);
        kegVolumeView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
                | InputType.TYPE_NUMBER_FLAG_SIGNED);

        updateKegButton = (Button) findViewById(R.id.submit_keg_button);
        updateKegButton.setOnClickListener(new AddNewKegOnClickListener());
    }

    public class AddNewKegOnClickListener implements android.view.View.OnClickListener {

        @Override
        public void onClick(View v) {
            double vol = Float.valueOf(kegVolumeView.getText().toString());
            if (vol > keg.getKegSize()) {
                kegVolumeMismatch();
            } else {
                kdA.kioskApp.kdKegs[tapNumber].setCurrentVolume(vol);
                kdA.getBeerTapController(tapNumber).refreshKeg();
                Log.d(TAG, "Updating keg " + tapNumber
                        + " to " + keg.getKegSize()
                        + " filled with " + keg.getCurrentVolume()
                        + " gallons.");
                UpdateKegDroidCloud rKd = new UpdateKegDroidCloud(kdA.kioskApp);
                rKd.update();
                KegSetupDialog.this.dismiss();
            }
        }
    }

    public void kegVolumeMismatch() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.keg_volume_mismatch,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        ImageView image = (ImageView) layout.findViewById(R.id.volume_mismatch_image);
        image.setImageResource(R.drawable.keg_volume_mismatch);
        TextView text = (TextView) layout.findViewById(R.id.volume_mismatch_text);
        text.setText("Volume entered is larger \nthan keg size!");

        Toast toast = new Toast(getContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();

    }

}
