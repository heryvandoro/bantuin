package net.slc.hoga.bantuin.Model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActiveUser {
    private static FirebaseUser session = FirebaseAuth.getInstance().getCurrentUser();
    private static User userDB;

    public static void setUser(FirebaseUser temp){ session = temp; }

    public static FirebaseUser getUser(){
        return session;
    }

    public static boolean isLogged(){
        return FirebaseAuth.getInstance().getCurrentUser() == null? false : true;
    }

    public static User getUserDB() {
        return userDB;
    }

    public static void setUserDB(User userDB) {
        ActiveUser.userDB = userDB;
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }
}
