package edu.bluejack17_1.bantuin;

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

import edu.bluejack17_1.bantuin.Adapter.EventAdapter;
import edu.bluejack17_1.bantuin.Helper.CustomFirebaseListener;
import edu.bluejack17_1.bantuin.Model.Category;
import edu.bluejack17_1.bantuin.Model.Event;

import java.util.ArrayList;

public class CategoryDetailActivity extends MasterActivity implements ValueEventListener, AdapterView.OnItemClickListener {

    RecyclerView.LayoutManager layoutManager;
    EventAdapter adapter;

    DatabaseReference database;
    ArrayList<Event> events;
    ListView listView;
    String categoryKey;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        initializeComponent();
    }

    @Override
    public void initializeComponent() {
        categoryKey = getIntent().getStringExtra("key");
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        layoutManager = new LinearLayoutManager(this);
        events = new ArrayList<>();
        adapter = new EventAdapter(this, R.layout.card_event, events);
        database = FirebaseDatabase.getInstance().getReference();

        listView = findViewById(R.id.list_view);
        database.child("events").addListenerForSingleValueEvent(this);
        database.child("categories").child(categoryKey).addListenerForSingleValueEvent(new CustomFirebaseListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Category temp = dataSnapshot.getValue(Category.class);
                actionBar.setTitle(temp.getName());
            }
        });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("key", events.get(i).getKey());
        startActivity(intent);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
            Event event = postSnapshot.getValue(Event.class);
            if (event.getCategory().getKey().equals(categoryKey)) {
                findViewById(R.id.noData).setVisibility(View.GONE);
                events.add(event);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
