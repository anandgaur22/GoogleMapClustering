package com.iws.googlemapclustering;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Subarata Talukder on 3/21/2017.
 */

public class GeoService extends Service implements LocationListener {
    // Initiate interface variable by implementer class instance
    private final IBinder geoServiceBinder = new GeoServiceBinder();
    private static LocationManager locationManager;
    // Geo location min update time and distance
    private final int MIN_TIME_TO_UPDATE_LOCATION = 2 * 60 * 5000;
    private final int MIN_DISTANCE_TO_UPDATE_LOCATION = 2;
    private Location location;

    public GeoService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // When service is start, get the location manager based on device service
        if (locationManager == null)
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        // Request for location
        if (locationManager != null)
            makeGeoRequest();
    }

    // Its a first time request for location
    private void makeGeoRequest() {
        // Define criteria for you network preferences
        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        // Get a provider for those criteria
        String provider = locationManager.getBestProvider(criteria, true);

        // Android new API > 21 it is highly recommendable to use runtime permission
        if (null != provider)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

        // Request for location update for this provider
        locationManager.requestLocationUpdates(provider, MIN_TIME_TO_UPDATE_LOCATION, MIN_DISTANCE_TO_UPDATE_LOCATION, this);
        // Get last location
        location = locationManager.getLastKnownLocation(provider);
        // Calling the overriding method to change the location
        if (location != null) {
            onLocationChanged(location);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return binder instance
        return geoServiceBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    // Those methods are responsible for location data change
    @Override
    public void onLocationChanged(Location location) {
        // Call the static method when location update
        MainActivity.updateLocation(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    // Create a binder class, to get it's instance to other class
    // It's a access point to other class
    public class GeoServiceBinder extends Binder {
        public GeoService getGeoService() {
            return GeoService.this;
        }
    }
}