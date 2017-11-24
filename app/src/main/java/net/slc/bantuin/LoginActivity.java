package net.slc.bantuin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import net.slc.bantuin.Model.ActiveUser;

import java.util.Arrays;

public class LoginActivity extends MasterActivity implements View.OnClickListener, OnCompleteListener, FacebookCallback<LoginResult>{

    private Button loginGoogle, btnRegister, loginFacebook, btnLogin;
    private EditText txtEmail, txtPassword;
    private GoogleSignInOptions gso;
    private CallbackManager mCallbackManager;
    public GoogleSignInClient googleSignInClient;
    private AuthCredential credential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.setApplicationId(getString(R.string.facebook_application_id));
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(ActiveUser.isLogged()){
            moveToHome();
        }

        initializeComponent();
        checkAvailbility();
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        handleFacebookAccessToken(loginResult.getAccessToken());
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException error) {

    }

    public void initializeComponent(){
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        loginGoogle = findViewById(R.id.loginGoogle);
        loginGoogle.setOnClickListener(this);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        loginFacebook = findViewById(R.id.loginFacebook);
        loginFacebook.setOnClickListener(this);
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager, this);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        txtEmail = findViewById(R.id.textEmail);
        txtPassword = findViewById(R.id.textPassword);
    }

    private void checkAvailbility(){
        switch (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)){
            case ConnectionResult.SERVICE_MISSING:
                GoogleApiAvailability.getInstance().getErrorDialog(this, ConnectionResult.SERVICE_MISSING,0).show();
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                GoogleApiAvailability.getInstance().getErrorDialog(this,ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED,0).show();
                break;
            case ConnectionResult.SERVICE_DISABLED:
                GoogleApiAvailability.getInstance().getErrorDialog(this,ConnectionResult.SERVICE_DISABLED,0).show();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.loginGoogle :
                signIn(1);
                break;
            case R.id.loginFacebook :
                signIn(2);
                break;
            case R.id.btnRegister :
                movetoRegister();
                break;
            case R.id.btnLogin :
                signIn(3);
                break;
        }
    }

    private void signIn(int code){
        if(code==1){
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 1);
        }else if(code==2){
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        }else if(code==3){
            String email = txtEmail.getText().toString();
            String password = txtPassword.getText().toString();
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this,this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(this, this);
            } catch (ApiException e) {
                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_LONG).show();
            }
        }else{
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onComplete(@NonNull Task task) {

        if(task.isSuccessful()){
            ActiveUser.setUser(FirebaseAuth.getInstance().getCurrentUser());
            moveToHome();
//            ActiveUser.getUser().linkWithCredential(credential)
//                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                ActiveUser.setUser(task.getResult().getUser());
//                            } else {
//                                Toast.makeText(LoginActivity.this, "Authentication failed.",
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
        }else{
            Toast.makeText(this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void handleFacebookAccessToken(AccessToken token) {
        credential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, this);
    }

    private void moveToHome(){
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void movetoRegister(){
        startActivity(new Intent(this, RegisterActivity.class));
    }
}