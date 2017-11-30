package net.slc.hoga.bantuin.Adapter;

import android.content.Context;
import android.location.Location;
import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.slc.hoga.bantuin.Model.Event;
import net.slc.hoga.bantuin.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class EventAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Event> events;
    private boolean showDistance = false;
    private Location loc;
    private DecimalFormatSymbols otherSymbols;
    private DecimalFormat df;

    public EventAdapter(ArrayList<Event> events, Context context) {
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
    public Object getItem(int i) {
        return events.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View temp = LayoutInflater.from(context).inflate(R.layout.card_event, null);
        ((TextView) temp.findViewById(R.id.eventTitle)).setText(events.get(i).getTitle());
        ((TextView) temp.findViewById(R.id.eventDescription)).setText(events.get(i).getDescription());
        if (showDistance) {
            Location locTemp = new Location("");
            locTemp.setLatitude(events.get(i).getLat());
            locTemp.setLongitude(events.get(i).getLng());

            float distance = locTemp.distanceTo(loc) / 1000;
            ((TextView) temp.findViewById(R.id.eventDistance)).setText(df.format(distance) + " KM");
        }else{
            temp.findViewById(R.id.eventDistance).setVisibility(View.GONE);
        }
        Picasso.with(context)
                .load(events.get(i).getPictures().get(0)).placeholder(context.getResources().getDrawable(R.drawable.load_event))
                .into((ImageView) temp.findViewById(R.id.eventPhoto));
        return temp;
    }
}
