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
    private String timestamp;
    private String userId;
    private String contactName;
    private String email;
    private String username;
    private String phonenumber;
    private String contactPicture;
    private String contactBiography;
    private Bitmap profilePicture;
    public static ArrayList<IContacts> contactList = new ArrayList<>();
    public static ArrayList<IContacts> sentRequests = new ArrayList<>();
    public static ArrayList<IContacts> receivedRequests = new ArrayList<>();
    public static ArrayList<IContacts> searchList = new ArrayList<>();

    public static ArrayList<IContacts> activeChat = new ArrayList<>();


    public static ArrayList<IContacts> getContactList() {
        return contactList;
    }
 /*
 [{"receiverId":34,"receiverName":"Kensuke Tamura","receiverProfilePicture":"profile_picture34.jpg","senderId":2,
 "senderName":"Tudor Zugravu","senderProfilePicture":"profile_picture2.jpg","message":"1111111","timestmp":"2017-03-17T14:11:24.000Z"}
 ,{"receiverId":33,"receiverName":"Tudor Vasile","receiverProfilePicture":"profile_picture33.jpg","senderId":2,
 "senderName":"Tudor Zugravu","senderProfilePicture":"profile_picture2.jpg","message":"Yo, Tudor!!! Hello!","timestmp":"2017-03-14T15:37:55.000Z"}]
  */


    public Contacts(){
    }

    public Contacts(int contactId, String timestamp,String userId,String contactName,String email,
                    String username,String phonenumber,String contactPicture){
       this.contactId=contactId;
        this.timestamp=timestamp;
        this.userId=userId;
        this.contactName=contactName;
        this.email=email;
        this.username=username;
        this.phonenumber=phonenumber;
        this.contactPicture=contactPicture;
    }

    public Contacts(int contactId, String timestamp,String userId,String contactName,String email,
                    String username,String phonenumber,String contactPicture,Bitmap profilePicture){
        this.contactId=contactId;
        this.timestamp=timestamp;
        this.userId=userId;
        this.contactName=contactName;
        this.email=email;
        this.username=username;
        this.phonenumber=phonenumber;
        this.contactPicture=contactPicture;
        this.profilePicture=profilePicture;
    }

    public Contacts(String userId,String username, String name){
        this.userId=userId;
        this.username=username;
        this.contactName=name;
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
    public String getContactProfile() {
        return this.contactPicture;
    }

    @Override
    public void setContactPicture(String contactPicture) {
    this.contactPicture = contactPicture;
    }

    @Override
    public Bitmap getBitmap() {
        return this.profilePicture;
    }

    @Override
    public void setBitmap(Bitmap contactsBitmap) {
    this.profilePicture = contactsBitmap;
    }

    @Override
    public void setBiography(String biography) {
        this.contactBiography = biography;
    }

    @Override
    public String getBiography() {
        return this.contactBiography;
    }
    public static ArrayList<IContacts> getSentRequests() {
        return sentRequests;
    }
    public static ArrayList<IContacts> getReceivedRequests() {
        return receivedRequests;
    }
    public static ArrayList<IContacts> getSearchList() {
        return searchList;
    }
}
