package net.slc.hoga.bantuin;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import net.slc.hoga.bantuin.Adapter.UserAdapter;
import net.slc.hoga.bantuin.Helper.CustomFirebaseListener;
import net.slc.hoga.bantuin.Model.ActiveUser;
import net.slc.hoga.bantuin.Model.Event;
import net.slc.hoga.bantuin.Model.User;

import java.util.ArrayList;


public class EventDetailActivity extends MasterActivity implements OnMapReadyCallback, View.OnClickListener {

    ImageView imageView;
    TextView category, user, location, modalText;
    Button btnJoin;

    DatabaseReference database;
    GoogleMap map;

    String eventKey;
    Event event;

    LinearLayout listViewVolunteer;
    UserAdapter adapter;

    PopupWindow modal;
    ArrayList<User> volunteers;
    SupportMapFragment mapFragment;

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
                .child("events");

        imageView = findViewById(R.id.imageView);
        category = findViewById(R.id.category);
        user = findViewById(R.id.user);
        location = findViewById(R.id.loctime);

        database.child(eventKey).addValueEventListener(new CustomFirebaseListener() {
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

    private void loadContent() {
        //Toast.makeText(this, event.getLat().toString(), Toast.LENGTH_SHORT).show();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(event.getTitle());

        location.setText(makeString("", event.getTime() + " at " + event.getLocation()));
        user.setText(makeString("", event.getOrganizer().getName()));

        category.setText(makeString(event.getCategory().getName(), ""));

        Picasso.with(this)
                .load(event.getPictures().get(0)).placeholder(getResources().getDrawable(R.drawable.load_event))
                .into(imageView);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        if (event.getOrganizer().getUid().equals(ActiveUser.getUser().getUid())) {
            removeJoin();
        }

        if (event.getVolunteers() != null) {
            volunteers = new ArrayList<>(event.getVolunteers().values());
            adapter = new UserAdapter(getApplicationContext(), volunteers);
            listViewVolunteer.removeAllViews();

            for (int i = 0; i < adapter.getCount(); i++) {
                if (((User) adapter.getItem(i)).getUid().equals(ActiveUser.getUser().getUid()))
                    continue;
                View temp = adapter.getView(i, null, listViewVolunteer);
                listViewVolunteer.addView(temp);
                temp.setTag(((User) adapter.getItem(i)).getUid());
                temp.setOnClickListener(this);
                if (((User) adapter.getItem(i)).getUid().equals(ActiveUser.getUser().getUid()))
                    removeJoin();
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
            case R.id.item_user:
                Intent intent = new Intent(this, UserDetailActivity.class);
                intent.putExtra("uid", view.getTag().toString());
                startActivity(intent);
                break;
        }
    }

    private void initializeModal() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.popup_layout, null);
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

    private boolean isJoined() {
        if (volunteers == null) return false;
        for (User x : volunteers)
            if (x.getEmail().equals(ActiveUser.getUser().getEmail())) return true;
        return false;
    }

    private void joinEvent() {
        if (isJoined()) {
            showModal("Event already joined!");
        } else {
            database.addListenerForSingleValueEvent(new CustomFirebaseListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean isBentrok = false;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Event temp = postSnapshot.getValue(Event.class);
                        if (temp.getKey().equals(eventKey)) continue;
                        if (!temp.getDate().equals(event.getDate())) continue;
                        if (temp.getOrganizer().getUid().equals(ActiveUser.getUser().getUid())) {
                            isBentrok = true;
                            break;
                        }
                        ArrayList<User> vol = new ArrayList<>(temp.getVolunteers().values());
                        for (User x : vol) {
                            if (x.getUid().equals(ActiveUser.getUser().getUid())) {
                                isBentrok = true;
                                break;
                            }
                        }
                    }
                    if (isBentrok) {
                        showModal("Ups, can't join! There are an event on the day.");
                    } else {
                        database.child(eventKey).child("volunteers").child(ActiveUser.getUser()
                                .getUid()).setValue(new User(ActiveUser.getUser().getDisplayName(), ActiveUser.getUser().getEmail(),
                                ActiveUser.getUser().getPhotoUrl().toString(), ActiveUser.getUser().getUid()));
                        showModal("Thankyou for join this event :)");
                        removeJoin();
                    }
                }
            });
        }
    }

    private void showModal(String str){
        modalText.setText(str);
        modal.showAtLocation(findViewById(R.id.rel), Gravity.CENTER, 0, 0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                modal.dismiss();
            }
        }, 2000);
    }

    private void removeJoin() {
        ViewGroup layout = (ViewGroup) btnJoin.getParent();
        if (null != layout) {
            layout.removeView(btnJoin);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            params.setMargins(0, 0, 0, 10);
            layout.findViewById(R.id.scrollDetail).setLayoutParams(params);
        }
    }
}
