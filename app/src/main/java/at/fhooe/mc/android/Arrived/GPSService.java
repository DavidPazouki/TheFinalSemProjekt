package at.fhooe.mc.android.Arrived;

import android.Manifest;
import android.app.NotificationManager;
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
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

public class GPSService extends Service {

    private static final String TAG = "xdd";
    int[] radius;
    String[] phoneNumber;
    String[] message;
    float[] lon1;
    float[] lat1;
    int entries;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "Service started");
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("entries", 0);

        entries = sharedPreferences.getInt("entries", 0);
        phoneNumber = new String[entries];
        message = new String[entries];
        lon1 = new float[entries];
        lat1 = new float[entries];
        radius = new int[entries];
        for (int i = 0; i < entries; i++) {
            phoneNumber[i] = sharedPreferences.getString("phoneNumber_" + i, "");
            message[i] = sharedPreferences.getString("message_" + i, "");
            lon1[i] = sharedPreferences.getFloat("lon_" + i, 0);
            lat1[i] = sharedPreferences.getFloat("lat_" + i, 0);
            radius[i] = sharedPreferences.getInt("radius_"+ i,0);
            Log.i(TAG, "searching for" + lon1[i] + " " + lat1[i] + "in" + radius[i]);
        }
        Log.i(TAG, "loaded entries");
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e(TAG, "onLocationChanged: " + location);
                SharedPreferences sharedPreferences1 = getApplicationContext().getSharedPreferences("entries", 0);
                for (int i = 0; i < entries; i++) {
                    if (!phoneNumber[i].equals("")) {
                        if (getDistance(lon1[i], lat1[i], location.getLongitude(), location.getLatitude()) < radius[i]) {
                            phoneNumber[i] = "";
                            Log.i(TAG, "sending sms");
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(phoneNumber[i], null, message[i], null, null);
                            NotificationManager notificationManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "")
                                    .setSmallIcon(R.drawable.ic_gps)
                                    .setContentTitle("Arrived")
                                    .setContentText("A message has been sent to " + phoneNumber[i]);
                            notificationManager.notify(0, builder.build());
                            SharedPreferences.Editor editor = sharedPreferences1.edit();
                            editor.remove("message_" + (i));
                            editor.remove("place_" + (i));
                            editor.remove("phoneNumber_" + (i));
                            editor.remove("lon_" + (i));
                            editor.remove("lat_" + (i));
                            editor.commit();
                        }
                    }
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
        //noinspection MissingPermission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "no permission granted");
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, locationListener);

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
        Log.i(TAG, "distance: " + rad * c+"km");
        return rad * c * 1000;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "service destroyed");
        super.onDestroy();
    }
}