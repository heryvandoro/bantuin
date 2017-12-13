package net.slc.hoga.bantuin.Adapter;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.slc.hoga.bantuin.Model.Event;
import net.slc.hoga.bantuin.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class EventAdapter extends ArrayAdapter<Event> {
    private Context context;
    private ArrayList<Event> events;
    private boolean showDistance = false;
    private Location loc;
    private DecimalFormatSymbols otherSymbols;
    private DecimalFormat df;

    public EventAdapter(Context context, int res, ArrayList<Event> events) {
        super(context, res, events);
        this.events = events;
        this.context = context;
    }

    public void setCurrentLatLng(Location loc) {
        this.loc = loc;
        showDistance = true;
        otherSymbols = DecimalFormatSymbols.getInstance();
        otherSymbols.setDecimalSeparator(',');
        otherSymbols.setGroupingSeparator('.');
        df = new DecimalFormat("###,###.##", otherSymbols);
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Event getItem(int i) {
        return events.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.card_event, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.eventTitle)).setText(events.get(position).getTitle());
        ((TextView) convertView.findViewById(R.id.eventDescription)).setText(events.get(position).getDescription());
        if (showDistance) {
            Location locTemp = new Location("");
            locTemp.setLatitude(events.get(position).getLat());
            locTemp.setLongitude(events.get(position).getLng());

            float distance = locTemp.distanceTo(loc) / 1000;
            ((TextView) convertView.findViewById(R.id.eventDistance)).setText(df.format(distance) + " KM");
        } else {
            convertView.findViewById(R.id.eventDistance).setVisibility(View.GONE);
        }
        Picasso.with(context)
                .load(events.get(position).getPictures().get(0)).placeholder(context.getResources().getDrawable(R.drawable.load_event))
                .into((ImageView) convertView.findViewById(R.id.eventPhoto));
        return convertView;
    }
}
