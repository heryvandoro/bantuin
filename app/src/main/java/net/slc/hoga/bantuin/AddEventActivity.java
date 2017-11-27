package net.slc.hoga.bantuin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.slc.hoga.bantuin.Helper.Config;
import net.slc.hoga.bantuin.Helper.DateParser;
import net.slc.hoga.bantuin.Model.Category;

import java.util.Calendar;

public class AddEventActivity extends MasterActivity implements ValueEventListener, View.OnClickListener {
    Spinner spinnerCategories;
    ArrayAdapter<String> adapter;
    DatabaseReference database;
    EditText textDate, textTime;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        initializeComponent();
    }

    @Override
    public void initializeComponent() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(actionBar.getTitle() + " - Add Event");
        actionBar.setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance().getReference().child("categories");
        database.addListenerForSingleValueEvent(this);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories = findViewById(R.id.spinnerCategories);
        spinnerCategories.setAdapter(adapter);

        calendar = Calendar.getInstance();
        textDate = findViewById(R.id.textDate);
        textDate.setOnClickListener(this);

        textTime = findViewById(R.id.textTime);
        textTime.setOnClickListener(this);
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            textDate.setText(DateParser.parse(calendar, Config.DATE_FORMAT));
        }
    };

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
            Category category = postSnapshot.getValue(Category.class);
            adapter.add(category.getName());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.textDate) {
            new DatePickerDialog(this, date, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        } else if (view.getId() == R.id.textTime) {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    textTime.setText(selectedHour + ":" + selectedMinute);
                }
            }, hour, minute, true);
            mTimePicker.show();
        }
    }
}