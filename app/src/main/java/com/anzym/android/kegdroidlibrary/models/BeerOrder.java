package com.anzym.android.kegdroidlibrary.models;

/**
 * Created by pcarff on 1/15/16.
 */
public class BeerOrder {

    private static String TAG = BeerOrder.class.getSimpleName();

    private Long beerId;
    private int orderSize;
    private int tapNumber;
    private String beerName;
    private String androidId;

    public static int LARGE_GLASS = 12;
    public static int MEDIUM_GLASS = 8;
    public static int SMALL_GLASS = 2;

    public int getTapNumber() {
        return tapNumber;
    }

    public void setTapNumber(int tapNumber) {
        this.tapNumber = tapNumber;
    }

    public BeerOrder() {
        this.tapNumber = 0;
        this.beerId = (long) 5006; //
        this.orderSize = MEDIUM_GLASS;
    }

    public BeerOrder(int tapNumber, int size) {
        this.tapNumber = tapNumber;
        this.orderSize = size;
    }

    public int getOrderSize() {
        return orderSize;
    }

    public void setOrderSize(int orderSize) {
        this.orderSize = orderSize;
    }

    public Long getBeerId() {
        return beerId;
    }

    public void setBeerId(Long beerId) {
        this.beerId = beerId;
    }

    public String getBeerName() {
        return beerName;
    }

    public void setBeerName(String beerName) {
        this.beerName = beerName;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }



}
