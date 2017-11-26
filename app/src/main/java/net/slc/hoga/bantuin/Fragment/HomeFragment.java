package net.slc.hoga.bantuin.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.slc.hoga.bantuin.Adapter.RecyclerAdapter;
import net.slc.hoga.bantuin.Model.Category;
import net.slc.hoga.bantuin.R;

import java.util.ArrayList;


public class HomeFragment extends Fragment implements ValueEventListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerAdapter adapter;

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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = v.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        categories = new ArrayList<>();
        adapter = new RecyclerAdapter(categories,getContext());

        categoryDatabase = FirebaseDatabase.getInstance().getReference();
        categoryDatabase = categoryDatabase.child("categories");
        recyclerView.setAdapter(adapter);

        categoryDatabase.addListenerForSingleValueEvent(this);

        return v;
    }


}
