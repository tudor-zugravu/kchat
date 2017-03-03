package IMPL;

import android.graphics.Bitmap;

import com.example.user.kchat01.R;

import java.util.ArrayList;

import API.IContacts;

/**
 * Created by Tudor Vasile on 2/15/2017.
 */

public class Contacts implements IContacts {
    private int contactId;
    private int requestNum;
    private String timestamp;
    private String userId;
    private String contactName;
    private String email;
    private String username;
    private String phonenumber;
    private int blocked;
    private int session;
    private String contactPicture;
    private Bitmap profilePicture;

    public static ArrayList<IContacts> contactList = new ArrayList<>();

    public Contacts(int contactId, int requestNum, String timestamp,String userId,String contactName,String email,
                    String username,String phonenumber,int blocked,int session,String contactPicture){
       this.contactId=contactId;
        this.requestNum=requestNum;
        this.timestamp=timestamp;
        this.userId=userId;
        this.contactName=contactName;
        this.email=email;
        this.username=username;
        this.phonenumber=phonenumber;
        this.blocked=blocked;
        this.session=session;
        this.contactPicture=contactPicture;
       // this.profilePicture=profilePicture;
    }


    @Override
    public int getContactId() {
        return 0;
    }

    @Override
    public void setContactId(int contactId) {

    }

    @Override
    public int getRequestNum() {
        return 0;
    }

    @Override
    public void setRequestNum(int requestNum) {

    }

    @Override
    public String getTimestamp() {
        return null;
    }

    @Override
    public void setTimestamp(String timestamp) {

    }

    @Override
    public String getUserId() {
        return null;
    }

    @Override
    public void setUserId(String userId) {

    }

    @Override
    public String getContactName() {
        return null;
    }

    @Override
    public void setContactName(String contactName) {

    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public void setEmail(String email) {

    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public void setUsername(String username) {

    }

    @Override
    public String getPhoneNumber() {
        return null;
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {

    }

    @Override
    public int getblcoked() {
        return 0;
    }

    @Override
    public void setBlocked(int blockedNum) {

    }

    @Override
    public int getSessionNum() {
        return 0;
    }

    @Override
    public void setSessionNum(int sessionNum) {

    }

    @Override
    public String getContactProfile() {
        return null;
    }

    @Override
    public void setContactPicture(String contactPicture) {

    }

    @Override
    public Bitmap getBitmap() {
        return null;
    }

    @Override
    public void setBitmap(Bitmap contactsBitmap) {

    }
}
