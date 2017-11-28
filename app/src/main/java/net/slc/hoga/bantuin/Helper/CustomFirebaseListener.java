package net.slc.hoga.bantuin.Helper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public abstract class CustomFirebaseListener implements ValueEventListener {
    public abstract void onDataChange(DataSnapshot dataSnapshot);

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}