package com.anzym.android.kegdroidkiosk.configurators;

/**
 * Created by pcarff on 1/15/16.
 */

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anzym.android.kegdroidkiosk.KegDroidKioskApplication;
import com.anzym.android.kegdroidkiosk.KegDroidKioskMainActivity;
//import com.anzym.android.kegdroidkiosk.R;
import com.anzym.android.kegdroidkiosk.R;
import com.anzym.android.kegdroidkiosk.models.Keg;
import com.anzym.android.kegdroidlibrary.database.BeerDataSource;
import com.anzym.android.kegdroidlibrary.models.Beer;

import java.util.List;

public class TapSetupDialog extends Dialog {

    private static final String TAG = TapSetupDialog.class.getSimpleName();

    private BeerDataSource beerDbSource;

    private GridView beerGridView;
    private BeerGridAdapter beerGridAdapter;

    LinearLayout beerGridItem;
    TextView tapConfig;
    private List<Beer> beerList;
    private KegDroidKioskMainActivity kdA;
    private int tapNumber;
    KegSetupDialog kegSetup;

    public TapSetupDialog(Context context, int tn) {
        super(context);
        this.kdA = (KegDroidKioskMainActivity) context;
        this.tapNumber = tn;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_tap_setup);
        setTitle(null);

        prepareList();

        beerGridAdapter = new BeerGridAdapter(beerList);
        // beerGridItem = (LinearLayout) findViewById(R.id.grid_layout_item);
        setTapText(tapNumber);

        beerGridView = (GridView) findViewById(R.id.beer_grid);
        beerGridView.setAdapter(beerGridAdapter);

        beerGridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Long beerId = beerList.get(position).getId();
                kdA.kioskApp.kdKegs[tapNumber].setBeerId(beerId);
                kegSetup = new KegSetupDialog(kdA, tapNumber, beerId);
                kegSetup.setTitle(beerList.get(position).getName());
                kegSetup.show();
                TapSetupDialog.this.dismiss();
            }

        });
    }

    public void prepareList() {
        beerDbSource = new BeerDataSource(kdA);
        beerDbSource.open();
        beerList = beerDbSource.getAllBeers();
        beerDbSource.close();
    }

    public class BeerGridAdapter extends BaseAdapter {

        private final List<Beer> beerList;
        private KegDroidKioskMainActivity kdA;

        BeerGridAdapter(List<Beer> bl) {
            super();
            this.beerList = bl;
        }

        @Override
        public int getCount() {
            return beerList.size();
        }

        @Override
        public Beer getItem(int position) {
            return beerList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {
            private ImageView imgViewBeer;
            private TextView txtViewTitle;
            private  Beer mBeer;
            private View mContainer;

            public void setBeer(Beer b) {
                this.mBeer = b;
                this.txtViewTitle.setText(mBeer.getName());
                this.imgViewBeer.setImageBitmap(mBeer.getImage());
                this.mContainer.setBackgroundColor((isSelected()
                        ? 0x6633b5e5 : Color.TRANSPARENT));
            }

            public boolean isSelected() {
                Keg[] kioskKegs = KegDroidKioskApplication.getInstance().kdKegs;
                long id = this.mBeer.getId();
                return (id == kioskKegs[tapNumber].getBeerId());
            }

            public ImageView getImgViewBeer() {
                return imgViewBeer;
            }

            public void setImgViewBeer(ImageView imgViewBeer) {
                this.imgViewBeer = imgViewBeer;
            }

            public TextView getTxtViewTitle() {
                return txtViewTitle;
            }

            public void setTxtViewTitle(TextView txtViewTitle) {
                this.txtViewTitle = txtViewTitle;
            }

            public Beer getmBeer() {
                return mBeer;
            }

            public void setmBeer(Beer mBeer) {
                this.mBeer = mBeer;
            }

            public void setContainer(View container) {
                mContainer = container;
            }

            public View getContainer() {
                return mContainer;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) KegDroidKioskApplication.getInstance()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewHolder view = null;

            if (convertView == null) {
                view = new ViewHolder();
                convertView = inflater.inflate(R.layout.beer_grid, null);
                view.setContainer(convertView.findViewById(R.id.grid_layout_item));
                view.setTxtViewTitle((TextView) convertView.findViewById(R.id.grid_item_beer_text));
                view.setImgViewBeer(
                        (ImageView) convertView.findViewById(R.id.grid_item_beer_image));
                convertView.setTag(view);

            } else {
                view = (ViewHolder) convertView.getTag();
            }
            view.setBeer(beerList.get(position));
            Log.d(TAG, "Position: " + position);
            Log.d(TAG, "selected: " + view.isSelected());
            return convertView;
        }
    }

    private void setTapText(int tap) {
        switch (tap) {
            case (0):
                setTitle("Configure LEFT tap.");
                break;
            case (1):
                setTitle("Configure RIGHT tap.");
                break;
            default:
                break;
        }
    }

}
