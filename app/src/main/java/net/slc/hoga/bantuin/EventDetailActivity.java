package net.slc.hoga.bantuin;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EventDetailActivity extends MasterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        initializeComponent();
    }

    @Override
    public void initializeComponent() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(actionBar.getTitle()+" - Event Name");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
