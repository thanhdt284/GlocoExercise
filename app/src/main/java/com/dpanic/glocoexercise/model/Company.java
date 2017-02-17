package com.dpanic.glocoexercise.model;

import io.realm.RealmObject;

/**
 * Created by dpanic on 17/02/2017.
 * Project: GlocoExercise
 */

public class Company extends RealmObject {
    private String name;
    private String catchPhrase;
    private String bs;

    public Company() {
    }

    public Company(String name, String catchPhrase, String bs) {
        this.name = name;
        this.catchPhrase = catchPhrase;
        this.bs = bs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatchPhrase() {
        return catchPhrase;
    }

    public void setCatchPhrase(String catchPhrase) {
        this.catchPhrase = catchPhrase;
    }

    public String getBs() {
        return bs;
    }

    public void setBs(String bs) {
        this.bs = bs;
    }
}
