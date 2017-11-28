package net.slc.hoga.bantuin;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import net.slc.hoga.bantuin.Adapter.VolunteerAdapter;
import net.slc.hoga.bantuin.Model.ActiveUser;
import net.slc.hoga.bantuin.Model.Event;
import net.slc.hoga.bantuin.Model.User;

import java.util.ArrayList;
import java.util.List;

public class EventDetailActivity extends MasterActivity implements ValueEventListener, OnMapReadyCallback, View.OnClickListener {

    ImageView imageView;
    TextView category, user, location, modalText;
    Button btnJoin;

    DatabaseReference database;
    GoogleMap map;

    String eventKey;
    Event event;

    LinearLayout listViewVolunteer;
    List<User> listVolunteer;
    VolunteerAdapter adapter;

    PopupWindow modal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        initializeComponent();
    }

    @Override
    public void initializeComponent() {
        eventKey = getIntent().getStringExtra("key");

        imageView = findViewById(R.id.imageView);
        category = findViewById(R.id.category);
        user = findViewById(R.id.user);
        location = findViewById(R.id.loctime);

        database = FirebaseDatabase.getInstance().getReference();
        database.child("events").child(eventKey).addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 event = dataSnapshot.getValue(Event.class);
                 loadContent();
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         }
        );

        btnJoin = findViewById(R.id.btnJoin);
        btnJoin.setOnClickListener(this);

        listVolunteer = new ArrayList<>();
        adapter = new VolunteerAdapter(getApplicationContext(), listVolunteer);
        listViewVolunteer = findViewById(R.id.listVolunteer);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private void loadContent() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(event.getTitle());
        location.setText(makeString("", event.getTime() + " at " + event.getLocation()));

        database.child("users").child(event.getOrganizer()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user.setText(makeString("", dataSnapshot.getValue(String.class)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        database.child("categories").child(event.getCategory().toString()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                category.setText(makeString(dataSnapshot.getValue(String.class), ""));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        database.child("events").child(eventKey).child("volunteers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    String uid = postSnapshot.getValue(String.class);
                    listViewVolunteer.removeAllViews();
                    listVolunteer.clear();
                    database.child("users").child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            listVolunteer.add(dataSnapshot.getValue(User.class));
                            listViewVolunteer.addView(adapter.getView(adapter.getCount()-1,null,listViewVolunteer));
                            adapter.notifyDataSetChanged();
                            dataSnapshot.getRef().removeEventListener(this);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        }
        );

        Picasso.with(this)
                .load(event.getPictures().get(0)).placeholder(getResources().getDrawable(R.drawable.load_event))
                .into(imageView);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initializeModal();
    }

    SpannableString makeString(String boldText, String normalText) {

        SpannableString str = new SpannableString(boldText + normalText);
        str.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng loc = new LatLng(event.getLat(), event.getLng());
        map.addMarker(new MarkerOptions().position(loc).title(event.getLocation()));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnJoin:
                joinEvent();
                break;
        }
    }

    private void initializeModal(){
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.popup_layout,null);
        modal = new PopupWindow(
                customView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        ImageButton closeButton = customView.findViewById(R.id.modalClose);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modal.dismiss();
            }
        });

        modalText = customView.findViewById(R.id.modalText);
    }

    private boolean isJoined(){
        for(User x : listVolunteer)
            if(x.getEmail().equals(ActiveUser.getUser().getEmail()))  return true;
        return false;
    }

    private void joinEvent() {
        if(isJoined())
            modalText.setText("Event already joined!");
        else
            modalText.setText("Thankyou for join this event :)");
        modal.showAtLocation(findViewById(R.id.rel), Gravity.CENTER, 0, 0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                modal.dismiss();
            }
        }, 2000);
    }
}
