package net.slc.hoga.bantuin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.slc.hoga.bantuin.Fragment.DiscoverFragment;
import net.slc.hoga.bantuin.Fragment.EventFragment;
import net.slc.hoga.bantuin.Fragment.FriendsFragment;
import net.slc.hoga.bantuin.Fragment.HomeFragment;
import net.slc.hoga.bantuin.Helper.BottomNavigationViewHelper;
import net.slc.hoga.bantuin.Helper.ConnectivityChecker;
import net.slc.hoga.bantuin.Helper.ConnectivityReceiver;
import net.slc.hoga.bantuin.Helper.GPSTracker;
import net.slc.hoga.bantuin.Model.ActiveUser;

public class HomeActivity extends MasterActivity implements
        MenuItem.OnMenuItemClickListener,
        View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle menuToggle;
    NavigationView navMenu;
    boolean dblClick = false;
    BottomNavigationView bottomNavigationView;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    GPSTracker gps;

    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        gps = new GPSTracker(this);
        initializeComponent();
        checkConnection();
    }

    @Override
    public void initializeComponent() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        drawerLayout = findViewById(R.id.drawer);
        menuToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(menuToggle);
        menuToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navMenu = findViewById(R.id.navigation_menu);
        ((TextView) navMenu.getHeaderView(0).findViewById(R.id.userName)).setText(ActiveUser.getUser().getDisplayName());
        ((TextView) navMenu.getHeaderView(0).findViewById(R.id.userEmail)).setText(ActiveUser.getUser().getEmail());
        navMenu.getHeaderView(0).setOnClickListener(this);
        Picasso.with(this)
                .load(ActiveUser.getUser().getPhotoUrl())
                .into((ImageView) navMenu.getHeaderView(0).findViewById(R.id.userPhoto));

        fillMenu();

        fragmentManager = getSupportFragmentManager();
        loadFragment(0);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        loadFragment(0);
                        return true;
                    case R.id.action_discover:
                        loadFragment(1);
                        return true;
                    case R.id.action_events:
                        loadFragment(2);
                        return true;
                    case R.id.action_friends:
                        loadFragment(3);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (menuToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    private void fillMenu() {
        Menu mainMenu = navMenu.getMenu();
        mainMenu.add(0, 0, Menu.NONE, "Add Event").setOnMenuItemClickListener(this);
        mainMenu.add(0, 1, Menu.NONE, "My Account").setOnMenuItemClickListener(this);
        mainMenu.add(0, 2, Menu.NONE, "Logout").setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        drawerLayout.closeDrawers();
        Intent i = null;
        switch (menuItem.getItemId()) {
            case 0:
                i = new Intent(this, AddEventActivity.class);
                break;
            case 1:
                i = new Intent(this, AccountActivity.class);
                break;
            case 2:
                ActiveUser.logout();
                i = new Intent(this, LoginActivity.class);
                break;
        }
        startActivity(i);
        if (menuItem.getItemId() == 2) finish();
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
                dblClick = false;
            }
        }, 2000);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.menuHeader) {
            startActivity(new Intent(this, AccountActivity.class));
        }
    }

    private void loadFragment(int index) {
        fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = null;
        switch (index) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new DiscoverFragment();
                break;
            case 2:
                fragment = new EventFragment();
                break;
            case 3:
                fragment = new FriendsFragment();
                break;
        }
        fragmentTransaction.replace(R.id.target_fragment, fragment).commit();
    }

    private void checkConnection() {
        showSnack(ConnectivityReceiver.isConnected());
    }

    private void showSnack(boolean isConnected) {
        if (!isConnected) {
            String message = "Check your internet connection :)";
            snackbar = Snackbar
                    .make(findViewById(R.id.drawer), message, Snackbar.LENGTH_INDEFINITE);

            snackbar.setAction("Dismiss", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });

            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityChecker.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
}