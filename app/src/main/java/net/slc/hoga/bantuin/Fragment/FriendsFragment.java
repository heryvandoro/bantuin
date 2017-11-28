package net.slc.hoga.bantuin.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.slc.hoga.bantuin.Adapter.UserAdapter;
import net.slc.hoga.bantuin.EventDetailActivity;
import net.slc.hoga.bantuin.Helper.CustomFirebaseListener;
import net.slc.hoga.bantuin.Model.ActiveUser;
import net.slc.hoga.bantuin.Model.User;
import net.slc.hoga.bantuin.R;
import net.slc.hoga.bantuin.UserDetailActivity;

import java.util.ArrayList;

public class FriendsFragment extends Fragment implements AdapterView.OnItemClickListener {
    ListView listView;
    DatabaseReference database;
    UserAdapter adapter;
    ArrayList<User> friends;

    public FriendsFragment() {
        // Required empty public constructor
    }

    private void initializeComponents() {
        friends = new ArrayList<>();
        adapter = new UserAdapter(getContext(), friends);

        database = FirebaseDatabase.getInstance()
                .getReference();

        database.child("friends")
                .child(ActiveUser.getUser().getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String uid = dataSnapshot.getKey();
                        database.child("users").child(uid).addListenerForSingleValueEvent(new CustomFirebaseListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User temp = dataSnapshot.getValue(User.class);
                                friends.add(temp);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View temp = inflater.inflate(R.layout.fragment_friends, container, false);
        listView = temp.findViewById(R.id.listFriends);
        initializeComponents();
        return temp;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getContext(), UserDetailActivity.class);
        intent.putExtra("uid", friends.get(i).getUid());
        startActivity(intent);
    }
}