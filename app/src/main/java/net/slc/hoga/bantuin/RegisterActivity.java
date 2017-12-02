package net.slc.hoga.bantuin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.slc.hoga.bantuin.Helper.Config;
import net.slc.hoga.bantuin.Model.ActiveUser;
import net.slc.hoga.bantuin.Model.User;

public class RegisterActivity extends MasterActivity implements View.OnClickListener, OnCompleteListener{

    private Button btnRegister;
    private EditText textFullname, textEmail, textPassword, textRepassword;
    private DatabaseReference userDatabase;

    private boolean isValid = true;

    LinearLayout linearLayout;
    ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        spinner = findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        initializeComponent();
    }

    public void initializeComponent(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        textFullname = findViewById(R.id.textFullname);
        textEmail = findViewById(R.id.textEmail);
        textPassword = findViewById(R.id.textPassword);
        textRepassword = findViewById(R.id.textRePassword);

        userDatabase = FirebaseDatabase.getInstance().getReference();
        userDatabase = userDatabase.child("users");
    }

    private void moveToHome(){
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void showError(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        isValid = false;
    }

    private void doRegister(){
        String fullname = textFullname.getText().toString();
        String email = textEmail.getText().toString();
        String password = textPassword.getText().toString();
        String repassword = textRepassword.getText().toString();
        if(fullname.isEmpty()) showError("Fullname must be filled.");
        else if(email.isEmpty()) showError("Email must be filled.");
        else if(password.isEmpty()) showError("Password must be filled.");
        else if(repassword.isEmpty()) showError("Re-password must be filled.");
        else if(!repassword.equals(password)) showError("Password and re-password must be same.");
        else isValid = true;

        if(isValid){
            spinner.setVisibility(View.VISIBLE);
            linearLayout = findViewById(R.id.linearLayout);
            linearLayout.setAlpha((float)0.2);

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, this);
        }
    }

    @Override
    public void onComplete(@NonNull Task task) {
        if (task.isSuccessful()) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(textFullname.getText().toString())
                    .setPhotoUri(Uri.parse(Config.TEMP_PHOTO))
                    .build();

            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                ActiveUser.setUser(FirebaseAuth.getInstance().getCurrentUser());
                                String uid = ActiveUser.getUser().getUid();
                                User user = new User(ActiveUser.getUser().getDisplayName(),
                                        ActiveUser.getUser().getEmail(), Config.TEMP_PHOTO, uid);
                                userDatabase.child(uid).setValue(user);
                                spinner.setVisibility(View.GONE);
                                linearLayout.setAlpha((float)1.0);
                                moveToHome();
                            }
                        }
                    });
        } else {
            spinner.setVisibility(View.GONE);
            linearLayout.setAlpha((float)1.0);
            Toast.makeText(RegisterActivity.this, task.getException().getMessage().toString(),
                    Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnRegister :
                doRegister();
                break;
        }
    }
}
