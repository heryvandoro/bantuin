package net.slc.hoga.bantuin.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.slc.hoga.bantuin.Adapter.TabAdapter;
import net.slc.hoga.bantuin.R;

public class EventFragment extends Fragment{

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

//        TabAdapter adapter = new TabAdapter(getFragmentManager().getPrimaryNavigationFragment());
//        adapter.addFragment(new DiscoverFragment(), "My Events");
//        adapter.addFragment(new DiscoverFragment(), "Upcoming");
//        adapter.addFragment(new DiscoverFragment(), "History");
//
//        ViewPager viewPager = view.findViewById(R.id.viewpager);
//        viewPager.setAdapter(adapter);
//
//        TabLayout tabLayout = view.findViewById(R.id.tab);
//        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

}
