package com.example.service_runner;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class LocationClass implements LocationListener, LocationTracker {

    // The minimum distance to change Updates in meters
    private static final long MIN_UPDATE_DISTANCE = 10;

    // The minimum time between updates in milliseconds
    private static final long MIN_UPDATE_TIME = 1000;

    private LocationManager lm;

    public enum ProviderType {
        NETWORK,
        GPS
    }

    private final Context mContext;
    private String provider;

    private Location lastLocation;
    private long lastTime;

    private boolean isRunning;

    private LocationUpdateListener listener;

    public LocationClass(Context context, ProviderType type) {
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (type == ProviderType.NETWORK) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            provider = LocationManager.GPS_PROVIDER;
        }
        this.mContext = context;
    }

    public void start() {
        if (isRunning) {
            //Already running, do nothing
            return;
        }

        //The provider is on, so start getting updates.  Update current location
        isRunning = true;
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext.getApplicationContext(), "GPS não OK!", Toast.LENGTH_SHORT).show();
        }
        lm.requestLocationUpdates(provider, MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE, this);
        lastLocation = null;
        lastTime = 0;
        return;
    }

    public void start(LocationUpdateListener update) {
        start();
        listener = update;

    }


    public void stop() {
        if (isRunning) {
            lm.removeUpdates(this);
            isRunning = false;
            listener = null;
        }
    }

    public boolean hasLocation() {
        if (lastLocation == null) {
            return false;
        }
        if (System.currentTimeMillis() - lastTime > 5 * MIN_UPDATE_TIME) {
            return false; //stale
        }
        return true;
    }

    public boolean hasPossiblyStaleLocation() {
        if (lastLocation != null) {
            return true;
        }
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext.getApplicationContext(), "GPS não OK!", Toast.LENGTH_SHORT).show();
        }
        return lm.getLastKnownLocation(provider) != null;
    }

    public Location getLocation(){
        if(lastLocation == null){
            return null;
        }
        if(System.currentTimeMillis() - lastTime > 5 * MIN_UPDATE_TIME){
            return null; //stale
        }
        return lastLocation;
    }

    public Location getPossiblyStaleLocation(){
        if(lastLocation != null){
            return lastLocation;
        }
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext.getApplicationContext(), "GPS não OK!", Toast.LENGTH_SHORT).show();
        }
        return lm.getLastKnownLocation(provider);
    }

    public void onLocationChanged(Location newLoc) {
        long now = System.currentTimeMillis();
        if(listener != null){
            listener.onUpdate(lastLocation, lastTime, newLoc, now);
        }
        lastLocation = newLoc;
        lastTime = now;
    }

    public void onProviderDisabled(String arg0) {

    }

    public void onProviderEnabled(String arg0) {

    }

    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
    }
}