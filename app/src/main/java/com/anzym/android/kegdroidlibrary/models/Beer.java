package com.anzym.android.kegdroidlibrary.models;

/**
 * Created by pcarff on 1/15/16.
 */
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;


public class Beer {

    private static final String TAG = Beer.class.getSimpleName();

    private long id;
    private String name;
    private long brewery_id;
    private String brewery_name;
    private String style;  //Should change this to id and pull from style db.
    private double abv;  //This will be converted to REAL in database.
    private int ibu;
    private String description;
    private double rating;
    private byte[] image; //This is the resource name...

    public Beer() {
    }

    public Beer(long id){
        this.id = id;
    }

    public Beer(long id, String name){
        this(id );
        this.name = name;
    }

    public Beer(long id, String name, long brewery_id){
        this(id, name);
        this.brewery_id = brewery_id;
    }

    public Beer(long id,  String name, long brewery_id, String style){
        this(id, name, brewery_id);
        this.style = style;
    }

    public Beer(long id,  String name, long brewery_id, String style, double abv){
        this(id, name, brewery_id, style);
        this.abv = abv;
    }

    public Beer(long id,  String name, long brewery_id, String style, double abv,
                int ibu){
        this(id, name, brewery_id, style, abv);
        this.ibu = ibu;
    }

    public Beer(long id,  String name, long brewery_id, String style, double abv,
                int ibu, String description){
        this(id, name, brewery_id, style, abv, ibu);
        this.description = description;
    }

    public Beer(long id,  String name, long brewery_id, String style, double abv,
                int ibu, String description, byte[] image){
        this(id, name, brewery_id, style, abv, ibu, description);
        this.image = image;
    }

    public Beer(long id,  String name, long brewery_id, String style, double abv,
                int ibu, String description, byte[] image, double rating){
        this(id, name, brewery_id, style, abv, ibu, description, image);
        this.rating = rating;
    }

    public Beer(long id,  String name, long brewery_id, String style, double abv,
                int ibu, String description, byte[] image, double rating, String brewery_name){
        this(id, name, brewery_id, style, abv, ibu, description, image, rating);
        this.brewery_name = brewery_name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getBrewery_id() {
        return brewery_id;
    }

    public void setBrewery_id(long brewery_id) {
        this.brewery_id = brewery_id;
    }

    public String getBrewery_name() {
        return brewery_name;
    }

    public void setBrewery_name(String brewery_name) {
        this.brewery_name = brewery_name;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public double getAbv() {
        return abv;
    }

    public void setAbv(double abv) {
        this.abv = abv;
    }

    public int getIbu() {
        return ibu;
    }

    public void setIbu(int ibu) {
        this.ibu = ibu;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Bitmap getImage() {
        byte[] imageByteArray = image;
        ByteArrayInputStream imageStream = new ByteArrayInputStream(imageByteArray);
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        return bitmap;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }


}
