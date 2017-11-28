package net.slc.hoga.bantuin;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import net.slc.hoga.bantuin.Helper.CustomFirebaseListener;
import net.slc.hoga.bantuin.Model.User;

public class UserDetailActivity extends MasterActivity {

    ImageView userPhoto;
    TextView userName, userEmail;
    DatabaseReference database;
    String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        initializeComponent();
    }

    @Override
    public void initializeComponent() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        userPhoto = findViewById(R.id.userPhoto);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);

        UID = getIntent().getStringExtra("uid");

        database = FirebaseDatabase.getInstance().getReference()
                .child("users").child(UID);

        database.addListenerForSingleValueEvent(new CustomFirebaseListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User temp = dataSnapshot.getValue(User.class);
                userName.setText(temp.getName());
                userEmail.setText(temp.getEmail());
                Picasso.with(getApplicationContext())
                        .load(temp.getPhoto())
                        .into(userPhoto);
                actionBar.setTitle(temp.getName());
            }
        });
    }
}
