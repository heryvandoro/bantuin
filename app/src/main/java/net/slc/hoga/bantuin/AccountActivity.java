package net.slc.hoga.bantuin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import net.slc.hoga.bantuin.Helper.Config;
import net.slc.hoga.bantuin.Helper.CustomFirebaseListener;
import net.slc.hoga.bantuin.Helper.FilePath;
import net.slc.hoga.bantuin.Helper.ImageRound;
import net.slc.hoga.bantuin.Helper.ImageService;
import net.slc.hoga.bantuin.Model.ActiveUser;
import net.slc.hoga.bantuin.Model.Event;
import net.slc.hoga.bantuin.Model.User;

import java.io.File;

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

public class AccountActivity extends MasterActivity implements View.OnClickListener {

    ImageView userPhoto;
    RoundedImageView editIcon;
    ImageService service;
    OkHttpClient client;

    LinearLayout linearLayout;
    ProgressBar spinner;

    TextView txtName, txtEmail;
    LinearLayout logoutBar;

    DatabaseReference databaseEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        initializeComponent();
    }

    @Override
    public void initializeComponent() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("My Account");
        actionBar.setDisplayHomeAsUpEnabled(true);

        spinner = findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        linearLayout = findViewById(R.id.linearLayout);

        userPhoto = findViewById(R.id.userPhoto);
        editIcon = findViewById(R.id.editIcon);

        txtName = findViewById(R.id.userName);
        txtName.setText(ActiveUser.getUser().getDisplayName());

        txtEmail = findViewById(R.id.userEmail);
        txtEmail.setText(ActiveUser.getUser().getEmail());

        logoutBar = findViewById(R.id.logout);
        logoutBar.setOnClickListener(this);

        databaseEvent = FirebaseDatabase.getInstance().getReference().child("events");

        applyPhoto();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        service = new Retrofit.Builder().baseUrl(Config.BASE_URL_PICTURE)
                .client(client).build().create(ImageService.class);

        editIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.editIcon) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, false);
            startActivityForResult(Intent.createChooser(intent, "Bantuin - Photo Profile"), 1);
        } else if (view.getId() == R.id.logout) {
            ActiveUser.logout();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finishAffinity();
        }
    }

    private void applyPhoto() {
        Picasso.with(getApplicationContext())
                .load(ActiveUser.getUser().getPhotoUrl())
                .transform(ImageRound.get(this, true))
                .into(userPhoto);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            linearLayout.setAlpha((float) 0.2);
            spinner.setVisibility(View.VISIBLE);
            File file = new File(FilePath.getPath(this, data.getData()));
            MultipartBody.Part images =
                    MultipartBody.Part.createFormData("fileToUpload", file.getName(),
                            RequestBody.create(MediaType.parse("image/*"), file));

            retrofit2.Call<okhttp3.ResponseBody> req = service.updatePhoto(images);
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String res = response.body().string();
                        if (!res.startsWith("AMAN#")) {
                            Toast.makeText(AccountActivity.this, res, Toast.LENGTH_SHORT).show();
                            linearLayout.setAlpha((float) 1.0);
                            spinner.setVisibility(View.GONE);
                        } else {
                            final String url = Config.BASE_URL_PICTURE + res.substring(5, res.length());
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(Uri.parse(url))
                                    .build();

                            ActiveUser.getUser().updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                String uid = ActiveUser.getUser().getUid();
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("users").child(uid).child("photo").setValue(url);
                                                applyPhoto();
                                                linearLayout.setAlpha((float) 1.0);
                                                spinner.setVisibility(View.GONE);
                                                Toast.makeText(AccountActivity.this, "Success update photo profile!", Toast.LENGTH_SHORT).show();

                                                databaseEvent.addListenerForSingleValueEvent(new CustomFirebaseListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                            Event e = postSnapshot.getValue(Event.class);
                                                            FirebaseUser u = ActiveUser.getUser();
                                                            User pp = new User(u.getDisplayName(),
                                                                    u.getEmail(), u.getPhotoUrl()
                                                                    .toString(), u.getUid());
                                                            if (e.getOrganizer().getUid().equals(ActiveUser.getUser().getUid())) {
                                                                postSnapshot.getRef().child(e.getKey()).child("organizer").setValue(pp);
                                                            }
                                                            if (e.getVolunteers() == null) continue;
                                                            for (User x : e.getVolunteers().values()) {
                                                                if (x.getUid().equals(u.getUid())) {
                                                                    postSnapshot.getRef().child("volunteers").child(x.getUid()).setValue(pp);
                                                                }
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
}
