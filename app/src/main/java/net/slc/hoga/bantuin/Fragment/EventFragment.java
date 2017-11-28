package net.slc.hoga.bantuin.Fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.slc.hoga.bantuin.Adapter.TabAdapter;
import net.slc.hoga.bantuin.Helper.Config;
import net.slc.hoga.bantuin.Helper.CustomFirebaseListener;
import net.slc.hoga.bantuin.Model.ActiveUser;
import net.slc.hoga.bantuin.Model.Event;
import net.slc.hoga.bantuin.R;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventFragment extends Fragment {
    DatabaseReference database;
    ArrayList<Event> myEvents;
    ArrayList<Event> upcoming;
    ArrayList<Event> history;

    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeComponents();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        TabAdapter adapter = new TabAdapter(getFragmentManager());
        adapter.addFragment(new EventPartFragment(), "My Events");
        adapter.addFragment(new EventPartFragment(), "Upcoming");
        adapter.addFragment(new EventPartFragment(), "History");

        ViewPager viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = view.findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    private void initializeComponents() {
        database = FirebaseDatabase.getInstance().getReference().child("events");
        database.addValueEventListener(new CustomFirebaseListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Event event = postSnapshot.getValue(Event.class);
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat(Config.DATE_FORMAT, Locale.US);
                        Date d1, d2;

                        if (event.getOrganizer().getUid().equals(ActiveUser.getUser().getUid())) {
                            myEvents.add(event);
                        }

                        if (event.getVolunteers().get(ActiveUser.getUser().getUid()) != null) {
                            d1 = sdf.parse(event.getDate());
                            d2 = sdf.parse(sdf.format(Calendar.getInstance().getTime()));
                            //if(d1.compareTo(d2)>0)
                        }
                    } catch (Exception e) {
                        Log.w("error", e.getMessage().toString());
                    }
                }
            }
        });
    }
}