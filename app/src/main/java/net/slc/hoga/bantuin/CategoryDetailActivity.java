package net.slc.hoga.bantuin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.slc.hoga.bantuin.Adapter.EventAdapter;
import net.slc.hoga.bantuin.Model.Event;

import java.util.ArrayList;

public class CategoryDetailActivity extends MasterActivity implements ValueEventListener, AdapterView.OnItemClickListener{

    RecyclerView.LayoutManager layoutManager;
    EventAdapter adapter;

    DatabaseReference eventDatabase;
    ArrayList<Event> events;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        initializeComponent();
    }

    @Override
    public void initializeComponent() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getIntent().getStringExtra("category_name"));
        actionBar.setDisplayHomeAsUpEnabled(true);

        layoutManager = new LinearLayoutManager(this);
        events = new ArrayList<>();
        adapter = new EventAdapter(events,this);
        eventDatabase = FirebaseDatabase.getInstance().getReference();
        eventDatabase = eventDatabase.child("events");

        listView = findViewById(R.id.list_view);
        eventDatabase.addListenerForSingleValueEvent(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, EventDetailActivity.class);
        //EventDetailActivity.event = events.get(i);
        startActivity(intent);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
            Event event = postSnapshot.getValue(Event.class);
            if(event.getCategory().toString().equals(getIntent().getStringExtra("category_position"))) {
                events.add(event);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
