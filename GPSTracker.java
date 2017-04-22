package com.uasdtu.tript.uas;


import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Tript on 02-02-2017.
 */

public class GPSTracker extends Service implements LocationListener{

    private final Context context;
    boolean isGPSEnabled = false;
    boolean canGetLocation = false;
    boolean isNetworkEnabled = true;

    Location location;

    double Lat, Long, Alt;

    private static final long MIN_DIST = 2;
    private static final long MIN_TIME = 1000;

    protected LocationManager locationManager;

    public GPSTracker(Context context){
        this.context = context;
        getLocation();
    }

    public Location getLocation(){
        try{
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled){

            }else{
                this.canGetLocation = true;

                if (isGPSEnabled){

                        try {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME, MIN_DIST,
                                    this);
                            if (locationManager!=null){
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                if (location != null){
                                    Lat = location.getLatitude();
                                    Long = location.getLongitude();
                                    Alt = location.getAltitude();
                                }
                            }
                        }catch (SecurityException e){e.printStackTrace();}

                        Log.d("GPS","Inside GPS");

                }
                if(isNetworkEnabled) {
                    if (location == null) {
                        try {
                            locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    MIN_TIME,
                                    MIN_DIST, this);

                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                        if (locationManager != null) {
                            try {
                                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            } catch (SecurityException e) {
                                e.printStackTrace();
                            }

                            if (location != null) {
                                Lat = location.getLatitude();
                                Long = location.getLongitude();
                                Alt = location.getAltitude();
                            }
                        }
                    }
                    Log.d("Network","Inside Network!!");

                }


            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return location;
    }

    public void stopUsingGPS(){
        if(locationManager!= null){
            try{
                locationManager.removeUpdates(GPSTracker.this);
            } catch (SecurityException e){e.printStackTrace();}
        }
    }

    public double getLatitude(){
        if(location != null){
            Lat = location.getLatitude();
        }
        return Lat;
    }

    public double getLongitude(){
        if(location != null){
            Long = location.getLongitude();
        }
        return Long;
    }

    public double getAltitude(){
        if (location != null){
            Alt = location.getAltitude();
        }
        return Alt;
    }

    public boolean canGetLocation(){
        return this.canGetLocation;
    }

    /*public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.set
    }*/

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
