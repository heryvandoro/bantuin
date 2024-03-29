package edu.bluejack17_1.bantuin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import edu.bluejack17_1.bantuin.Model.Event;
import edu.bluejack17_1.bantuin.R;

import java.util.ArrayList;

public class EventSmallAdapter  extends BaseAdapter{

    private Context context;
    private ArrayList<Event> events;
    public EventSmallAdapter(ArrayList<Event> events, Context context){
        this.events = events;
        this.context = context;
    }
    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int i) {
        return events.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View temp = LayoutInflater.from(context).inflate(R.layout.item_event, null);
        ((TextView)temp.findViewById(R.id.eventTitle)).setText(events.get(i).getTitle());
        ((TextView)temp.findViewById(R.id.eventDetail)).setText(events.get(i).getDate()+" at "+events.get(i).getLocation());
        return temp;
    }
}