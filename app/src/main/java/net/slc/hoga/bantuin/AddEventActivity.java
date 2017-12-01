package net.slc.hoga.bantuin;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.slc.hoga.bantuin.Helper.Config;
import net.slc.hoga.bantuin.Helper.DateParser;
import net.slc.hoga.bantuin.Helper.FilePath;
import net.slc.hoga.bantuin.Helper.ImageService;
import net.slc.hoga.bantuin.Model.Category;

import java.io.File;
import java.util.Calendar;

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
    EditText textDate, textTime;
    Calendar calendar;
    PlaceAutocompleteFragment autocompleteFragment;
    Button btnAdd;

    ImageService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        initializeComponent();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        service = new Retrofit.Builder().baseUrl(Config.BASE_URL_PICTURE)
                .client(client).build().create(ImageService.class);
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
        textDate = findViewById(R.id.textDate);
        textDate.setOnClickListener(this);

        textTime = findViewById(R.id.textTime);
        textTime.setOnClickListener(this);

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
        } else if (view.getId() == R.id.btnAddEvent) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            photoPickerIntent.setType("image/*");
            photoPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, false);
            startActivityForResult(Intent.createChooser(photoPickerIntent, "Complete Action Using"), 11);
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11) {
            if (resultCode == Activity.RESULT_OK) {
                File file = new File(FilePath.getPath(this, data.getData()));

                MultipartBody.Part images =
                MultipartBody.Part.createFormData("fileToUpload", file.getName(),
                        RequestBody.create(MediaType.parse("image/*"), file));

                retrofit2.Call<okhttp3.ResponseBody> req = service.postImage(images);
                req.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.w("result MK", response.body().toString());
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        }
    }

    @Override
    public void onPlaceSelected(Place place) {
        Toast.makeText(this, "Loc: " + place.getAddress(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Status status) {

    }
}