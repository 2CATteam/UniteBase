package uniteapp.uniteclient;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.Timer;
import java.util.TimerTask;

public class GeofenceTransitionsIntentService extends IntentService {

    public GeofenceTransitionsIntentService() {
        super("Geofence");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("Tripped!", "Tripped a fence!");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError())
        {
            Log.e("Geofence error", String.valueOf(geofencingEvent.getErrorCode()));
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        Log.d("Tripped!", String.valueOf(geofenceTransition));

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
        {
            Log.d("Tripped", "Entered a geofence!");
            final MainActivity.GeofenceResultReceiver receiver = MainActivity.mGeofenceResultReceiver;
            receiver.send(GeofenceResultReceiver.WAKE_CODE, new Bundle());
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    receiver.send(MainActivity.GeofenceResultReceiver.JOIN_CODE, new Bundle());
                }
            }, 10000);
        }
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
        {
            Log.d("Tripped", "Left a geofence!");
            final MainActivity.GeofenceResultReceiver receiver = MainActivity.mGeofenceResultReceiver;
            receiver.send(MainActivity.GeofenceResultReceiver.WAKE_CODE, new Bundle());
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    receiver.send(MainActivity.GeofenceResultReceiver.LEAVE_CODE, new Bundle());
                }
            }, 10000);
        }
    }
}
