package net.slc.bantuin.Model;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActiveUser {
    private static FirebaseUser session = FirebaseAuth.getInstance().getCurrentUser();
    public static GoogleSignInClient googleSignInClient;

    public static void setUser(FirebaseUser temp){ session = temp; }

    public static FirebaseUser getUser(){
        return session;
    }

    public static boolean isLogged(){
        return FirebaseAuth.getInstance().getCurrentUser() == null? false : true;
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }
}
