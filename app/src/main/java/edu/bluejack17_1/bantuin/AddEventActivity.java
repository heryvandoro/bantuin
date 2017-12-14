package edu.bluejack17_1.bantuin;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.bluejack17_1.bantuin.Helper.Config;
import edu.bluejack17_1.bantuin.Helper.DateParser;
import edu.bluejack17_1.bantuin.Helper.FilePath;
import edu.bluejack17_1.bantuin.Services.ImageService;
import edu.bluejack17_1.bantuin.Model.ActiveUser;
import edu.bluejack17_1.bantuin.Model.Category;
import edu.bluejack17_1.bantuin.Model.Event;
import edu.bluejack17_1.bantuin.Model.User;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddEventActivity extends MasterActivity implements ValueEventListener, View.OnClickListener, PlaceSelectionListener {
    Spinner spinnerCategories;
    ArrayAdapter<String> adapter;
    DatabaseReference database;
    EditText textDate, textTime, textTitle, textDescription;
    Calendar calendar;
    PlaceAutocompleteFragment autocompleteFragment;
    Button btnAdd;
    Place place;
    TextInputLayout layoutTitle, layoutDesc, layoutDate, layoutTime;

    ImageService service;
    List<String> pictures;
    List<Category> allCat;

    LinearLayout linearLayout;
    ProgressBar spinner;

    final long today = System.currentTimeMillis() - 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        spinner = findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
        linearLayout = findViewById(R.id.linearLayout);
        spinner = findViewById(R.id.progressBar);
        linearLayout.setAlpha((float) 0.2);
        initializeComponent();
    }

    public void initializeComponent() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add Event");
        actionBar.setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance().getReference().child("categories");
        database.addListenerForSingleValueEvent(this);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories = findViewById(R.id.spinnerCategories);
        spinnerCategories.setAdapter(adapter);

        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getMinimum(Calendar.MILLISECOND));

        textDate = findViewById(R.id.textDate);
        textDate.setOnClickListener(this);

        textTime = findViewById(R.id.textTime);
        textTime.setOnClickListener(this);

        textTitle = findViewById(R.id.textTitle);
        textDescription = findViewById(R.id.textDescription);

        allCat = new ArrayList<>();

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.placeFragment);

        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_COUNTRY)
                .setCountry("ID")
                .build();
        autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.setFilter(autocompleteFilter);

        btnAdd = findViewById(R.id.btnAddEvent);
        btnAdd.setOnClickListener(this);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        service = new Retrofit.Builder().baseUrl(Config.BASE_URL_PICTURE)
                .client(client).build().create(ImageService.class);

        pictures = new ArrayList<>();

        layoutTitle = findViewById(R.id.layoutTitle);
        layoutDesc = findViewById(R.id.layoutDesc);
        layoutTime = findViewById(R.id.textTimeLayout);
        layoutDate = findViewById(R.id.textDateLayout);
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
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
            allCat.add(category);
            adapter.add(category.getName());
            adapter.notifyDataSetChanged();
        }
        linearLayout.setAlpha((float) 1.0);
        spinner.setVisibility(View.GONE);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }



    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.textDate) {
            DatePickerDialog dpd;
            dpd = new DatePickerDialog(this, date, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            dpd.show();
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
        } else if (view.getId() == R.id.btnAddEvent) {
            layoutTitle.setErrorEnabled(false);
            layoutDesc.setErrorEnabled(false);
            layoutTime.setErrorEnabled(false);
            layoutDate.setErrorEnabled(false);

            SimpleDateFormat df = new SimpleDateFormat(Config.DATE_FORMAT);
            Calendar today = Calendar.getInstance();
            Calendar myDay = Calendar.getInstance();
            today.setTimeInMillis(System.currentTimeMillis());
            boolean isValidated = false;


            if (textTitle.getText().toString().isEmpty()) {
                layoutTitle.setError("Title must be filled");
                layoutTitle.setErrorEnabled(true);
            } else if (textDescription.getText().toString().isEmpty()) {
                layoutDesc.setError("Description must be filled");
                layoutDesc.setErrorEnabled(true);
            } else if (textDate.getText().toString().isEmpty()) {
                layoutDate.setError("Event date must be filled");
                layoutDate.setErrorEnabled(true);
            } else if (textTime.getText().toString().isEmpty()) {
                layoutTime.setError("Event time must be filled");
                layoutTime.setErrorEnabled(true);
            } else if (place == null) {
                Toast.makeText(this, "Event location must be filled", Toast.LENGTH_SHORT).show();
            }else{
                try {
                    Date date = df.parse(textDate.getText().toString());
                    myDay.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (!myDay.before(today))
                    isValidated = true;
                else
                    Toast.makeText(this, "Event Date cant be lower than today", Toast.LENGTH_SHORT).show();
            }
            if(isValidated) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, false);
                startActivityForResult(Intent.createChooser(intent, "Bantuin - Event Images"), 1);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            //two images
            if (data.getClipData() != null) {
                linearLayout.setAlpha((float) 0.2);
                spinner.setVisibility(View.VISIBLE);
                ClipData mClipData = data.getClipData();

                List<MultipartBody.Part> parts = new ArrayList();

                for (int i = 0; i < mClipData.getItemCount(); i++) {
                    File file = new File(FilePath.getPath(this, mClipData.getItemAt(i).getUri()));
                    MultipartBody.Part images =
                            MultipartBody.Part.createFormData("fileToUpload[" + i + "]", file.getName(),
                                    RequestBody.create(MediaType.parse("image/*"), file));
                    parts.add(images);
                }
                retrofit2.Call<okhttp3.ResponseBody> req = service.postImage(parts);
                req.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String res = response.body().string();
                            if (!res.contains("AMAN")) {
                                Toast.makeText(AddEventActivity.this, res, Toast.LENGTH_SHORT).show();
                            } else {
                                res = res.substring(5, res.length());
                                String[] temp = res.split("#");
                                for (String x : temp) {
                                    pictures.add(Config.BASE_URL_PICTURE + x);
                                }
                                saveEvent();
                            }
                            linearLayout.setAlpha((float) 1.0);
                            spinner.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            } else {
                Toast.makeText(this, "Please select at least 2 images!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPlaceSelected(Place place) {
        this.place = place;
        //Toast.makeText(this, "Loc: " + place.getAddress(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Status status) {

    }

    private void saveEvent() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("events");
        String key = db.push().getKey();

        try {
            Event event = new Event();
            event.setCategory(allCat.get(spinnerCategories.getSelectedItemPosition()));
            event.setDate(textDate.getText().toString());
            event.setTime(textTime.getText().toString());
            FirebaseUser tempUser = ActiveUser.getUser();
            event.setOrganizer(new User(tempUser.getDisplayName(),
                    tempUser.getEmail(), tempUser.getPhotoUrl().toString(),
                    tempUser.getUid()));
            event.setDescription(textDescription.getText().toString());
            event.setTitle(textTitle.getText().toString());
            event.setKey(key);
            event.setLat(place.getLatLng().latitude);
            event.setLng(place.getLatLng().longitude);
            event.setLocation(place.getAddress().toString());
            event.setPictures(pictures);
            db.child(key).setValue(event);
            Toast.makeText(this, "Success add event!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Something wrong!", Toast.LENGTH_SHORT).show();
        }
        super.onBackPressed();
    }
}