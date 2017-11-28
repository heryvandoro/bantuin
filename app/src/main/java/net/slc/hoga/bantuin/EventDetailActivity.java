package net.slc.hoga.bantuin;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import net.slc.hoga.bantuin.Helper.CustomFirebaseListener;
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
    VolunteerAdapter adapter;

    PopupWindow modal;
    ArrayList<User> volunteers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        initializeComponent();
    }

    @Override
    public void initializeComponent() {
        eventKey = getIntent().getStringExtra("key");
        database = FirebaseDatabase.getInstance().getReference()
                .child("events").child(eventKey);

        imageView = findViewById(R.id.imageView);
        category = findViewById(R.id.category);
        user = findViewById(R.id.user);
        location = findViewById(R.id.loctime);
        database.addValueEventListener(new CustomFirebaseListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               event = dataSnapshot.getValue(Event.class);
               loadContent();
           }
        });

        btnJoin = findViewById(R.id.btnJoin);
        btnJoin.setOnClickListener(this);

        listViewVolunteer = findViewById(R.id.listVolunteer);
        initializeModal();
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
        user.setText(makeString("", event.getOrganizer().getName()));

        category.setText(makeString(event.getCategory().getName(), ""));

        Picasso.with(this)
                .load(event.getPictures().get(0)).placeholder(getResources().getDrawable(R.drawable.load_event))
                .into(imageView);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(event.getVolunteers()!=null){
            volunteers = new ArrayList<User>(event.getVolunteers().values());
            adapter = new VolunteerAdapter(getApplicationContext(), volunteers);
            listViewVolunteer.removeAllViews();

            for(int i=0; i<adapter.getCount(); i++){
                listViewVolunteer.addView(adapter.getView(i,null,listViewVolunteer));
            }
        }
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
        if(volunteers==null) return false;
        for(User x : volunteers)
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
