package com.anzym.android.kegdroidlibrary.database;

/**
 * Created by pcarff on 1/15/16.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BeerDBHelper extends SQLiteOpenHelper {

    private static String TAG = "BeerDBHelper";

    public static final String TABLE_BEERS = "beers";
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_BREWERY_ID = "brewery_id";
    public static final String COL_STYLE = "style";
    public static final String COL_ABV = "abv";
    public static final String COL_IBU = "ibu";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_RATING = "rating";
    public static final String COL_IMAGE = "image";
    public static final String COL_BREWERY_NAME = "brewery_name";

    private static final String DATABASE_NAME = "beers.db";
    private static final int DATABASE_VERSION = 2;

    // <TODO>Database creation sql statement
    // **** SHOULD COL_ID BE AUTOINCREMENT?????
    private static final String DATABASE_CREATE = "create table "
            + TABLE_BEERS + "("
            + COL_ID + " integer primary key, "
            + COL_NAME + " text not null, "
            + COL_BREWERY_ID + " integer, "
            + COL_STYLE + " text, "
            + COL_ABV + " real, "
            + COL_IBU + " integer, "
            + COL_DESCRIPTION + " text, "
            + COL_RATING + " real, "
            + COL_IMAGE + " blob, "
            + COL_BREWERY_NAME + " text); ";

    public BeerDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(BeerDBHelper.class.getName(), "Creating Database");
        Log.d(BeerDBHelper.class.getName(), DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(BeerDBHelper.class.getName(), "Upgrading database from version " + oldVersion
                + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BEERS);
        onCreate(db);

    }

}
