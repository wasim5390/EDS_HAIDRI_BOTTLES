package com.optimus.eds.location_services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class LocationService extends Service  {
    public static final String ACTION = "LOCATION_ACTION";
    public static final String LOCATION = "LOCATION_DATA";
    private static final String TAG = LocationService.class.getName();
    private static final String DISTANCE = "DISTANCE";
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS =  10*1000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS/2;
    private static final float ACCURACY_THRESHOLD=100;


    protected LocationRequest locationRequest;
    protected Location oldLocation;
    protected Location newLocation;
    protected Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private String timeCurrentLocation;

    private float distance;


    @Override
    public void onCreate() {
        super.onCreate();
        oldLocation = new Location("Point A");
        newLocation = new Location("Point B");
        timeCurrentLocation  = "";
        buildClient();

        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location lastLocation = locationResult.getLastLocation();
               // if(lastLocation==null || lastLocation.getAccuracy()>ACCURACY_THRESHOLD)
                 if(lastLocation==null)
                return;
                currentLocation = lastLocation;
                timeCurrentLocation = DateFormat.getTimeInstance().format(new Date());
                updateUI();
            }
        };
        // distance = PreferenceManager.getDefaultSharedPreferences(this).getFloat(DISTANCE, 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startLocationUpdates();
        return START_STICKY;

    }

    protected void startLocationUpdates() {
        try {

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(task -> {

                if(currentLocation!=null) {
                    currentLocation=   task;
                    sendLocationBroadcast();
                }
            });

            fusedLocationProviderClient.requestLocationUpdates( locationRequest, mLocationCallback, Looper.myLooper());


        } catch (SecurityException ex) {

        }
    }

    protected synchronized void buildClient() {

        createLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }


    protected void createLocationRequest() {
        locationRequest = new LocationRequest();

        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationRequest.setSmallestDisplacement(2f); // 2 meters
    }

    private void updateUI() {

        if (null != currentLocation) {

            StringBuilder locationData = new StringBuilder();
            locationData
                    .append("Latitude: " + currentLocation.getLatitude())
                    .append("\n")
                    .append("Longitude: " + currentLocation.getLongitude())
                    .append("\n")
                    .append("Time: " + timeCurrentLocation)
                    .append("\n")
                    .append(getUpdatedDistance())
                    .append(" meters");


            PreferenceManager.getDefaultSharedPreferences(this).edit().putFloat(DISTANCE, distance).apply();

            Log.d(TAG, "Location Data:\n" + locationData.toString());

            sendLocationBroadcast();

        } else {

            Toast.makeText(this, "Unable to find location", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendLocationBroadcast() {

        Intent locationIntent = new Intent();
        locationIntent.setAction(ACTION);
        locationIntent.putExtra(LOCATION, currentLocation);

        LocalBroadcastManager.getInstance(this).sendBroadcast(locationIntent);

    }


    protected void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }


    @Override
    public void onDestroy() {

        PreferenceManager.getDefaultSharedPreferences(this).edit().putFloat(DISTANCE, distance).apply();

        stopLocationUpdates();


        Log.d(TAG, "onDestroy Distance " + distance);


        super.onDestroy();
    }




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private float getUpdatedDistance() {


        if (currentLocation.getAccuracy() > ACCURACY_THRESHOLD) {

            return distance;
        }


        if (oldLocation.getLatitude() == 0 && oldLocation.getLongitude() == 0) {

            oldLocation.setLatitude(currentLocation.getLatitude());
            oldLocation.setLongitude(currentLocation.getLongitude());

            newLocation.setLatitude(currentLocation.getLatitude());
            newLocation.setLongitude(currentLocation.getLongitude());

            return distance;
        } else {

            oldLocation.setLatitude(newLocation.getLatitude());
            oldLocation.setLongitude(newLocation.getLongitude());

            newLocation.setLatitude(currentLocation.getLatitude());
            newLocation.setLongitude(currentLocation.getLongitude());

        }

        distance += newLocation.distanceTo(oldLocation);

        return distance;
    }


}