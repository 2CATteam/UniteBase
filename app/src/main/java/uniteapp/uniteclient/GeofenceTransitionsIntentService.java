package uniteapp.uniteclient;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

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
            doWake();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    doPost(true);
                }
            }, 10000);
        }
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
        {
            Log.d("Tripped", "Left a geofence!");
            doWake();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    doPost(false);
                }
            }, 10000);
        }
    }

    private void doWake()
    {
        HttpsURLConnection connection = null;
        try {
            URL url = new URL(DownloadParameters.url);
            connection = (HttpsURLConnection) url.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(3000);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(3000);
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod("GET");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void doPost(Boolean doJoin)
    {
        HttpsURLConnection connection = null;
        try {
            URL url = new URL(DownloadParameters.url);
            connection = (HttpsURLConnection) url.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(3000);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(3000);
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod("POST");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);
            connection.setDoOutput(true);

            JSONObject toWrite = new JSONObject();
            toWrite.put("join", doJoin);
            OutputStream writeTo = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writeTo, "UTF-8"));
            writer.write(toWrite.toString());
            writer.close();
            writeTo.close();

            connection.connect();
            try {
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw new IOException("HTTP error code: " + responseCode);
                }
            } catch(SocketTimeoutException e)
            {
                e.printStackTrace();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
