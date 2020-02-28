package com.anzym.android.kegdroidlibrary.models;

/**
 * Created by pcarff on 1/15/16.
 */

public class KegDroidUser {

    private String emailAddress = null;
    private String gPlusId = null;
    private String displayName = null;
    private String givenName = null;
    private String imageUrl = null;

    public KegDroidUser() {

    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getGPlusId() {
        return gPlusId;
    }
    public void setGPlusId(String gPlusId) {
        this.gPlusId = gPlusId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }



}
