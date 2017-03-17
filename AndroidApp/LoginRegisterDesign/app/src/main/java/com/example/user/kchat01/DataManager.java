package com.example.user.kchat01;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import API.IContacts;
import IMPL.Contacts;

/**
 * Created by Tudor Vasile on 3/11/2017.
 */

public class DataManager {

    //server:[{"contact_id":33,"timestmp":"2017-03-11T18:53:28.000Z","user_id":80,"name":"aaa","email":"aaa@aa.aa","username":"aaa","password":"aaa","phone_number":"7070707070","blocked":0,"session":1,"profile_picture":null,"biography":null}
    // ,{"contact_id":12,"timestmp":"2017-02-19T22:15:47.000Z","user_id":34,"name":"Kensuke Tamura","email":"kensuke.tamura@kcl.ac.uk","username":"ken","password":"ken","phone_number":"0777777777","blocked":0,"session":0,"profile_picture":"profile_picture34.jpg","biography":null},{"contact_id":32,"timestmp":"2017-03-11T18:52:31.000Z","user_id":74,"name":"testtest","email":"testtest@outlook.com","username":"tt","password":"12345","phone_number":"78979790853","blocked":0,"session":1,"profile_picture":null,"biography":null},{"contact_id":11,"timestmp":"2017-02-19T22:15:47.000Z","user_id":33,"name":"Tudor Vasile","email":"tudor.vasile@kcl.ac.uk","username":"tudorv","password":"tudor","phone_number":"0777777777","blocked":0,"session":0,"profile_picture":"profile_picture33.jpg","biography":null}]

    //name of rows
    public static final String CONTACT_TABLE_ID = "contactid";
    public static final String CONTACT_TABLE_TIMESTAMP = "timestamp";
    public static final String CONTACT_TABLE_USERID = "userid";
    public static final String CONTACT_TABLE_CONTACTNAME = "name";
    public static final String CONTACT_TABLE_EMAIL = "email";
    public static final String CONTACT_TABLE_USERNAME = "username";
    public static final String CONTACT_TABLE_PHONE = "phone";
    public static final String CONTACT_TABLE_PROFILEDIRECTORY = "profilelocation";
    public static final String CONTACT_TABLE_BIOGRAPHY = "biography";
    public static final String CONTACT_TABLE_BITMAP = "bitmap";

    public static final String PRIVATE_MESSAGES_MESSAGEID = "messageid";
    public static final String PRIVATE_MESSAGES_SENDERID = "sender";
    public static final String PRIVATE_MESSAGES_RECEIVERID = "receiver";
    public static final String PRIVATE_MESSAGES_MESSAGE = "message";
    public static final String PRIVATE_MESSAGES_TIMESTAMP = "timestamp";

    public static final String GROUP_MESSAGES_MESSAGEID = "messageid";
    public static final String GROUP_MESSAGES_SENDERID = "sender";
    public static final String GROUP_MESSAGES_RECEIVERID = "receiver";
    public static final String GROUP_MESSAGES_MESSAGE = "message";
    public static final String GROUP_MESSAGES_TIMESTAMP = "timestamp";

    private static final String CLIENT_DATABASE = "kchat_db"; // db
    private static final String CONTACTS_TABLE = "contacts"; // table name
    private static final String PRIVATE_MESSAGES_TABLE = "privatemessages"; // table name
    private static final String GROUP_MESSAGES_TABLE = "groupmessages"; // table name

    private static final int DB_VERSION = 1;
    private SQLiteDatabase db;

    public DataManager(Context context) {//CustomSQLiteOpenHelper class
        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        db = helper.getWritableDatabase();
    }

    // Insert a record
    public void insertContact(int contactid, String timestamp, int userid, String contactName, String email, String username, String phone, String profileDirectory, String biography,String usersImage){
// Add all the details to the table
        String query = "INSERT INTO " + CONTACTS_TABLE + " (" +
                CONTACT_TABLE_ID + ", " +
                CONTACT_TABLE_TIMESTAMP + ", " +
                CONTACT_TABLE_USERID + ", " +
                CONTACT_TABLE_CONTACTNAME + ", " +
                CONTACT_TABLE_EMAIL + ", " +
                CONTACT_TABLE_USERNAME + ", " +
                CONTACT_TABLE_PHONE + ", " +
                CONTACT_TABLE_PROFILEDIRECTORY + ", " +
                CONTACT_TABLE_BIOGRAPHY + ", " +
                CONTACT_TABLE_BITMAP + ") " +
                "VALUES (" +
                "'" + contactid + "'" + ", " +
                "'" + timestamp + "'" + ", " +
                "'" + userid + "'" + ", " +
                "'" + contactName + "'" + ", " +
                "'" + email + "'" + ", " +
                "'" + username + "'" + ", " +
                "'" + phone + "'" + ", " +
                "'" + profileDirectory + "'" + ", " +
                "'" + biography + "'" + ", " +
                "'" + usersImage + "'" +
                "); ";
        Log.i("insert() = ", query);
        db.execSQL(query);
    }
    public void flushAllData(){ //logout to delete all
// Delete the details from the table if already exists
        String query = "DELETE FROM " +  CONTACTS_TABLE+ ";";
        Log.i("delete() = ", query);
        db.execSQL(query);
    }
    public void deleteContact(String name){
// Delete the details from the table if already exists
        String query = "DELETE FROM " +  CONTACTS_TABLE+
                " WHERE " + CONTACT_TABLE_USERID +
                " = '" + name + "';";
        Log.i("delete() = ", query);
        db.execSQL(query);
    }

