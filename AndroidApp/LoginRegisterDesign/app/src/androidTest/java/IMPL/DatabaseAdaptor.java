package IMPL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

import API.IAdaptors;

/**
 * Created by Tudor Vasile on 2/11/2017.
 */

public class DatabaseAdaptor extends SQLiteOpenHelper implements IAdaptors  {


    // Database
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

    }

    @Override
    public boolean addToContactsTable(int contactId, int globalId, String contactName, String username) {
        return false;
    }

    @Override
    public boolean addToGroupsTable(int groupId, String globalId, String name, String username) {
        return false;
    }

    @Override
    public boolean addToMessagesTable(int messageId, int senderId, int groupId, String message, Date timestamp) {
        return false;
    }

    @Override
    public boolean flushData() {
        return false;
    }
}
