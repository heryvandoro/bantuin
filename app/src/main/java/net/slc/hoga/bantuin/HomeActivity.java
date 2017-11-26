package net.slc.hoga.bantuin;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.slc.hoga.bantuin.Adapter.ViewPagerAdapter;
import net.slc.hoga.bantuin.Fragment.AccountFragment;
import net.slc.hoga.bantuin.Fragment.DiscoverFragment;
import net.slc.hoga.bantuin.Fragment.EventFragment;
import net.slc.hoga.bantuin.Fragment.HomeFragment;

public class HomeActivity extends MasterActivity {
    TabLayout tabLayout;
    ViewPager viewPager;

    private int[] tabIcons = {
            R.drawable.icon_home,
            R.drawable.icon_discover,
            R.drawable.icon_bookmark,
            R.drawable.icon_gears
    };

    private String[] tabTitles = {"HOME", "DISCOVER", "MY EVENTS", "ACCOUNT"};

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
        setupTabIcons();
    }

    private void setupTabIcons() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            LinearLayout temp = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            TextView a =  temp.findViewById(R.id.tabText);
            a.setText(tabTitles[i]);
            ImageView b = temp.findViewById(R.id.tabIcon);
            b.setBackgroundResource(tabIcons[i]);
            tabLayout.getTabAt(i).setCustomView(temp);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), tabTitles[0]);
        adapter.addFragment(new DiscoverFragment(), tabTitles[1]);
        adapter.addFragment(new EventFragment(), tabTitles[2]);
        adapter.addFragment(new AccountFragment(), tabTitles[3]);
        viewPager.setAdapter(adapter);
    }
}
