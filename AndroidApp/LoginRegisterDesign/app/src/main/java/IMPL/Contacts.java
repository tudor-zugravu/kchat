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
        return this.contactId;
    }

    @Override
    public void setContactId(int contactId) {
        this.contactId=contactId;
    }

    @Override
    public int getRequestNum() {
        return this.requestNum;
    }

    @Override
    public void setRequestNum(int requestNum) {
    this.requestNum = requestNum;
    }

    @Override
    public String getTimestamp() {
        return this.timestamp;
    }

    @Override
    public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
    }

    @Override
    public String getUserId() {
        return this.userId;
    }

    @Override
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String getContactName() {
        return this.contactName;
    }

    @Override
    public void setContactName(String contactName) {
        this.contactName=contactName;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public void setUsername(String username) {
        this.username= username;
    }

    @Override
    public String getPhoneNumber() {
        return this.phonenumber;
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
    this.phonenumber = phoneNumber;
    }

    @Override
    public int getblcoked() {
        return this.blocked;
    }

    @Override
    public void setBlocked(int blockedNum) {
        this.blocked = blockedNum;
    }

    @Override
    public int getSessionNum() {
        return this.session;
    }

    @Override
    public void setSessionNum(int sessionNum) {
    this.session =sessionNum;
    }

    @Override
    public String getContactProfile() {
        return this.contactPicture;
    }

    @Override
    public void setContactPicture(String contactPicture) {
    this.contactPicture = contactPicture;
    }

    @Override
    public Bitmap getBitmap() {
        return null;
    }

    @Override
    public void setBitmap(Bitmap contactsBitmap) {

    }
}
