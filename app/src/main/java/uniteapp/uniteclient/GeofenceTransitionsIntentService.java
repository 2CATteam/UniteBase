package uniteapp.uniteclient;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceTransitionsIntentService extends IntentService {

    public GeofenceTransitionsIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError())
        {
            Log.e("Geofence error", String.valueOf(geofencingEvent.getErrorCode()));
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
        {

        }
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
        {

        }
    }
}
