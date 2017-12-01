package net.slc.hoga.bantuin.Fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.slc.hoga.bantuin.Adapter.TabAdapter;
import net.slc.hoga.bantuin.Helper.Config;
import net.slc.hoga.bantuin.Helper.CustomFirebaseListener;
import net.slc.hoga.bantuin.Model.ActiveUser;
import net.slc.hoga.bantuin.Model.Event;
import net.slc.hoga.bantuin.Model.User;
import net.slc.hoga.bantuin.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventFragment extends Fragment {
    DatabaseReference database;

    EventPartFragment partMyevents;
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
        initializeComponents();

        partMyevents = new EventPartFragment();
        partUpcoming = new EventPartFragment();
        partHistory = new EventPartFragment();

        TabAdapter adapter = new TabAdapter(getChildFragmentManager());
        adapter.addFragment(partMyevents, "My Events");
        adapter.addFragment(partUpcoming, "Upcoming");
        adapter.addFragment(partHistory, "History");

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
                    if(showed.contains(event.getKey())) continue;
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat(Config.DATE_FORMAT, Locale.US);
                        Date d1, d2;

                        if (event.getOrganizer().getUid().equals(ActiveUser.getUser().getUid())) {
                            partMyevents.getView().findViewById(R.id.noData).setVisibility(View.GONE);
                            partMyevents.events.add(event);
                            partMyevents.adapter.notifyDataSetChanged();
                            showed.add(event.getKey());
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
            }
        });
    }
}