package com.dpanic.glocoexercise;

import android.support.annotation.NonNull;

import com.google.android.gms.location.LocationListener;

import java.lang.ref.WeakReference;

/**
 * Created by dpanic on 17/02/2017.
 * Project: GlocoExercise
 */

class WeakRefLocationListener implements LocationListener {
    private final WeakReference<LocationListener> locationListenerRef;

    WeakRefLocationListener(@NonNull LocationListener locationListener) {
        locationListenerRef = new WeakReference<>(locationListener);
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        if (locationListenerRef.get() == null) {
            return;
        }
        locationListenerRef.get().onLocationChanged(location);
    }
}
