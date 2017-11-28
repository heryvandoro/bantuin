package net.slc.hoga.bantuin;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import net.slc.hoga.bantuin.Helper.CustomFirebaseListener;
import net.slc.hoga.bantuin.Model.ActiveUser;
import net.slc.hoga.bantuin.Model.User;

public class UserDetailActivity extends MasterActivity implements View.OnClickListener {

    ImageView userPhoto;
    TextView userName, userEmail;
    DatabaseReference database;
    String UID;
    Button btnAdd;

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

        database = FirebaseDatabase.getInstance().getReference();

        database.child("users").child(UID).addListenerForSingleValueEvent(new CustomFirebaseListener() {
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

        database.child("friends").child(ActiveUser.getUser().getUid()).addListenerForSingleValueEvent(
                new CustomFirebaseListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            //already friends
                            if (postSnapshot.getKey().equals(UID)) {
                                removeAdd();
                                break;
                            }
                        }
                    }
                });

        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnAdd) {
            database.child("friends")
                    .child(ActiveUser.getUser().getUid())
                    .child(UID).setValue(true);
            Toast.makeText(this, "Successfully added as friends!", Toast.LENGTH_SHORT).show();
            removeAdd();
        }
    }

    private void removeAdd() {
        ViewGroup layout = (ViewGroup) btnAdd.getParent();
        if (null != layout) {
            layout.removeView(btnAdd);
        }
    }
}
