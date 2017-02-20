package IMPL;

import android.graphics.Bitmap;

import java.util.ArrayList;

import API.IContacts;
import API.IGroups;

/**
 * Created by tudor on 2/20/2017.
 */

public class MasterUser {


    static Bitmap usersprofile;
    static String username;
    static String email;
    static String telephonenumber;
    static String biography;

    static ArrayList<IContacts> contactlist = new ArrayList<>();
    static ArrayList<IGroups> groupsList = new ArrayList<>();

    public MasterUser(Bitmap usersprofile, String username, String email,String telephonenumber,String biography){
        this.usersprofile=usersprofile;
        this.username=username;
        this.email=email;
        this.telephonenumber=telephonenumber;
        this.biography=biography;
    }

    public Bitmap getUsersprofile() {
        return usersprofile;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephonenumber() {
        return telephonenumber;
    }

    public String getBiography() {
        return biography;
    }
}
