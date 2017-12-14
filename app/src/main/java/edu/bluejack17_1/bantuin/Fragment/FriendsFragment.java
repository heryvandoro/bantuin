package edu.bluejack17_1.bantuin.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.bluejack17_1.bantuin.Adapter.UserAdapter;
import edu.bluejack17_1.bantuin.Helper.CustomFirebaseListener;
import edu.bluejack17_1.bantuin.Model.ActiveUser;
import edu.bluejack17_1.bantuin.Model.User;
import edu.bluejack17_1.bantuin.R;
import edu.bluejack17_1.bantuin.UserDetailActivity;

import java.util.ArrayList;

public class FriendsFragment extends Fragment implements AdapterView.OnItemClickListener {
    ListView listView;
    DatabaseReference database;
    UserAdapter adapter;
    ArrayList<User> friends;

    FrameLayout layoutError;

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
                                try{
                                    ((ViewGroup) layoutError.getParent()).removeView(layoutError);
                                }catch (Exception e){}
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
        layoutError = temp.findViewById(R.id.noData);
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