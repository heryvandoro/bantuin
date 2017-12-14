package edu.bluejack17_1.bantuin.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahmadrosid.svgloader.SvgLoader;

import edu.bluejack17_1.bantuin.Model.Category;
import edu.bluejack17_1.bantuin.R;

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

        SvgLoader.pluck()
                .with((Activity) context)
                .setPlaceHolder(R.mipmap.ic_launcher, R.mipmap.ic_launcher)
                .load(categories.get(i).getIcon(), (ImageView) temp.findViewById(R.id.categoryIcon));
        return temp;
    }
}
