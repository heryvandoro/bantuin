package net.slc.bantuin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.slc.bantuin.Model.ActiveUser;

public class MainActivity extends MasterActivity implements View.OnClickListener{

    Button btnLogout, btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponent();
    }

    public void initializeComponent(){
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);

        btnProfile = findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnLogout :
                logout();
                break;
            case R.id.btnProfile :
                profile();
                break;
        }
    }

    private void logout(){
        ActiveUser.logout();
        if(!ActiveUser.isLogged()){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    public void profile(){
        Toast.makeText(this, ActiveUser.getUser().getUid().toString(), Toast.LENGTH_SHORT).show();
    }
}
