package IMPL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import API.IAdaptors;
import API.IContacts;
import API.IGroups;
import API.IMessage;

/**
 * Created by Tudor Vasile on 2/11/2017.
 */

public class DatabaseAdaptor extends SQLiteOpenHelper implements IAdaptors  {

    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "ChatMobileDB";

    public DatabaseAdaptor(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // SQL statement to create book table
        String CREATE_CONTACTS_TABLE = "CREATE TABLE contacts ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "global_id INTEGER, "+
                "name TEXT, "+
                "username TEXT" +")";

        String CREATE_MESSAGES_TABLE = "CREATE TABLE messages ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sender_id INTEGER, "+
                "group_id INTEGER, "+
                "message TEXT, "+
                "timestamp DATETIME,+" +
                "FOREIGN KEY(sender_id) REFERENCES contacts(id)," +
                "FOREIGN KEY(group_id) REFERENCES groups(id))";

        String CREATE_GROUPS_TABLE = "CREATE TABLE groups ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, "+
                "description TEXT, "+
                "type INTEGER )";

        db.execSQL(CREATE_MESSAGES_TABLE);
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_GROUPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS messages");
        db.execSQL("DROP TABLE IF EXISTS groups");
        db.execSQL("DROP TABLE IF EXISTS contacts");

        // create fresh books table
        this.onCreate(db);
    }

    @Override
    public void initialDatabaseSetup() {
        //some change
    }

    @Override
    public boolean addToContactsTable(int contactId, int globalId, String contactName, String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", contactId);
        values.put("global_id", globalId);
        values.put("name", contactName);
        values.put("username", username);
        db.insert("contacts", // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values
        db.close();
        return true;
    }

    @Override
    public boolean addToGroupsTable(int id, String name, String description, int type) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("name", name);
        values.put("description", description);
        values.put("type", type);
        db.insert("groups", // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values
        db.close();
        return true;
    }

    @Override
    public boolean addToMessagesTable(int messageId, int senderId, int groupId, String message, Date timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", messageId);
        values.put("sender_id", senderId);
        values.put("group_id", groupId);
        values.put("message", message);
        values.put("timestamp",timestamp.toString());
        db.insert("messages", // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values
        db.close();
        return true;
    }

    private static final String[] COLUMNS = {"id","global_id","name","username"};
    public IContacts getContact(int contactId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query("contacts", // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(contactId) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit
        // if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();
        //  build the object
        IContacts contact = new Contacts(Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(1)),cursor.getString(2),cursor.getString(3));
        Log.d("getBook("+contactId+")", contact.toString());
        return contact;
    }

    private static final String[] COLUMNS2 = {"id","sender_id","group_id","message","timestamp"};
    public IMessage getMessage(int messageId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query("messages", // a. table
                        COLUMNS2, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(messageId) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit
        // if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();
        //  build the object
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(cursor.getString(4));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        IMessage message = new Message(Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(1)),Integer.parseInt(cursor.getString(2)),cursor.getString(3),convertedDate);
        Log.d("getBook("+messageId+")", message.toString());
        return message;
    }

    private static final String[] COLUMNS3 = {"id","name","description","type"};
    public IGroups getGroup(int contactId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query("groups", // a. table
                        COLUMNS3, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(contactId) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit
        // if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();
        //  build the object
        IGroups group = new Groups(Integer.parseInt(cursor.getString(0)),cursor.getString(1),cursor.getString(2),Integer.parseInt(cursor.getString(3)));
        Log.d("getBook("+contactId+")", group.toString());
        return group;
    }

    public List<IContacts> getAllContacts() {
        List<IContacts> contacts = new LinkedList<>();
        String query = "SELECT  * FROM " + "contacts";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        IContacts contact = null;
        if (cursor.moveToFirst()) {
            do {
                contact = new Contacts(Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(1)),cursor.getString(2),cursor.getString(3));
                // Add book to books
                contacts.add(contact);
            } while (cursor.moveToNext());
        }
        Log.d("getAllBooks()", contacts.toString());
        return contacts;
    }

    public List<IGroups> getAllGroups() {
        List<IGroups> groups = new LinkedList<>();
        String query = "SELECT  * FROM " + "groups";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        IGroups group = null;
        if (cursor.moveToFirst()) {
            do {
                group = new Groups(Integer.parseInt(cursor.getString(0)),cursor.getString(1),cursor.getString(2),Integer.parseInt(cursor.getString(3)));
                // Add book to books
                groups.add(group);
            } while (cursor.moveToNext());
        }
        Log.d("getAllBooks()", groups.toString());
        return groups;
    }

    public List<IMessage> getAllMessages() {
        List<IMessage> messages = new LinkedList<>();
        String query = "SELECT  * FROM " + "messages";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(cursor.getString(4));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        IMessage message = null;
        if (cursor.moveToFirst()) {
            do {
                 message = new Message(Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(1)),Integer.parseInt(cursor.getString(2)),cursor.getString(3),convertedDate);
                // Add book to books
                messages.add(message);
            } while (cursor.moveToNext());
        }
        Log.d("getAllBooks()", messages.toString());
        return messages;
    }

    public int updateContact(IContacts contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", contact.getContactId());
        values.put("global_id", contact.getGlobalId());
        values.put("name", contact.getContactName());
        values.put("username", contact.getUsername());

        int i = db.update("contacts", //table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(contact.getContactId()) });

        db.close();
        return i;
    }

    public int updateGroup(IGroups group) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", group.getGroupId());
        values.put("name", group.getName());
        values.put("description", group.getDescription());
        values.put("type", group.getType());

        int i = db.update("contacts", //table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(group.getGroupId()) });

        db.close();
        return i;
    }

    public int updateMessage(IMessage message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", message.getMessageId());
        values.put("sender_id", message.getSenderId());
        values.put("group_id", message.getGroupId());
        values.put("message", message.getMessage());
        values.put("timestamp", message.getTimestamp().toString());

        int i = db.update("contacts", //table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(message.getMessageId()) });

        db.close();
        return i;
    }

    public void deleteGroup(IGroups group) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("groups", //table name
                "id"+" = ?",  // selections
                new String[] { String.valueOf(group.getGroupId()) }); //selections args
        db.close();
        Log.d("deleteBook", group.toString());
    }

    public void deleteContact(IContacts contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("contacts", //table name
                "id"+" = ?",  // selections
                new String[] { String.valueOf(contact.getContactId()) }); //selections args
        db.close();
        Log.d("deleteBook", contact.toString());
    }

    public void deleteMessage(IMessage message) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("messages", //table name
                "id"+" = ?",  // selections
                new String[] { String.valueOf(message.getMessageId()) }); //selections args
        db.close();
        Log.d("deleteBook", message.toString());
    }

    @Override
    public boolean flushData() {
        return false;
    }
}
