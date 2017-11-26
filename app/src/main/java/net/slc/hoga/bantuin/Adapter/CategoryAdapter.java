package net.slc.hoga.bantuin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.slc.hoga.bantuin.Model.Category;
import net.slc.hoga.bantuin.R;

import java.util.ArrayList;

public class CategoryAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Category> categories;
    public CategoryAdapter(ArrayList<Category> categories, Context context){
        this.categories = categories;
        this.context = context;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int i) {
        return categories.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View temp = LayoutInflater.from(context).inflate(R.layout.card_category, null);
        ((TextView)temp.findViewById(R.id.categoryName)).setText(categories.get(i).getName());
        Picasso.with(context)
                .load(categories.get(i).getIcon())
                .resize(100, 100)
                .into((ImageView) temp.findViewById(R.id.categoryIcon));
        return temp;
    }
}
