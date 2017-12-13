package net.slc.hoga.bantuin.Fragment;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.slc.hoga.bantuin.Adapter.EventAdapter;
import net.slc.hoga.bantuin.EventDetailActivity;
import net.slc.hoga.bantuin.Helper.Config;
import net.slc.hoga.bantuin.Helper.GPSTracker;
import net.slc.hoga.bantuin.Model.Event;
import net.slc.hoga.bantuin.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DiscoverFragment extends Fragment implements ChildEventListener, AdapterView.OnItemClickListener {
    RecyclerView.LayoutManager layoutManager;
    EventAdapter adapter;

    DatabaseReference eventDatabase;
    ArrayList<Event> events;
    ListView listView;
    Location loc1, loc2, loc3;
    GPSTracker gps;
    FrameLayout layoutError;
    SimpleDateFormat sdf = new SimpleDateFormat(Config.DATE_FORMAT, Locale.US);
    Date d1, d2;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_discover, container, false);
        layoutError = v.findViewById(R.id.noData);
        initializeComponents();
        listView = v.findViewById(R.id.list_view);
        eventDatabase.addChildEventListener(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        return v;
    }

    private void initializeComponents() {
        gps = new GPSTracker(getContext());

        loc1 = new Location("");
        loc2 = new Location("");
        loc3 = new Location("");

        layoutManager = new LinearLayoutManager(getContext());
        events = new ArrayList<>();
        adapter = new EventAdapter(getContext(), R.layout.card_event, events);
        adapter.setCurrentLatLng(gps.getLocation());
        eventDatabase = FirebaseDatabase.getInstance().getReference();
        eventDatabase = eventDatabase.child("events");
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getContext(), EventDetailActivity.class);
        intent.putExtra("key", events.get(i).getKey());
        startActivity(intent);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        try {
            ((ViewGroup) layoutError.getParent()).removeView(layoutError);
        } catch (Exception e) {
        }
        Event event = dataSnapshot.getValue(Event.class);
        try {
            d1 = sdf.parse(event.getDate());
            d2 = sdf.parse(sdf.format(Calendar.getInstance().getTime()));
            if (d1.compareTo(d2) < 0) {
                return;
            }
        } catch (Exception e) {
        }
        loc1.setLatitude(gps.getLatitude());
        loc1.setLongitude(gps.getLongitude());

        loc2.setLatitude(event.getLat());
        loc2.setLongitude(event.getLng());
        float distance = loc1.distanceTo(loc2);
        boolean inserted = false;
        for (int i = 0; i < adapter.getCount(); i++) {
            Event temp = adapter.getItem(i);
            loc3.setLongitude(temp.getLng());
            loc3.setLatitude(temp.getLat());

            if (loc1.distanceTo(loc3) > distance) {
                inserted = true;
                events.add(i, event);
                adapter.notifyDataSetChanged();
                break;
            }
        }

        if (!inserted) {
            events.add(event);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}