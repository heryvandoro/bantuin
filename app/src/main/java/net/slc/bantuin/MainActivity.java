package net.slc.bantuin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.slc.bantuin.Model.ActiveUser;
import net.slc.bantuin.Model.Category;
import net.slc.bantuin.Model.User;

import java.util.ArrayList;

public class MainActivity extends MasterActivity implements View.OnClickListener {

    Button btnLogout, btnProfile;
    DatabaseReference categoryDatabase;
    ArrayList<Category> categories = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponent();


    }

    public void initializeComponent() {
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);

        btnProfile = findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogout:
                logout();
                break;
            case R.id.btnProfile:
                profile();
                break;
        }
    }

    private void logout() {
        ActiveUser.logout();
        if (!ActiveUser.isLogged()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    public void profile() {
        String email = ActiveUser.getUser().getEmail().toString();
        String name = ActiveUser.getUser().getDisplayName().toString();
        Toast.makeText(this, name + ", " + email, Toast.LENGTH_SHORT).show();
    }
}
