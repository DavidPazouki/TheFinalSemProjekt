package at.fhooe.mc.android.Arrived;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;

public class GPSService extends Service {

    private static final String TAG = "xdd";
    double radius;
    String phoneNumber;
    String message;
    double lon1;
    double lat1;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        radius = 10;
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("entries", 0);
        int entries = sharedPreferences.getInt("entries", -1);
        phoneNumber = sharedPreferences.getString("phoneNumber_" + (entries - 1), "");
        message = sharedPreferences.getString("message_" + (entries - 1), "");
        lon1 = sharedPreferences.getFloat("lon_" + (entries - 1), 0);
        lat1 = sharedPreferences.getFloat("lat_" + (entries - 1), 0);
        Log.e(TAG, "ziellocation " + lon1 + " " + lat1);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e(TAG, "onLocationChanged: " + location);
                if (getDistance(lon1, lat1, location.getLongitude(), location.getLatitude()) < radius) {
                    Log.i(TAG, "sending sms");
                    SmsManager bat = SmsManager.getDefault();
                    bat.sendTextMessage(phoneNumber, null, message, null, null);
                    stopService(new Intent(GPSService.this, CreateEntry.class));
                    stopSelf();
                }
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.e(TAG, "onProviderDisabled: " + provider);
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.e(TAG, "onProviderEnabled: " + provider);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.e(TAG, "onStatusChanged: " + provider);
            }
        };
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        //noinspection MissingPermission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private double getDistance(double lon1, double lat1, double lon2, double lat2) {
        // distance between latitudes and longitudes
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // convert to radians
        double lati1 = Math.toRadians(lat1);
        double lati2 = Math.toRadians(lat2);

        // apply formula
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(lati1) *
                        Math.cos(lati2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        Log.i(TAG, "distance: " + rad * c);
        return rad * c;
    }
}