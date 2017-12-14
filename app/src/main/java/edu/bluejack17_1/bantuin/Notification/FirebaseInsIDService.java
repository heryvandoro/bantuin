package edu.bluejack17_1.bantuin.Notification;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class FirebaseInsIDService extends FirebaseInstanceIdService{
    @Override
    public void onTokenRefresh() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String token = FirebaseInstanceId.getInstance().getToken();
                Log.w("tokenFCM", token);
            }
        }, 2000);
    }
}
