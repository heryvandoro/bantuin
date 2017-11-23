package net.slc.bantuin;
//
//import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.widget.Toast;
//
//import com.firebase.ui.auth.AuthUI;
//import com.firebase.ui.auth.ResultCodes;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//import java.util.Arrays;
//import java.util.List;

public class UILoginActivity extends MasterActivity {
    @Override
    public void initializeComponent() {

    }

//    FirebaseUser activeUser;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_ui_login);
//
//        List<AuthUI.IdpConfig> providers = Arrays.asList(
//                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
//                new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
//                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
//                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build());
//
//        startActivityForResult(
//                AuthUI.getInstance()
//                        .createSignInIntentBuilder()
//                        .setAvailableProviders(providers)
//                        .setIsSmartLockEnabled(false)
//                        .build(),
//                1);
//    }
//
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 1) {
//            if (resultCode == ResultCodes.OK) {
//                activeUser = FirebaseAuth.getInstance().getCurrentUser();
//            } else {
//                //failed
//                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}
