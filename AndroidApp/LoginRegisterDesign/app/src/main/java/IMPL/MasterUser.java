package IMPL;

import android.graphics.Bitmap;

import java.util.ArrayList;

import API.IContacts;
import API.IGroups;

/**
 * Created by tudor on 2/20/2017.
 */

public class MasterUser {

    public static int usersId;
    static int blocked;
    static int session;
    static String fullName;
    static Bitmap usersprofile;
    static String username;
    static String email;
    static String telephonenumber;
    static String biography;
    static String profileLocation;
    static boolean isAdmin;

    static ArrayList<IContacts> contactlist = new ArrayList<>();
    static ArrayList<IGroups> groupsList = new ArrayList<>();

    public MasterUser(Bitmap usersprofile, String username, String email,String telephonenumber,String biography){
        this.usersprofile=usersprofile;
        this.username=username;
        this.email=email;
        this.telephonenumber= telephonenumber;
        this.biography=biography;
    }

    public MasterUser(){
    }

    //user id   int
    //name  String
    //email  String
    //username  String
    //phone_number  int
    //blocked  int
    //session  int
    //profile_picture  String

    public MasterUser(int userId, String name, String email, String username, String phoneNumber, int blocked, int session, String profile_picture){
        this.usersId=userId;
        this.fullName = name;
        this.email=email;
        this.username=username;
        this.telephonenumber=phoneNumber;
        this.blocked=blocked;
        this.session = session;
        this.profileLocation = profile_picture;
    }

    public Bitmap getUsersprofile() {
        return usersprofile;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
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

    public String getuserId() {
        return Integer.toString(this.usersId);
    }
    public String getProfileLocation() {
        return this.profileLocation;
    }

    public void setBiography(String biography){
        this.biography = biography;
    }


    public void setUsersImage(Bitmap profile) {
       this.usersprofile=profile;
    }


    public static void setUsername(String username) {
        MasterUser.username = username;
    }

    public static void setEmail(String email) {
        MasterUser.email = email;
    }

    public static void setTelephonenumber(String telephonenumber) {
        MasterUser.telephonenumber = telephonenumber;
    }
}
