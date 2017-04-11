package jfh.jawstestapp1a.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import jfh.jawstestapp1a.common.Constants;

import static android.location.LocationProvider.AVAILABLE;
import static android.location.LocationProvider.OUT_OF_SERVICE;
import static android.location.LocationProvider.TEMPORARILY_UNAVAILABLE;

public class GPSService extends Service {
    private static final String TAG = GPSService.class.getSimpleName();

    private boolean enableUpdates = true;

    //Private vars
    private LocationListener locationListener;
    private LocationManager locationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationListener = new UserLocationListener();
        locationManager =
                (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (!enableUpdates) return;

        //noinspection MissingPermission
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                3000,
                0,
                locationListener
                );

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager!=null) {
            Log.i(TAG, "onDestroy: Removing updates");
            //noinspection MissingPermission
            locationManager.removeUpdates(locationListener);
        }
    }


    /**
     * LocationListener to handle changes to GPS.
     */
    public class UserLocationListener implements LocationListener {
        private final String TAG = UserLocationListener.class.getSimpleName();

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged() called with: location = [" + location + "]");
            Intent i = new Intent(Constants.INTENT_GPS_UPDATE_ACTION);
            i.putExtra(Constants.INTENT_GPS_LAT, location.getLatitude());
            i.putExtra(Constants.INTENT_GPS_LONG, location.getLongitude());
            i.putExtra(Constants.INTENT_GPS_TIME, location.getTime());
            i.putExtra(Constants.INTENT_GPS_ACCURACY, location.getAccuracy());
            i.putExtra(Constants.INTENT_GPS_ALTITUDE, location.getAltitude());
            i.putExtra(Constants.INTENT_GPS_BEARING, location.getBearing());
            i.putExtra(Constants.INTENT_GPS_SPEED, location.getSpeed());

            sendBroadcast(i);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

            String statusStr;
            switch (status) {
                case OUT_OF_SERVICE: statusStr = "GPS Out of Service"; break;
                case TEMPORARILY_UNAVAILABLE: statusStr = "GPS Temporarily Unavailable"; break;
                case AVAILABLE: statusStr = "GPS Available"; break;
                default: statusStr = "Unknown status";
            }
            Log.d(TAG, "onStatusChanged: " +statusStr + " satellites: " + extras.get("satellites"));

        }

        @Override
        public void onProviderEnabled(String s) {
            Log.v(TAG, "onProviderEnabled: " + s);
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.i(TAG, "onProviderDisabled");
            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        }
    }

}
