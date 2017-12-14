package edu.bluejack17_1.bantuin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import edu.bluejack17_1.bantuin.Helper.ImageRound;
import edu.bluejack17_1.bantuin.Model.User;
import edu.bluejack17_1.bantuin.R;

import java.util.List;

public class UserAdapter extends BaseAdapter {
    List<User> listUser;
    Context context;

    public UserAdapter(Context context, List<User> listUser) {
        this.context = context;
        this.listUser = listUser;
    }

    @Override
    public int getCount() {
        return listUser.size();
    }

    @Override
    public Object getItem(int i) {
        return listUser.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_user, null);
        ((TextView) v.findViewById(R.id.userName)).setText(listUser.get(i).getName());
        ImageView temp = v.findViewById(R.id.userPhoto);
        Picasso.with(context)
                .load(listUser.get(i).getPhoto())
                .transform(ImageRound.get(context, 60, false))
                .into(temp);
        return v;
    }
}
