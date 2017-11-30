package net.slc.hoga.bantuin.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
import net.slc.hoga.bantuin.Model.Category;
import net.slc.hoga.bantuin.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class HomeFragment extends Fragment implements ValueEventListener, AdapterView.OnItemClickListener, ViewPager.OnPageChangeListener{

    GridView gridView;
    RecyclerView.LayoutManager layoutManager;
    CategoryAdapter adapter;

    DatabaseReference categoryDatabase;
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
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
            Category category = postSnapshot.getValue(Category.class);
            categories.add(category);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        gridView = v.findViewById(R.id.grid_view);
        viewPager = v.findViewById(R.id.view_pager);
        dotsLayout = v.findViewById(R.id.layoutDots);

        initializeComponents();
        images.add("https://www.thelocal.de/userdata/images/article/fa6fd5014ccbd8f4392f716473ab6ff354f871505d9128820bbb0461cce1d645.jpg");
        images.add("https://vignette.wikia.nocookie.net/wingsoffire/images/5/54/Panda.jpeg/revision/latest?cb=20170205005103");
        images.add("http://www.thechinawatch.com/wp-content/uploads/2012/03/11113.jpg");

        sliderAdapter = new SliderAdapter(images, getContext());
        viewPager.setAdapter(sliderAdapter);
        viewPager.addOnPageChangeListener(this);

        categoryDatabase.addListenerForSingleValueEvent(this);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
        addBottomDots(viewPager.getCurrentItem());
        return v;
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[images.size()];

        int colorsActive = getResources().getColor(R.color.dot_light_screen1);
        int colorsInactive = getResources().getColor(R.color.dot_dark_screen1);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(getContext());
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive);
    }

    private void initializeComponents() {
        images = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getContext());
        categories = new ArrayList<>();
        adapter = new CategoryAdapter(categories, getContext());
        categoryDatabase = FirebaseDatabase.getInstance().getReference();
        categoryDatabase = categoryDatabase.child("categories");
        initHandler();
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
        addBottomDots(position);
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}