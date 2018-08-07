package uniteapp.uniteclient;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceTransitionsIntentService extends IntentService {

    public GeofenceTransitionsIntentService(String name) {
        super(name);
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

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
        {
            Log.d("Tripped", "Entered a geofence!");
            final GeofenceResultReceiver receiver = intent.getParcelableExtra("receiver");
            receiver.send(GeofenceResultReceiver.WAKE_CODE, new Bundle());
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    receiver.send(GeofenceResultReceiver.JOIN_CODE, new Bundle());
                }
            }, 10000);
        }
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
        {
            Log.d("Tripped", "Left a geofence!");
            final GeofenceResultReceiver receiver = intent.getParcelableExtra("receiver");
            receiver.send(GeofenceResultReceiver.WAKE_CODE, new Bundle());
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    receiver.send(GeofenceResultReceiver.LEAVE_CODE, new Bundle());
                }
            }, 10000);
        }
    }
}
