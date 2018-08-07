package uniteapp.uniteclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class GeofenceResultReceiver extends ResultReceiver {
    private GeofenceCallback storedCallback;

    public static final int WAKE_CODE = 1;
    public static final int JOIN_CODE = 2;
    public static final int LEAVE_CODE = 3;

    public interface GeofenceCallback
    {
        void doJoin();
        void doLeave();
        void doWake();
    }

    public void setReceiver(GeofenceCallback toSet)
    {
        this.storedCallback = toSet;
    }

    public GeofenceResultReceiver(Handler handler) {
        super(handler);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData)
    {
        switch (resultCode)
        {
            case WAKE_CODE:
                storedCallback.doWake();
                break;
            case JOIN_CODE:
                storedCallback.doJoin();
                break;
            case LEAVE_CODE:
                storedCallback.doLeave();
        }
    }
}
