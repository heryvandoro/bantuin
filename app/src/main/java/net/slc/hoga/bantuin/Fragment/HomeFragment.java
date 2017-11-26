package net.slc.hoga.bantuin.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.slc.hoga.bantuin.Adapter.CategoryAdapter;
import net.slc.hoga.bantuin.Model.Category;
import net.slc.hoga.bantuin.R;

import java.util.ArrayList;


public class HomeFragment extends Fragment implements ValueEventListener {

    GridView gridView;
    RecyclerView.LayoutManager layoutManager;
    CategoryAdapter adapter;

    DatabaseReference categoryDatabase;
    ArrayList<Category> categories;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
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
        initializeComponents();
        categoryDatabase.addListenerForSingleValueEvent(this);
        gridView.setAdapter(adapter);
        return v;
    }

    private void initializeComponents(){
        layoutManager = new LinearLayoutManager(getContext());
        categories = new ArrayList<>();
        adapter = new CategoryAdapter(categories,getContext());
        categoryDatabase = FirebaseDatabase.getInstance().getReference();
        categoryDatabase = categoryDatabase.child("categories");
    }
}
