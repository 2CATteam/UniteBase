package uniteapp.uniteclient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements DownloadCallback<JSONObject>, AddDialogFragment.AddDialogListener {

    ArrayList<String> toDos;
    //HashMap<String, Boolean> toDoStates;

    private NetworkFragment mNetworkFragment;

    private boolean mDownloading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toDos = new ArrayList<String>();//
        mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), DownloadParameters.url);

        Button toAdd = findViewById(R.id.addButton);
        toAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNoticeDialog();
            }
        });

        doGet();



        //LinearLayout layout = findViewById(R.id.placeButtonsHere);
        /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); */

        /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
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
    private void doPost(ArrayList<String> keys, ArrayList<Boolean> values) {
        DownloadParameters toDo = new DownloadParameters("POST", keys, values);
        if (!mDownloading && mNetworkFragment != null) {
            mNetworkFragment.startDownload(toDo, this);
            mDownloading = true;
        }
    }

    @Override
    public void updateFromDownload(JSONObject result) {
        Iterator<String> keys = result.keys();
        while (keys.hasNext())
        {
            String key = keys.next();
            if (!toDos.contains(key))
            {
                toDos.add(key);
            }
        }
        clearButtons();
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
        toAdd.setText(toDo);
        if (isDone)
        {
            toAdd.setBackgroundColor(0xFF00FF00);
        } else {
            toAdd.setBackgroundColor(0xFFFF4444);
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
        LinearLayout layout = findViewById(R.id.placeButtonsHere);
        layout.addView(toAdd);
    }

    @Override
    public void updateFromError(String errorString) {

    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch(progressCode) {
            // You can add UI behavior for progress updates here.
            case Progress.ERROR:

                break;
            case Progress.CONNECT_SUCCESS:

                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:

                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:

                break;
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:

                break;
        }
    }

    @Override
    public void finishDownloading() {
        mDownloading = false;
        if (mNetworkFragment != null) {
            mNetworkFragment.cancelDownload();
        }
    }

    public void clearButtons() {
        LinearLayout layout = findViewById(R.id.placeButtonsHere);
        layout.removeAllViews();
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

    /*private void createButton(LinearLayout layout) {
        FloatingActionButton fab = new FloatingActionButton(this);
        fab.setLayoutParams(new LinearLayout.LayoutParams(this));
        layout.addView(fab);
    }*/
}
