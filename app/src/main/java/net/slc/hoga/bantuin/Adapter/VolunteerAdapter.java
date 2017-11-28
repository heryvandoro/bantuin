package net.slc.hoga.bantuin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.slc.hoga.bantuin.Model.User;
import net.slc.hoga.bantuin.R;

import java.util.List;

public class VolunteerAdapter extends BaseAdapter{
    List<User> listVolunteers;
    Context context;

    public VolunteerAdapter(Context context, List<User> listVolunteers) {
        this.context = context;
        this.listVolunteers = listVolunteers;
    }

    @Override
    public int getCount() {
        return listVolunteers.size();
    }

    @Override
    public Object getItem(int i) {
        return listVolunteers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_volunteer, null);
        ((TextView)v.findViewById(R.id.volunteerName)).setText(listVolunteers.get(i).getName());
        Picasso.with(context)
                .load(listVolunteers.get(i).getPhoto())
                .into((ImageView) v.findViewById(R.id.volunteerPhoto));
        return v;
    }
}
