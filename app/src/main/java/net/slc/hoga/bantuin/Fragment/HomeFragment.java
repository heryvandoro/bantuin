package net.slc.hoga.bantuin.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.slc.hoga.bantuin.Adapter.CategoryAdapter;
import net.slc.hoga.bantuin.Adapter.SliderAdapter;
import net.slc.hoga.bantuin.CategoryDetailActivity;
import net.slc.hoga.bantuin.Helper.CustomFirebaseListener;
import net.slc.hoga.bantuin.Helper.CustomGridView;
import net.slc.hoga.bantuin.Model.Category;
import net.slc.hoga.bantuin.Model.Event;
import net.slc.hoga.bantuin.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener, ViewPager.OnPageChangeListener {

    CustomGridView gridView;
    RecyclerView.LayoutManager layoutManager;
    CategoryAdapter adapter;

    DatabaseReference database;
    ArrayList<Category> categories;

    ViewPager viewPager;
    LinearLayout dotsLayout;

    TextView[] dots;
    List<String> images;
    SliderAdapter sliderAdapter;
    Handler handler;
    Runnable update;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        gridView = v.findViewById(R.id.grid_view);
        viewPager = v.findViewById(R.id.view_pager);
        dotsLayout = v.findViewById(R.id.layoutDots);

        initializeComponents();

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);

        return v;
    }

    private void initDots() {
        dots = new TextView[images.size()];
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(getContext());
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.dot_disabled));
            dotsLayout.addView(dots[i]);
        }
    }

    private void setActiveDots(int currentPage) {
        for (int i = 0; i < dots.length; i++)
            dots[i].setTextColor(getResources().getColor(R.color.dot_disabled));
            dots[currentPage].setTextColor(getResources().getColor(R.color.dot_enabled));
    }

    private void initializeComponents() {
        images = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getContext());
        categories = new ArrayList<>();
        adapter = new CategoryAdapter(categories, getContext());
        database = FirebaseDatabase.getInstance().getReference();
        sliderAdapter = new SliderAdapter(images, getContext());
        database.child("categories").addListenerForSingleValueEvent(new CustomFirebaseListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Category category = postSnapshot.getValue(Category.class);
                    categories.add(category);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        database.child("slider").addListenerForSingleValueEvent(new CustomFirebaseListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String image = postSnapshot.getValue(String.class);
                    images.add(image);
                    sliderAdapter.notifyDataSetChanged();
                }
                Log.i("asdasd","Size: "+images.size());

                viewPager.setAdapter(sliderAdapter);
                viewPager.addOnPageChangeListener(HomeFragment.this);
                initDots();
                setActiveDots(0);
                initHandler();
            }
        });
    }

    void initHandler() {
        handler = new Handler();
        update = new Runnable() {
            public void run() {
                int curr = viewPager.getCurrentItem();
                if (curr == 3 - 1)
                    curr = 0;
                else
                    curr++;
                viewPager.setCurrentItem(curr, true);
            }
        };
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 50, 3000);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this.getContext(), CategoryDetailActivity.class);
        intent.putExtra("key", categories.get(i).getKey());
        startActivity(intent);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        setActiveDots(position);
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}