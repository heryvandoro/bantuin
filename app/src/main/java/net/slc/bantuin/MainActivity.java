//package net.slc.bantuin.bantuin;
//
//import android.content.Intent;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.facebook.AccessToken;
//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.FacebookSdk;
//import com.facebook.login.LoginManager;
//import com.facebook.login.LoginResult;
//import com.facebook.login.widget.LoginButton;
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.auth.api.signin.Google;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GoogleApiAvailability;
//import com.google.android.gms.common.api.ApiException;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthCredential;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FacebookAuthProvider;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.auth.GoogleAuthProvider;
//
//import java.util.Arrays;
//
//public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnCompleteListener{
//
//    private Button loginGoogle;
//    private Button loginFacebook;
//    private GoogleSignInClient googleSignInClient;
//    private FirebaseAuth mAuth;
//    private GoogleSignInOptions gso;
//    private CallbackManager mCallbackManager;
//    private static int GOOGLE_SIGN_IN_REQUEST_CODE;
//    public static FirebaseUser currentUser;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        FacebookSdk.setApplicationId(getString(R.string.facebook_application_id));
//        FacebookSdk.sdkInitialize(getApplicationContext());
//
//        setContentView(R.layout.activity_main);
//
//        initializeComponents();
//        checkAvailbility();
//    }
//
//    private void initializeComponents(){
//        //for intent data status code
//        GOOGLE_SIGN_IN_REQUEST_CODE = 1;
//
//        loginGoogle = (Button) findViewById(R.id.loginGoogle);
//        loginGoogle.setOnClickListener(this);
//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        googleSignInClient = GoogleSignIn.getClient(this, gso);
//
//        loginFacebook = (Button) findViewById(R.id.loginFacebook);
//        loginFacebook.setOnClickListener(this);
//        mCallbackManager = CallbackManager.Factory.create();
//
//        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                handleFacebookAccessToken(loginResult.getAccessToken());
//            }
//
//            @Override
//            public void onCancel() {
//
//                // ...
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//
//                // ...
//            }
//        });
//
//        mAuth = FirebaseAuth.getInstance();
//        if(mAuth.getCurrentUser()!=null && !mAuth.getCurrentUser().getEmail().equals("")){
//            Toast.makeText(this, mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
//            FirebaseAuth.getInstance().signOut();
//        }
//    }
//
//    public void checkAvailbility(){
//        switch (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)){
//            case ConnectionResult.SERVICE_MISSING:
//                GoogleApiAvailability.getInstance().getErrorDialog(this, ConnectionResult.SERVICE_MISSING,0).show();
//                break;
//            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
//                GoogleApiAvailability.getInstance().getErrorDialog(this,ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED,0).show();
//                break;
//            case ConnectionResult.SERVICE_DISABLED:
//                GoogleApiAvailability.getInstance().getErrorDialog(this,ConnectionResult.SERVICE_DISABLED,0).show();
//                break;
//        }
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch(view.getId()){
//            case R.id.loginGoogle :
//                signIn(GOOGLE_SIGN_IN_REQUEST_CODE);
//                break;
//            case R.id.loginFacebook :
//                signIn(2);
//                break;
//        }
//    }
//
//    public void signIn(int code){
//        if(code==GOOGLE_SIGN_IN_REQUEST_CODE){
//            Intent signInIntent = googleSignInClient.getSignInIntent();
//            startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);
//        }else{
//            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        mCallbackManager.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == GOOGLE_SIGN_IN_REQUEST_CODE){
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
//                mAuth.signInWithCredential(credential)
//                        .addOnCompleteListener(this, this);
//            } catch (ApiException e) {
//                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//    @Override
//    public void onComplete(@NonNull Task task) {
//        if (task.isSuccessful()) {
//            currentUser = mAuth.getCurrentUser();
//        } else {
//            Toast.makeText(this, "Authentication failed.",
//                    Toast.LENGTH_LONG).show();
//        }
//    }
//
//    private void handleFacebookAccessToken(AccessToken token) {
//        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, this);
//    }
//}