package net.slc.hoga.bantuin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
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

import net.slc.hoga.bantuin.Adapter.EventSmallAdapter;
import net.slc.hoga.bantuin.Helper.Config;
import net.slc.hoga.bantuin.Helper.CustomFirebaseListener;
import net.slc.hoga.bantuin.Model.ActiveUser;
import net.slc.hoga.bantuin.Model.Event;
import net.slc.hoga.bantuin.Model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UserDetailActivity extends MasterActivity implements View.OnClickListener {

    ImageView userPhoto;
    TextView userName, userEmail;
    DatabaseReference database;
    String UID;
    Button btnAdd;
    EventSmallAdapter adapter;
    ArrayList<Event> events;
    LinearLayout listEvent;

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
                                viewUpcomingEvent();
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
        switch (view.getId()) {
            case R.id.btnAdd:
                database.child("friends")
                        .child(ActiveUser.getUser().getUid())
                        .child(UID).setValue(true);
                Toast.makeText(this, "Successfully added as friends!", Toast.LENGTH_SHORT).show();
                removeAdd();
                viewUpcomingEvent();
                break;
            case R.id.item_event:
                Intent intent = new Intent(this, EventDetailActivity.class);
                intent.putExtra("key", view.getTag().toString());
                startActivity(intent);
                break;
        }
    }

    private void removeAdd() {
        ViewGroup layout = (ViewGroup) btnAdd.getParent();
        if (null != layout) {
            layout.removeView(btnAdd);
        }
    }

    private void viewUpcomingEvent() {
        listEvent = findViewById(R.id.listEvent);
        events = new ArrayList<>();
        database.child("events").addListenerForSingleValueEvent(
                new CustomFirebaseListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Event event = postSnapshot.getValue(Event.class);
                            SimpleDateFormat sdf = new SimpleDateFormat(Config.DATE_FORMAT, Locale.US);
                            Date d1, d2;
                            try {
                                d1 = sdf.parse(event.getDate());
                                d2 = sdf.parse(sdf.format(Calendar.getInstance().getTime()));
                                if (d1.compareTo(d2) >= 0) {
                                    if (event.getVolunteers() != null) {
                                        for (User user : event.getVolunteers().values()) {
                                            if (user.getUid().equals(UID))
                                                events.add(event);
                                        }
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        adapter = new EventSmallAdapter(events, getApplicationContext());
                        for (int i = 0; i < adapter.getCount(); i++) {
                            View temp = adapter.getView(i, null, listEvent);
                            listEvent.addView(temp);
                            temp.setTag(((Event) adapter.getItem(i)).getKey());
                            temp.setOnClickListener(UserDetailActivity.this);
                        }
                    }
                });
    }
}