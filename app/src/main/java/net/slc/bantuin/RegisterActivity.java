package net.slc.bantuin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import net.slc.bantuin.Model.ActiveUser;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, OnCompleteListener{

    Button btnRegister;
    EditText textFullname, textEmail, textPassword, textRepassword;

    private boolean isValid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeComponent();
    }

    private void initializeComponent(){
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        textFullname = findViewById(R.id.textFullname);
        textEmail = findViewById(R.id.textEmail);
        textPassword = findViewById(R.id.textPassword);
        textRepassword = findViewById(R.id.textRePassword);
    }

    private void moveToHome(){
        startActivity(new Intent(this, MainActivity.class));
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
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, this);
        }
    }

    @Override
    public void onComplete(@NonNull Task task) {
        if (task.isSuccessful()) {
            ActiveUser.setUser(FirebaseAuth.getInstance().getCurrentUser());
            moveToHome();
        } else {
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
