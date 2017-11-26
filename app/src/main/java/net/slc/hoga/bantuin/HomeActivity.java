package net.slc.hoga.bantuin;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import net.slc.hoga.bantuin.Adapter.ViewPagerAdapter;
import net.slc.hoga.bantuin.Fragment.AccountFragment;
import net.slc.hoga.bantuin.Fragment.DiscoverFragment;
import net.slc.hoga.bantuin.Fragment.EventFragment;
import net.slc.hoga.bantuin.Fragment.HomeFragment;

public class HomeActivity extends MasterActivity {
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeComponent();
    }

    @Override
    public void initializeComponent() {
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "Home");
        adapter.addFragment(new DiscoverFragment(), "Discover");
        adapter.addFragment(new EventFragment(), "My Events");
        adapter.addFragment(new AccountFragment(), "Account");
        viewPager.setAdapter(adapter);
    }
}
