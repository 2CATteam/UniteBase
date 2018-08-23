package uniteapp.uniteclient;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements DownloadCallback<JSONObject>, AddDialogFragment.AddDialogListener, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private final static int LOCATION_PERMISSION_CODE = 4;

    ArrayList<Button> toDos;
    ButtonAdapter adapter;

    private NetworkFragment mNetworkFragment;

    private boolean mDownloading = false;

    private Geofence geofence;
    private PendingIntent mGeofencePendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toDos = new ArrayList<>();//
        mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), DownloadParameters.url);

        Button toAdd = findViewById(R.id.addButton);
        toAdd.setTextColor(0xFFFFFFFF);
        toAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNoticeDialog();
            }
        });
        Button toRefresh = findViewById(R.id.refreshButton);
        toRefresh.setTextColor(0xFFFFFFFF);
        toRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doGet();
            }
        });

        RecyclerView toLayout = findViewById(R.id.placeButtonsHere);
        toLayout.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        toLayout.setLayoutManager(layoutManager);
        adapter = new ButtonAdapter(new ArrayList<Button>());
        toLayout.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback callback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(callback).attachToRecyclerView(toLayout);

        FirebaseMessaging.getInstance().subscribeToTopic("student");

        GeofencingClient mGeofencingClient = LocationServices.getGeofencingClient(this);
        geofence = new Geofence.Builder()
                .setRequestId("Tuttle")
                .setCircularRegion(36.06517485, -95.869829, 100f)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Success","Added Geofence");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });

        doGet();
    }

    private void doGet() {
        DownloadParameters toDo = new DownloadParameters("GET", null, null);
        if (!mDownloading && mNetworkFragment != null) {
            mNetworkFragment.startDownload(toDo, this);
            mDownloading = true;
        }
    }
    private void doPut(ArrayList<String> keys, ArrayList<Boolean> values) {
        DownloadParameters toDo = new DownloadParameters("PUT", keys, values);
        if (!mDownloading && mNetworkFragment != null) {
            mNetworkFragment.startDownload(toDo, this);
            mDownloading = true;
        }
    }
    private void doDelete(ArrayList<String> keys, ArrayList<Boolean> values) {
        DownloadParameters toDo = new DownloadParameters("DELETE", keys, values);
        if (!mDownloading && mNetworkFragment != null) {
            mNetworkFragment.startDownload(toDo, this);
            mDownloading = true;
        }
    }
    /*
    private void doPost(ArrayList<String> keys, ArrayList<Boolean> values) {
        DownloadParameters toDownload = new DownloadParameters("POST", keys, values);
        if (!mDownloading && mNetworkFragment != null) {
            mNetworkFragment.startDownload(toDownload, this);
            mDownloading = true;
        }
    }
    */

    @Override
    public void updateFromDownload(JSONObject result) {
        Iterator<String> keys = result.keys();
        ArrayList<String> toDos = new ArrayList<>();
        while (keys.hasNext())
        {
            String key = keys.next();
            if (!toDos.contains(key))
            {
                toDos.add(key);
            }
        }
        for (String key: toDos)
        {
            try {
                makeButton(key, result.getBoolean(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void makeButton(final String toDo, final Boolean isDone)
    {
        Button toAdd = new Button(this);
        toAdd.setTextColor(0xFFFFFFFF);
        toAdd.setText(toDo);
        toAdd.setTransformationMethod(null);
        if (isDone)
        {
            toAdd.setBackground(ContextCompat.getDrawable(this, R.drawable.todo_done));
        } else {
            toAdd.setBackground(ContextCompat.getDrawable(this, R.drawable.todo_pending));
        }
        toAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> keys = new ArrayList<>();
                ArrayList<Boolean> values = new ArrayList<>();
                keys.add(toDo);
                values.add(!isDone);
                doPut(keys, values);
            }
        });
        adapter.addItem(toAdd);
    }

    @Override
    public void updateFromError(String errorString) {

    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        return connectivityManager.getActiveNetworkInfo();
    }

    @Override
    public void finishDownloading() {
        mDownloading = false;
        if (mNetworkFragment != null) {
            mNetworkFragment.cancelDownload();
        }
    }

    @Override
    public void onDialogPositiveClick(String toAdd) {
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<Boolean> values = new ArrayList<>();
        keys.add(toAdd);
        values.add(false);
        doPut(keys, values);
    }

    public void showNoticeDialog()
    {
        DialogFragment dialog = new AddDialogFragment();
        dialog.show(getSupportFragmentManager(), "AddDialogFragment");
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        adapter.removeItem(viewHolder.getAdapterPosition());
        ArrayList<String> toDelete = new ArrayList<>();
        toDelete.add((((Button) ((ButtonAdapter.ViewHolder) viewHolder).layout.getChildAt(0)).getText()).toString());
        ArrayList<Boolean> trues = new ArrayList<>();
        trues.add(true);
        doDelete(toDelete, trues);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null)
        {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    //TODO: Fork to student-teacher versions
}
