package edu.bluejack17_1.bantuin.Fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.bluejack17_1.bantuin.Adapter.TabAdapter;
import edu.bluejack17_1.bantuin.Helper.Config;
import edu.bluejack17_1.bantuin.Model.ActiveUser;
import edu.bluejack17_1.bantuin.Model.Event;
import edu.bluejack17_1.bantuin.Model.User;
import edu.bluejack17_1.bantuin.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventFragment extends Fragment implements ChildEventListener {
    DatabaseReference database;

    EventPartFragment partMyEvents;
    EventPartFragment partUpcoming;
    EventPartFragment partHistory;
    ArrayList<String> showed = new ArrayList<>();

    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        partMyEvents = new EventPartFragment();
        partUpcoming = new EventPartFragment();
        partHistory = new EventPartFragment();

        database = FirebaseDatabase.getInstance().getReference().child("events");
        database.addChildEventListener(this);

        TabAdapter adapter = new TabAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(partMyEvents, "My Events");
        adapter.addFragment(partUpcoming, "Upcoming");
        adapter.addFragment(partHistory, "History");

        ViewPager viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = view.findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Event event = dataSnapshot.getValue(Event.class);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(Config.DATE_FORMAT, Locale.US);
            Date d1, d2;

            if (event.getOrganizer().getUid().equals(ActiveUser.getUser().getUid())) {
                partMyEvents.getView().findViewById(R.id.noData).setVisibility(View.GONE);
                partMyEvents.events.add(event);
                partMyEvents.adapter.notifyDataSetChanged();
                showed.add(event.getKey());
                return;
            }

            ArrayList<User> volun = new ArrayList<>(event.getVolunteers().values());

            for(User x : volun){
                if(x.getUid().equals(ActiveUser.getUser().getUid())){
                    d1 = sdf.parse(event.getDate());
                    d2 = sdf.parse(sdf.format(Calendar.getInstance().getTime()));
                    if (d1.compareTo(d2) >= 0) {
                        partUpcoming.getView().findViewById(R.id.noData).setVisibility(View.GONE);
                        partUpcoming.events.add(event);
                        partUpcoming.adapter.notifyDataSetChanged();
                    } else if (d1.compareTo(d2) < 0) {
                        partHistory.getView().findViewById(R.id.noData).setVisibility(View.GONE);
                        partHistory.events.add(event);
                        partHistory.adapter.notifyDataSetChanged();
                    }
                    showed.add(event.getKey());
                    break;
                }
            }

        } catch (Exception e) {
            Log.w("error", e.getMessage().toString());
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