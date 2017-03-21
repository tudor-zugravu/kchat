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
import API.IMessage;
import IMPL.Contacts;
import IMPL.MasterUser;
import IMPL.Message;

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
    public static final String PRIVATE_MESSAGES_TYPE = "type";

    public static final String GROUP_MESSAGES_MESSAGEID = "messageid";
    public static final String GROUP_MESSAGES_SENDERID = "sender";
    public static final String GROUP_MESSAGES_RECEIVERID = "receiver";
    public static final String GROUP_MESSAGES_MESSAGE = "message";
    public static final String GROUP_MESSAGES_TIMESTAMP = "timestamp";
    public static final String GROUP_MESSAGES_TYPE = "type";

    private static final String CLIENT_DATABASE = "kchat_db"; // db
    private static final String CONTACTS_TABLE = "contacts"; // table name
    private static final String PRIVATE_MESSAGES_TABLE = "privatemessages"; // table name
    private static final String GROUP_MESSAGES_TABLE = "groupmessages"; // table name
    private static final String BUFFER_MESSAGES_TABLE = "buffermessages"; // table name

    private static final int DB_VERSION = 1;
    private SQLiteDatabase db;

    public DataManager(Context context) {//CustomSQLiteOpenHelper class
        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        db = helper.getWritableDatabase();
    }

    public void insertContact(int contactid, String timestamp, int userid, String contactName, String email, String username, String phone, String profileDirectory, String biography,String usersImage){
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

    static int counter = 0;
    public void insertPrivateMessage(int messageId, int senderId, int receiverId, String message, String timestamp, String type){
        String query = "INSERT OR REPLACE INTO " + PRIVATE_MESSAGES_TABLE + " (" +
                PRIVATE_MESSAGES_MESSAGEID + ", " +
                PRIVATE_MESSAGES_SENDERID + ", " +
                PRIVATE_MESSAGES_RECEIVERID + ", " +
                PRIVATE_MESSAGES_MESSAGE + ", " +
                PRIVATE_MESSAGES_TIMESTAMP + ", " +
                PRIVATE_MESSAGES_TYPE + " ) " +
                " VALUES (" + //  public Message(int messageId,int senderId,String message,String timestamp){

                "'" + messageId + "'" + ", " +
                "'" + senderId + "'" + ", " +
                "'" + receiverId + "'" + ", " +
                "'" + message + "'" + ", " +
                "'" + timestamp + "'" + ", " +
                "'" + type + "'" +
                " ); ";
        Log.i("insert() = ", query);
        db.execSQL(query);
        counter = counter + 1;
        Log.d("OFFLINE TESTER", "this was called  " + counter + " times");
    }

    public void insertGroupMessage(int messageId, int senderId, int receiverId, String message, String timestamp, String type){
        String query = "INSERT INTO " + GROUP_MESSAGES_TABLE + " (" +
                GROUP_MESSAGES_MESSAGEID + ", " +
                GROUP_MESSAGES_SENDERID + ", " +
                GROUP_MESSAGES_RECEIVERID + ", " +
                GROUP_MESSAGES_MESSAGE + ", " +
                GROUP_MESSAGES_TIMESTAMP + ", " +
                GROUP_MESSAGES_TYPE + ") " +
                "VALUES (" +
                "'" + messageId + "'" + ", " +
                "'" + senderId + "'" + ", " +
                "'" + receiverId + "'" + ", " +
                "'" + message + "'" + ", " +
                "'" + timestamp + "'" + ", " +
                "'" + type + "'" +
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

    public void flushAllMessageData(){ //logout to delete all
// Delete the details from the table if already exists
        String query = "DELETE FROM " +  PRIVATE_MESSAGES_TABLE+ ";";
        Log.i("delete() = ", query);
        db.execSQL(query);
    }

    public void deletePrivateContactMessages(String senderId,String receiverId){
// Delete the details from the table if already exists
        String query = "DELETE FROM " +  PRIVATE_MESSAGES_TABLE+
                " WHERE " + PRIVATE_MESSAGES_SENDERID +
                " = '" + senderId + "' AND " +
                PRIVATE_MESSAGES_RECEIVERID +
                " = '" + receiverId+"';";
        Log.i("delete() = ", query);
        db.execSQL(query);

        String query2 = "DELETE FROM " +  PRIVATE_MESSAGES_TABLE+
                " WHERE " + PRIVATE_MESSAGES_SENDERID +
                " = '" + receiverId + "' AND " +
                PRIVATE_MESSAGES_RECEIVERID +
                " = '" + senderId+"';";
        Log.i("delete() = ", query);
        db.execSQL(query2);
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

            Bitmap profile = decodeBase64(base64Bitmap);
            IContacts contact = new Contacts(contactId,timestamp,Integer.toString(userId),contactname,email,username,phone,profilelocation,profile);
            Contacts.contactList.add(contact);
        }
        return c;
    }

    public Cursor selectAllPrivateMessages(int senderId, int myID) { ///name = 34 ken //myId is 2 Tudor
        Cursor c = db.rawQuery("SELECT DISTINCT * " +
                " FROM " + PRIVATE_MESSAGES_TABLE +
                " WHERE" + "( "+ PRIVATE_MESSAGES_SENDERID + " = '" + senderId +"' )"+
                " AND ( " + PRIVATE_MESSAGES_RECEIVERID + " = '" + myID +"' )"+ " OR " +
                "( "+ PRIVATE_MESSAGES_SENDERID + " = '" + myID +"' )"+
                " AND ( " + PRIVATE_MESSAGES_RECEIVERID + " = '" + senderId +"' )"+
                " ORDER BY datetime(timestamp) DESC ;", null);
        if(ChatsActivity.dataList!=null) {
            ChatsActivity.dataList.clear();
        }
        while (c.moveToNext()){
            int messageId = c.getInt(0);
            int sender_id = c.getInt(1);
            String message= c.getString(3);
            String timestamp = c.getString(4);

            IMessage messageStored = new Message(messageId,sender_id,message,timestamp);
            if(sender_id == MasterUser.usersId){
                messageStored.setMe(true);//if the message is sender, set "true". if not, set "false".
            }else{
                messageStored.setMe(false);//if the message is sender, set "true". if not, set "false".
            }
            if(ChatsActivity.dataList!=null )ChatsActivity.dataList.add(messageStored);
        }
        Log.d("OFFLINE TESTER", "size of the offline list is  " + ChatsActivity.dataList.size());
        return c;
    }

    public Cursor selectAllGroupMessages(int groupId, int myID) { ///name = 34 ken //myId is 2 Tudor
        Cursor c = db.rawQuery("SELECT DISTINCT * " +
                " FROM " + GROUP_MESSAGES_TABLE +
                " WHERE" + "( "+ GROUP_MESSAGES_SENDERID + " = '" + groupId +"' )"+
                " AND ( " + GROUP_MESSAGES_RECEIVERID + " = '" + myID +"' )"+ " OR " +
                "( "+ GROUP_MESSAGES_SENDERID + " = '" + myID +"' )"+
                " AND ( " + GROUP_MESSAGES_RECEIVERID + " = '" + groupId +"' )"+
                " ORDER BY datetime(timestamp) DESC ;", null);
        if(ChatsActivity.dataList!=null) {
            ChatsActivity.dataList.clear();
        }
        while (c.moveToNext()){
            int messageId = c.getInt(0);
            int sender_id = c.getInt(1);
            String message= c.getString(3);
            String timestamp = c.getString(4);

            IMessage messageStored = new Message(messageId,sender_id,message,timestamp);
            if(sender_id == MasterUser.usersId){
                messageStored.setMe(true);//if the message is sender, set "true". if not, set "false".
            }else{
                messageStored.setMe(false);//if the message is sender, set "true". if not, set "false".
            }
            if(ChatsActivity.dataList!=null )ChatsActivity.dataList.add(messageStored);
        }
        Log.d("OFFLINE TESTER", "size of the offline list is  " + ChatsActivity.dataList.size());
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
                    + " text not null,"
                    + PRIVATE_MESSAGES_TYPE
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
                    + " text not null,"
                    + GROUP_MESSAGES_TYPE
                    + " text not null);";
            db.execSQL(newgroupTableQueryString);

            String bufferMessageTableQueryString = "create table "
                    + BUFFER_MESSAGES_TABLE + " ("
                    + PRIVATE_MESSAGES_MESSAGEID
                    + " integer primary key not null,"
                    + PRIVATE_MESSAGES_SENDERID
                    + " integer not null,"
                    + PRIVATE_MESSAGES_RECEIVERID
                    + " integer not null,"
                    + PRIVATE_MESSAGES_MESSAGE
                    + " text not null,"
                    + PRIVATE_MESSAGES_TIMESTAMP
                    + " text not null,"
                    + PRIVATE_MESSAGES_TYPE
                    + " text not null);";
            db.execSQL(bufferMessageTableQueryString);
        }
        // This method only runs when increment DB_VERSION
        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion) {
        }
    }
}



