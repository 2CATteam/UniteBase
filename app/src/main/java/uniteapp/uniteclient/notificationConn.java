package uniteapp.uniteclient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class notificationConn extends FirebaseMessagingService {
    private static final String TAG = "DEBUG ";

    public notificationConn() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "ID: " + remoteMessage.getMessageId());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        sendNotification(remoteMessage.getNotification().getBody());

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void sendNotification(String toSend)
    {
        Log.d(TAG, "Sending notification: " + toSend);
        Intent toDo = new Intent(this, MainActivity.class);
        PendingIntent toReallyDo = PendingIntent.getActivity(this, 0, toDo, 0);
        NotificationManager notif = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification toPost = new Notification.Builder(getApplicationContext())
                .setContentTitle("Testing")
                .setContentText(toSend)
                .setSmallIcon(R.drawable.union_alert_icon)
                .setContentIntent(toReallyDo)
                .build();
        toPost.flags |= Notification.FLAG_AUTO_CANCEL;
        assert notif != null;
        notif.notify(0,toPost);
    }
}
