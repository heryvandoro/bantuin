package net.slc.hoga.bantuin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

import net.slc.hoga.bantuin.Helper.Config;
import net.slc.hoga.bantuin.Model.*;

import java.util.Arrays;

public class LoginActivity extends MasterActivity implements View.OnClickListener, OnCompleteListener, FacebookCallback<LoginResult>{

    private Button loginGoogle, btnRegister, loginFacebook, btnLogin;
    private EditText txtEmail, txtPassword;
    private GoogleSignInOptions gso;
    private CallbackManager mCallbackManager;
    public GoogleSignInClient googleSignInClient;
    private AuthCredential credential;
    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.setApplicationId(getString(R.string.facebook_application_id));
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeComponent();
        if(ActiveUser.isLogged()){
            addAccountToDB();
            moveToHome();
        }
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

        userDatabase = FirebaseDatabase.getInstance().getReference();
        userDatabase = userDatabase.child("users");
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
            addAccountToDB();
            moveToHome();
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

    private void addAccountToDB(){
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uid = ActiveUser.getUser().getUid();
                if(dataSnapshot.hasChild(uid)) return;
                User user = new User(ActiveUser.getUser().getDisplayName(),ActiveUser.getUser().getEmail(), Config.TEMP_PHOTO);
                userDatabase.child(uid).setValue(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}