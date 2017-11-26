package net.slc.hoga.bantuin;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.slc.hoga.bantuin.Adapter.TabAdapter;
import net.slc.hoga.bantuin.Fragment.AccountFragment;
import net.slc.hoga.bantuin.Fragment.DiscoverFragment;
import net.slc.hoga.bantuin.Fragment.EventFragment;
import net.slc.hoga.bantuin.Fragment.HomeFragment;

public class HomeActivity extends MasterActivity implements TabLayout.OnTabSelectedListener{
    TabLayout tabLayout;
    ViewPager viewPager;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle menuToggle;

    private int[] tabIcons = {
            R.drawable.icon_home,
            R.drawable.icon_discover,
            R.drawable.icon_bookmark,
            R.drawable.icon_gears
    };

    private int[] tabIconsRed = {
            R.drawable.icon_home_red,
            R.drawable.icon_discover_red,
            R.drawable.icon_bookmark_red,
            R.drawable.icon_gears_red
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

        tabLayout.addOnTabSelectedListener(this);
        tabLayout.getTabAt(0).select();

        drawerLayout = findViewById(R.id.drawer);
        menuToggle= new ActionBarDrawerToggle(this,drawerLayout,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(menuToggle);
        menuToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(menuToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    private void setupTabIcons() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            LinearLayout temp = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            TextView textTab =  temp.findViewById(R.id.tabText);
            textTab.setText(tabTitles[i]);
            ImageView iconTab = temp.findViewById(R.id.tabIcon);
            iconTab.setBackgroundResource(tabIcons[i]);
            tabLayout.getTabAt(i).setCustomView(temp);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), tabTitles[0]);
        adapter.addFragment(new DiscoverFragment(), tabTitles[1]);
        adapter.addFragment(new EventFragment(), tabTitles[2]);
        adapter.addFragment(new AccountFragment(), tabTitles[3]);
        viewPager.setAdapter(adapter);
    }

    void setTabActive(TabLayout.Tab tab, boolean yes){
        View temp = tab.getCustomView();
        TextView textTab = temp.findViewById(R.id.tabText);
        ImageView iconTab = temp.findViewById(R.id.tabIcon);
        if(yes){
            textTab.setTextColor(getResources().getColor(R.color.primaryDarkColor));
            iconTab.setBackgroundResource(tabIconsRed[tab.getPosition()]);
        }else{
            textTab.setTextColor(getResources().getColor(R.color.secondaryTextColor));
            iconTab.setBackgroundResource(tabIcons[tab.getPosition()]);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        setTabActive(tab, true);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        setTabActive(tab, false);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        setTabActive(tab, true);
    }
}
