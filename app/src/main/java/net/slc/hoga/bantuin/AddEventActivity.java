package net.slc.hoga.bantuin;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

public class AddEventActivity extends MasterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        initializeComponent();
    }

    @Override
    public void initializeComponent() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(actionBar.getTitle()+" - Add Event");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
