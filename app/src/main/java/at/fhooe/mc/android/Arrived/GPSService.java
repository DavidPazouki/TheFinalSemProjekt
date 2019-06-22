package at.fhooe.mc.android.Arrived;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

public class GPSService extends Service implements android.location.LocationListener{
    public GPSService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        String phoneNumber = intent.getStringExtra("phoneNumber");
        String message = intent.getStringExtra("message");
        String place = intent.getStringExtra("place");
        String location = "xxx";
        if(checkIfNear(place,location)){
            SmsManager bat = SmsManager.getDefault();
            bat.sendTextMessage(phoneNumber,null,message,null,null);
        }




        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

    private boolean checkIfNear(String place, String location) {
        //implement algorithm
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {

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
}
