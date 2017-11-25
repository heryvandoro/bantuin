package net.slc.hoga.bantuin.Notification;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import net.slc.hoga.bantuin.LoginActivity;


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

        //sendRegistrationToServer(refreshedToken);
    }
}
