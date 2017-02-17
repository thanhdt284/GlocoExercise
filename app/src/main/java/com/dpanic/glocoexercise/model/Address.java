package com.dpanic.glocoexercise.model;

import io.realm.RealmObject;

/**
 * Created by dpanic on 17/02/2017.
 * Project: GlocoExercise
 */

public class Address extends RealmObject {
    private String street;
    private String suite;
    private String city;
    private String zipCode;
    private GeoLocation geoLocation;

    public Address() {
    }

    public Address(String street, String suite, String city, String zipCode, GeoLocation geoLocation) {
        this.street = street;
        this.suite = suite;
        this.city = city;
        this.zipCode = zipCode;
        this.geoLocation = geoLocation;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getSuite() {
        return suite;
    }

    public void setSuite(String suite) {
        this.suite = suite;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    public String getFullAddress() {
        return street + " " + suite + " " + city;
    }
}
