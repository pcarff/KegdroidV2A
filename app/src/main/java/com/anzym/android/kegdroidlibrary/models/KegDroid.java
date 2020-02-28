package com.anzym.android.kegdroidlibrary.models;

/**
 * Created by pcarff on 1/15/16.
 */
public class KegDroid {

    private static final String TAG = KegDroid.class.getSimpleName();

    private String name;
    private String android_id;
    private double lat;
    private double lon;
    private int[] beer_id = new int[2];
    private double total_volume_poured;
    private int number_unique_beers;
    private int number_unique_drinkers;


    public KegDroid(String n) {
        this.name = n;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getAndroid_id() {
        return android_id;
    }


    public void setAndroid_id(String android_id) {
        this.android_id = android_id;
    }


    public double getLat() {
        return lat;
    }


    public void setLat(double lat) {
        this.lat = lat;
    }


    public double getLon() {
        return lon;
    }


    public void setLon(double lon) {
        this.lon = lon;
    }


    public int getBeer_id(int b) {
        return beer_id[b];
    }


    public void setBeer_id(int[] beer_id) {
        this.beer_id = beer_id;
    }


    public double getTotal_volume_poured() {
        return total_volume_poured;
    }


    public void setTotal_volume_poured(double total_volume_poured) {
        this.total_volume_poured = total_volume_poured;
    }


    public int getNumber_unique_beers() {
        return number_unique_beers;
    }


    public void setNumber_unique_beers(int number_unique_beers) {
        this.number_unique_beers = number_unique_beers;
    }


    public int getNumber_unique_drinkers() {
        return number_unique_drinkers;
    }


    public void setNumber_unique_drinkers(int number_unique_drinkers) {
        this.number_unique_drinkers = number_unique_drinkers;
    }




}
