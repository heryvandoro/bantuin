package net.slc.bantuin.Model;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActiveUser {
    private static FirebaseAuth session = FirebaseAuth.getInstance();

    public static FirebaseAuth getSession(){
        return session;
    }

    public static FirebaseUser getUser(){
        return session.getCurrentUser();
    }

    public static void setUser(FirebaseAuth temp){
        session = temp;
    }

    public static boolean isLogged(){
        return session.getCurrentUser()==null? false : true;
    }

    public static void logout(){
        session.signOut();
        //manually invalidate facebook token
        LoginManager.getInstance().logOut();
    }
}