    public Cursor selectAllContacts() {
        Cursor c = db.rawQuery("SELECT *" +" from " +
                CONTACTS_TABLE, null);
        Contacts.contactList.clear();
        while (c.moveToNext()){
            int contactId = c.getInt(0);
            String timestamp = c.getString(1);
            int userId = c.getInt(2);
            String contactname = c.getString(3);
            String email = c.getString(4);
            String username = c.getString(5);
            String phone = c.getString(6);
            String profilelocation = c.getString(7);
            String biography = c.getString(8);
            String base64Bitmap = c.getString(9);
            /*
            public Contacts(int contactId, String timestamp,String userId,String contactName,String email,
                    String username,String phonenumber,String contactPicture,Bitmap profilePicture){
             */
            Bitmap profile = decodeBase64(base64Bitmap);
            IContacts contact = new Contacts(contactId,timestamp,Integer.toString(userId),contactname,email,username,phone,profilelocation,profile);
            Contacts.contactList.add(contact);
        }
        return c;
    }
    public Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public Cursor searchContact(String name) {
        String query = "SELECT *" +
                " from " +
                CONTACTS_TABLE + " WHERE " +
                CONTACT_TABLE_ID + " = '" + name + "';";
        Log.i("searchName() = ", query);
        Cursor c = db.rawQuery(query, null);
        return c;
    }

    // This class is created when our DataManager is initialized
    private class CustomSQLiteOpenHelper extends SQLiteOpenHelper {
        public CustomSQLiteOpenHelper(Context context) {
            super(context, CLIENT_DATABASE, null, DB_VERSION);
        }
        // This method only runs the first time the database is created
        @Override
        public void onCreate(SQLiteDatabase db) {// Creates a table for clients details

            String newContactsTableQueryString = "create table "
                    + CONTACTS_TABLE + " ("
                    + CONTACT_TABLE_ID
                    + " integer primary key not null,"
                    + CONTACT_TABLE_TIMESTAMP
                    + " text not null,"
                    + CONTACT_TABLE_USERID
                    + " integer not null,"
                    + CONTACT_TABLE_CONTACTNAME
                    + " text not null,"
                    + CONTACT_TABLE_EMAIL
                    + " text not null,"
                    + CONTACT_TABLE_USERNAME
                    + " text not null,"
                    + CONTACT_TABLE_PHONE
                    + " text,"
                    + CONTACT_TABLE_PROFILEDIRECTORY
                    + " text,"
                    + CONTACT_TABLE_BIOGRAPHY
                    + " text,"
                    + CONTACT_TABLE_BITMAP
                    + " text not null);";
            db.execSQL(newContactsTableQueryString);

            String newMessageTableQueryString = "create table "
                    + PRIVATE_MESSAGES_TABLE + " ("
                    + PRIVATE_MESSAGES_MESSAGEID
                    + " integer primary key not null,"
                    + PRIVATE_MESSAGES_SENDERID
                    + " integer not null,"
                    + PRIVATE_MESSAGES_RECEIVERID
                    + " integer not null,"
                    + PRIVATE_MESSAGES_MESSAGE
                    + " text not null,"
                    + PRIVATE_MESSAGES_TIMESTAMP
                    + " text not null);";
            db.execSQL(newMessageTableQueryString);

            String newgroupTableQueryString = "create table "
                    + GROUP_MESSAGES_TABLE + " ("
                    + GROUP_MESSAGES_MESSAGEID
                    + " integer primary key not null,"
                    + GROUP_MESSAGES_SENDERID
                    + " integer not null,"
                    + GROUP_MESSAGES_RECEIVERID
                    + " integer not null,"
                    + GROUP_MESSAGES_MESSAGE
                    + " text not null,"
                    + GROUP_MESSAGES_TIMESTAMP
                    + " text not null);";
            db.execSQL(newgroupTableQueryString);
        }
        // This method only runs when increment DB_VERSION
        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion) {
        }
    }
}



