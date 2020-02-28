package com.anzym.android.kegdroidlibrary.database;

/**
 * Created by pcarff on 1/15/16.
 */
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.anzym.android.kegdroidlibrary.models.Beer;

public class BeerDataSource {

    private static String TAG = "BeerDataSource";

    private SQLiteDatabase beerDb;
    private BeerDBHelper beerDbHelper;

    private String[] allColumns = {
            BeerDBHelper.COL_ID,
            BeerDBHelper.COL_NAME,
            BeerDBHelper.COL_BREWERY_ID,
            BeerDBHelper.COL_STYLE,
            BeerDBHelper.COL_ABV,
            BeerDBHelper.COL_IBU,
            BeerDBHelper.COL_DESCRIPTION,
            BeerDBHelper.COL_RATING,
            BeerDBHelper.COL_IMAGE,
            BeerDBHelper.COL_BREWERY_NAME
    };

    public BeerDataSource(Context context) {
        beerDbHelper = new BeerDBHelper(context);
    }

    public void open() throws SQLException {
        beerDb = beerDbHelper.getWritableDatabase();
    }

    public void close() {
        beerDb.close();
    }

    // Do I need to return the beer object here?
    public Beer createBeer(long id, String name, long brewery_id, String style, double abv,
                           int ibu, String description, double rating, String image, String brewery_name) {
        ContentValues values = new ContentValues();
        values.put(BeerDBHelper.COL_ID, id);
        values.put(BeerDBHelper.COL_NAME, name);
        values.put(BeerDBHelper.COL_BREWERY_ID, brewery_id);
        values.put(BeerDBHelper.COL_STYLE, style);
        values.put(BeerDBHelper.COL_ABV, abv);
        values.put(BeerDBHelper.COL_IBU, ibu);
        values.put(BeerDBHelper.COL_DESCRIPTION, description);
        values.put(BeerDBHelper.COL_RATING, rating);
        values.put(BeerDBHelper.COL_IMAGE, image);
        values.put(BeerDBHelper.COL_BREWERY_NAME, brewery_name);

        long insertId = beerDb.insert(BeerDBHelper.TABLE_BEERS, null, values);
        Cursor cursor = beerDb.query(BeerDBHelper.TABLE_BEERS, allColumns,
                BeerDBHelper.COL_ID + "=" + insertId, null, null, null, null, null);
        cursor.moveToFirst();
        Beer newBeer = cursorToBeer(cursor);
        cursor.close();
        return newBeer;
    }

    // Add Beer
    public void addBeer(long id, String name, long brewery_id, String style, double abv,
                        int ibu, String description, double rating, String image, String brewery_name) {
        ContentValues values = new ContentValues();
        values.put(BeerDBHelper.COL_ID, id);
        values.put(BeerDBHelper.COL_NAME, name);
        values.put(BeerDBHelper.COL_BREWERY_ID, brewery_id);
        values.put(BeerDBHelper.COL_STYLE, style);
        values.put(BeerDBHelper.COL_ABV, abv);
        values.put(BeerDBHelper.COL_IBU, ibu);
        values.put(BeerDBHelper.COL_DESCRIPTION, description);
        values.put(BeerDBHelper.COL_RATING, rating);
        values.put(BeerDBHelper.COL_IMAGE, image);
        values.put(BeerDBHelper.COL_BREWERY_NAME, brewery_name);
        this.addBeer(values);
    }

    // Add Beer
    public void addBeer(ContentValues values) {
        open();
        beerDb.insert(BeerDBHelper.TABLE_BEERS, null, values);
        close();
    }

    // Update a Beer
    public void updateBeer(long id, ContentValues values) {
        Log.d(TAG, "updateBeer id: " + id);
        open();
        beerDb.update(BeerDBHelper.TABLE_BEERS, values, BeerDBHelper.COL_ID + "=" + id, null);
        close();
    }

    // Get Beer
    public Beer getBeer(long id) {
        Beer beer = null;
        open();
        Cursor cursor = beerDb.query(BeerDBHelper.TABLE_BEERS, allColumns,
                BeerDBHelper.COL_ID + "=" + id, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            beer = cursorToBeer(cursor);
        }
        cursor.close();
        close();
        return beer;
    }

    public Beer getBeer(String name) {
        Beer beer = null;
        open();
        Cursor cursor = beerDb.query(BeerDBHelper.TABLE_BEERS, allColumns,
                BeerDBHelper.COL_NAME + "=" + name, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            beer = cursorToBeer(cursor);
        }
        cursor.close();
        close();
        return beer;
    }

    public boolean checkDBEmpty() {
        boolean isEmpty = true;
        Beer beer = null;
        open();
        try {
            Cursor cursor = beerDb.query(BeerDBHelper.TABLE_BEERS, allColumns,
                    null, null, null, null, null);
            if (cursor.moveToFirst()) {
                beer = cursorToBeer(cursor);
                if (beer != null) {
                    Log.d(TAG, "DB is POPULATED");
                    isEmpty = false;
                } else {
                    Log.d(TAG, "DB is EMPTY");
                }
            }
        } catch (Exception e) {

        }
        close();
        return isEmpty;
    }

    public void deleteBeer(long id) {
        Log.w(TAG, "Beer deleted with id: " + id);
        open();
        beerDb.delete(BeerDBHelper.TABLE_BEERS, BeerDBHelper.COL_ID + "=" + id, null);
        close();
    }

    public void deleteBeer(Beer beer) {
        long id = beer.getId();
        Log.w(TAG, "Beer deleted with id: " + id);
        open();
        beerDb.delete(BeerDBHelper.TABLE_BEERS, BeerDBHelper.COL_ID + "=" + id, null);
        close();
    }

    public List<Beer> getAllBeers() {
        List<Beer> beers = new ArrayList<Beer>();
        open();
        Cursor cursor = beerDb.query(BeerDBHelper.TABLE_BEERS, allColumns,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Beer beer = cursorToBeer(cursor);
            beers.add(beer);
            cursor.moveToNext();
        }
        cursor.close();
        close();
        return beers;
    }

    private Beer cursorToBeer(Cursor cursor) {
        Beer beer = new Beer(cursor.getLong(0));
        beer.setName(cursor.getString(1));
        beer.setBrewery_id(cursor.getInt(2));
        beer.setStyle(cursor.getString(3));
        beer.setAbv(cursor.getDouble(4));
        beer.setIbu(cursor.getInt(5));
        beer.setDescription(cursor.getString(6));
        beer.setRating(cursor.getDouble(7));
        beer.setImage(cursor.getBlob(8));
        beer.setBrewery_name(cursor.getString(9));
        return beer;
    }

}
