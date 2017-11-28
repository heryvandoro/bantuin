package net.slc.hoga.bantuin.Model;

public class User {
    String name;
    String email;
    String photo;
    String uid; //ini key untuk user

    public User(){

    }

    public User(String name, String email, String photo, String uid ){
        this.name = name;
        this.email = email;
        this.photo = photo;
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
