package net.slc.hoga.bantuin;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import net.slc.hoga.bantuin.Helper.ImageRound;
import net.slc.hoga.bantuin.Model.ActiveUser;

public class AccountActivity extends MasterActivity {

    ImageView userPhoto, editPhoto;

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

        userPhoto = findViewById(R.id.userPhoto);
        editPhoto = findViewById(R.id.editIcon);

        Picasso.with(getApplicationContext())
                .load(ActiveUser.getUser().getPhotoUrl())
                .transform(ImageRound.get(this, true))
                .into(userPhoto);
    }
}
