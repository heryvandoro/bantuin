package net.slc.hoga.bantuin.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.slc.hoga.bantuin.Adapter.EventAdapter;
import net.slc.hoga.bantuin.EventDetailActivity;
import net.slc.hoga.bantuin.Model.Event;
import net.slc.hoga.bantuin.R;

import java.util.ArrayList;

public class DiscoverFragment extends Fragment implements ValueEventListener, AdapterView.OnItemClickListener {
    RecyclerView.LayoutManager layoutManager;
    EventAdapter adapter;

    DatabaseReference eventDatabase;
    ArrayList<Event> events;
    ListView listView;
    public DiscoverFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_discover, container, false);
        initializeComponents();
        listView = v.findViewById(R.id.list_view);
        eventDatabase.addListenerForSingleValueEvent(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        return v;
    }

    private void initializeComponents(){
        layoutManager = new LinearLayoutManager(getContext());
        events = new ArrayList<>();
        adapter = new EventAdapter(events,getContext());
        eventDatabase = FirebaseDatabase.getInstance().getReference();
        eventDatabase = eventDatabase.child("events");
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
            Event event = postSnapshot.getValue(Event.class);
            events.add(event);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getContext(), EventDetailActivity.class);
        intent.putExtra("key", events.get(i).getKey());
        startActivity(intent);
    }
}