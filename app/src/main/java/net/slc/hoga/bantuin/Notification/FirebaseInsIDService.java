package net.slc.hoga.bantuin.Notification;

import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseInsIDService extends FirebaseInstanceIdService{
    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Toast.makeText(this, token, Toast.LENGTH_SHORT).show();
        //Log .d("token", "Refreshed token: " + token);

        //sendRegistrationToServer(refreshedToken);
    }
}
