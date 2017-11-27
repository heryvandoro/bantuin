package net.slc.hoga.bantuin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.slc.hoga.bantuin.Adapter.TabAdapter;
import net.slc.hoga.bantuin.Fragment.FriendsFragment;
import net.slc.hoga.bantuin.Fragment.DiscoverFragment;
import net.slc.hoga.bantuin.Fragment.EventFragment;
import net.slc.hoga.bantuin.Fragment.HomeFragment;
import net.slc.hoga.bantuin.Model.ActiveUser;

public class HomeActivity extends MasterActivity implements TabLayout.OnTabSelectedListener,
        MenuItem.OnMenuItemClickListener,
        View.OnClickListener{
    TabLayout tabLayout;
    ViewPager viewPager;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle menuToggle;
    NavigationView navMenu;
    boolean dblClick = false;

    private int[] tabIcons = {
            R.drawable.icon_home,
            R.drawable.icon_discover,
            R.drawable.icon_bookmark,
            R.drawable.icon_happy
    };

    private int[] tabIconsRed = {
            R.drawable.icon_home_red,
            R.drawable.icon_discover_red,
            R.drawable.icon_bookmark_red,
            R.drawable.icon_happy_red
    };

    private String[] tabTitles = {"HOME", "DISCOVER", "EVENTS", "FRIENDS"};

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
        menuToggle= new ActionBarDrawerToggle(this,drawerLayout,R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(menuToggle);
        menuToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navMenu = findViewById(R.id.navigation_menu);
        ((TextView)navMenu.getHeaderView(0).findViewById(R.id.userName)).setText(ActiveUser.getUserDB().getName());
        ((TextView)navMenu.getHeaderView(0).findViewById(R.id.userEmail)).setText(ActiveUser.getUserDB().getEmail());
        navMenu.getHeaderView(0).setOnClickListener(this);
        Picasso.with(this)
                .load(ActiveUser.getUserDB().getPhoto())
                .into((ImageView) navMenu.getHeaderView(0).findViewById(R.id.userPhoto));

        fillMenu();
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
        adapter.addFragment(new FriendsFragment(), tabTitles[3]);
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

    private void fillMenu(){
        Menu mainMenu = navMenu.getMenu();
        mainMenu.add(0,0,Menu.NONE,"Add Event").setOnMenuItemClickListener(this);
        mainMenu.add(0,1,Menu.NONE,"My Account").setOnMenuItemClickListener(this);
        mainMenu.add(0,2,Menu.NONE,"Logout").setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        drawerLayout.closeDrawers();
        Intent i = null;
        switch(menuItem.getItemId()){
            case 0 :
                i = new Intent(this, AddEventActivity.class);
                break;
            case 1 :
                i = new Intent(this, AccountActivity.class);
                break;
            case 2 :
                ActiveUser.logout();
                i = new Intent(this, LoginActivity.class);
                break;
        }
        startActivity(i);
        if(menuItem.getItemId()==2) finish();
        return false;
    }

    @Override
    public void onBackPressed() {
        if (dblClick) {
            super.onBackPressed();
            return;
        }
        dblClick = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dblClick=false;
            }
        }, 2000);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.menuHeader){
            startActivity(new Intent(this, AccountActivity.class));
        }
    }
}
