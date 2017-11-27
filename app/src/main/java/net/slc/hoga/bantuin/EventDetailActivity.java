package net.slc.hoga.bantuin;

import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import net.slc.hoga.bantuin.Model.Event;

import java.util.ArrayList;

public class EventDetailActivity extends MasterActivity implements ValueEventListener {

    ImageView imageView;
    TextView category,user,location,time;

    DatabaseReference database;
    public static Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        initializeComponent();
    }

    @Override
    public void initializeComponent() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(event.getTitle());
        actionBar.setDisplayHomeAsUpEnabled(true);

        imageView = findViewById(R.id.imageView);
        category = findViewById(R.id.category);
        user = findViewById(R.id.user);
        time = findViewById(R.id.time);
        location = findViewById(R.id.location);

        time.setText(makeString("Time",": "+event.getTime()));
        location.setText(makeString("Location",": "+event.getLocation()));

        database = FirebaseDatabase.getInstance().getReference();
        database.child("users").child(event.getOrganizer()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user.setText(makeString("Organizer",": "+dataSnapshot.getValue(String.class)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        database.child("categories").child(event.getCategory().toString()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                category.setText(makeString("Category",": "+dataSnapshot.getValue(String.class)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Picasso.with(this)
                .load(event.getPictures().get(0))
                .into(imageView);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    SpannableString makeString(String boldText, String normalText){

        SpannableString str = new SpannableString(boldText + normalText);
        str.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }
}
