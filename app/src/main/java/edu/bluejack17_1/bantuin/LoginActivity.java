package edu.bluejack17_1.bantuin;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.bluejack17_1.bantuin.Helper.Config;
import edu.bluejack17_1.bantuin.Model.ActiveUser;
import edu.bluejack17_1.bantuin.Model.User;

import java.util.Arrays;

public class LoginActivity extends MasterActivity implements View.OnClickListener, OnCompleteListener, FacebookCallback<LoginResult> {

    private Button loginGoogle, loginFacebook, btnLogin;
    private EditText txtEmail, txtPassword;
    private TextView btnRegister;
    private GoogleSignInOptions gso;
    private CallbackManager mCallbackManager;
    public GoogleSignInClient googleSignInClient;
    private AuthCredential credential;
    private DatabaseReference userDatabase;
    private User tempUser = null;

    LinearLayout linearLayout;
    ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.setApplicationId(getString(R.string.facebook_application_id));
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        } else {
            nextProcess();
        }
        return;
    }

    private void nextProcess() {
        initializeComponent();
        checkAvailbility();

        if (ActiveUser.isLogged()) moveToHome();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    nextProcess();
                } else {
                    System.exit(0);
                }
                return;
            }
        }
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

    public void initializeComponent() {
        getSupportActionBar().hide();
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        spinner = findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        linearLayout = findViewById(R.id.linearLayout);

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

    private void checkAvailbility() {
        switch (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)) {
            case ConnectionResult.SERVICE_MISSING:
                GoogleApiAvailability.getInstance().getErrorDialog(this, ConnectionResult.SERVICE_MISSING, 0).show();
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                GoogleApiAvailability.getInstance().getErrorDialog(this, ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED, 0).show();
                break;
            case ConnectionResult.SERVICE_DISABLED:
                GoogleApiAvailability.getInstance().getErrorDialog(this, ConnectionResult.SERVICE_DISABLED, 0).show();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginGoogle:
                signIn(1);
                break;
            case R.id.loginFacebook:
                signIn(2);
                break;
            case R.id.btnRegister:
                movetoRegister();
                break;
            case R.id.btnLogin:
                signIn(3);
                break;
        }
    }

    private void signIn(int code) {
        if (code == 1) {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 1);
        } else if (code == 2) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        } else if (code == 3) {
            linearLayout.setAlpha((float) 0.2);
            spinner.setVisibility(View.VISIBLE);
            String email = txtEmail.getText().toString();
            String password = txtPassword.getText().toString();
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            linearLayout.setAlpha((float) 0.2);
            spinner.setVisibility(View.VISIBLE);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(this, this);
            } catch (ApiException e) {
                e.printStackTrace();
                linearLayout.setAlpha((float) 1.0);
                spinner.setVisibility(View.GONE);
                Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onComplete(@NonNull Task task) {
        if (task.isSuccessful()) {
            ActiveUser.setUser(FirebaseAuth.getInstance().getCurrentUser());
            checkAccountExistsDB();
        } else {
            linearLayout.setAlpha((float) 1.0);
            spinner.setVisibility(View.GONE);
            Toast.makeText(this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        linearLayout.setAlpha((float) 0.2);
        spinner.setVisibility(View.VISIBLE);
        credential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, this);
    }

    private void moveToHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void movetoRegister() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void checkAccountExistsDB() {
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uid = ActiveUser.getUser().getUid();
                if (!dataSnapshot.hasChild(uid)) {
                    tempUser = new User(ActiveUser.getUser().getDisplayName(),
                            ActiveUser.getUser().getEmail(),
                            ActiveUser.getUser().getPhotoUrl().toString(), uid);

                    userDatabase.child(uid).setValue(tempUser);
                }
                linearLayout.setAlpha((float) 1.0);
                spinner.setVisibility(View.GONE);
                moveToHome();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}