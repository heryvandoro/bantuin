package net.slc.bantuin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import net.slc.bantuin.Model.ActiveUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponent();
    }

    private void initializeComponent(){
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnLogout :
                logout();
                break;
        }
    }

    private void logout(){
        ActiveUser.logout();
        if(!ActiveUser.isLogged()){
            Toast.makeText(this, "a", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
