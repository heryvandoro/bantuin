package edu.bluejack17_1.bantuin.Fragment;

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

import edu.bluejack17_1.bantuin.Adapter.EventAdapter;
import edu.bluejack17_1.bantuin.EventDetailActivity;
import edu.bluejack17_1.bantuin.Model.Event;
import edu.bluejack17_1.bantuin.R;

import java.util.ArrayList;

public class EventPartFragment extends Fragment implements AdapterView.OnItemClickListener {
    RecyclerView.LayoutManager layoutManager;
    public EventAdapter adapter;
    public ArrayList<Event> events = new ArrayList<>();
    ListView listView;

    public EventPartFragment() {
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
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new EventAdapter(getContext(), R.layout.card_event, events);
        listView = v.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getContext(), EventDetailActivity.class);
        intent.putExtra("key", events.get(i).getKey());
        startActivity(intent);
    }
}