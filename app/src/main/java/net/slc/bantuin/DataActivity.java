package net.slc.bantuin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DataActivity extends MasterActivity {

    private DatabaseReference mDatabase;
    private ArrayList<String> bookList = new ArrayList<>();
    private ListView listViewBook;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        initializeComponent();
    }

    public  void initializeComponent(){
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase = mDatabase.child("books");

        // Associate the teachers' list with the corresponding ListView
        listViewBook = (ListView) findViewById(R.id.listviewBooks);

        // Set the ArrayAdapter to the ListView
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bookList);
        listViewBook.setAdapter(arrayAdapter);

        // Attach a ChildEventListener to the teacher database, so we can retrieve the teacher entries
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // Get the value from the DataSnapshot and add it to the teachers' list
                String teacher = dataSnapshot.getValue(String.class);
                bookList.add(teacher);

                // Notify the ArrayAdapter that there was a change
                arrayAdapter.notifyDataSetChanged();
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
    }
}
