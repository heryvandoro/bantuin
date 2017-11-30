package net.slc.hoga.bantuin.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import net.slc.hoga.bantuin.R;

import java.util.List;

public class SliderAdapter extends PagerAdapter {
    LayoutInflater layoutInflater;
    private List<String> images;
    private Context context;

    public SliderAdapter(List<String> images, Context context) {
        this.images = images;
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = layoutInflater.from(context).inflate(R.layout.item_slider, container, false);
        Picasso.with(context)
                .load(images.get(position))
                .into((ImageView) view.findViewById(R.id.sliderImage));
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}