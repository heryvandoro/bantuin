package net.slc.hoga.bantuin.Notification;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMsgService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("from : ", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("messages : ", "Message data payload: " + remoteMessage.getData());
            /*if ( Check if data needs to be processed by long running job  true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }*/
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("messages body : ", remoteMessage.getNotification().getBody());
        }
    }
}
