package com.dpanic.glocoexercise.model;

import android.location.Location;

import io.realm.RealmObject;

/**
 * Created by dpanic on 17/02/2017.
 * Project: GlocoExercise
 */

public class GeoLocation extends RealmObject {
    private double latitude;
    private double longitude;

    public GeoLocation() {
    }

    public GeoLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
