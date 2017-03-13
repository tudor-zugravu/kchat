package com.example.user.kchat01;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Tudor Vasile on 3/11/2017.
 */

public class DataManager {
    public static final String TABLE_ROW_ID = "_id";
    public static final String TABLE_ROW_NAME = "name";
    public static final String TABLE_ROW_AGE = "age";

    private static final String DB_NAME = "address_book_db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_N_AND_A = "names_and_addresses";
    // This is the actual database
    private SQLiteDatabase db;

    public DataManager(Context context) {
//CustomSQLiteOpenHelper class
        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        db = helper.getWritableDatabase();
    }

    // Insert a record
    public void insert(String name, String age){
// Add all the details to the table
        String query = "INSERT INTO " + TABLE_N_AND_A + " (" +
                TABLE_ROW_NAME + ", " +
                TABLE_ROW_AGE + ") " +
                "VALUES (" +
                "'" + name + "'" + ", " +
                "'" + age + "'" +
                "); ";
        Log.i("insert() = ", query);
        db.execSQL(query);
    }

    // Delete a record
    public void delete(String name){
// Delete the details from the table if already exists
        String query = "DELETE FROM " + TABLE_N_AND_A +
                      " WHERE " + TABLE_ROW_NAME +
                       " = '" + name + "';";
                       Log.i("delete() = ", query);
        db.execSQL(query);
    }

    // Get all the records
    public Cursor selectAll() {
        Cursor c = db.rawQuery("SELECT *" +" from " +
                TABLE_N_AND_A, null);
        return c;
    }

    // Find a specific record
    public Cursor searchName(String name) {
        String query = "SELECT " +
                TABLE_ROW_ID + ", " +
                TABLE_ROW_NAME +
                ", " + TABLE_ROW_AGE +
                " from " +
                TABLE_N_AND_A + " WHERE " +
                TABLE_ROW_NAME + " = '" + name + "';";
        Log.i("searchName() = ", query);
        Cursor c = db.rawQuery(query, null);
        return c;
    }

    // This class is created when our DataManager is initialized
    private class CustomSQLiteOpenHelper extends SQLiteOpenHelper {
        public CustomSQLiteOpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }
        // This method only runs the first time the database is created
        @Override
        public void onCreate(SQLiteDatabase db) {
// Create a table for photos and all their details
            String newTableQueryString = "create table "
                    + TABLE_N_AND_A + " ("
                    + TABLE_ROW_ID
                    + " integer primary key autoincrement not null,"
                    + TABLE_ROW_NAME
                    + " text not null,"
                    + TABLE_ROW_AGE
                    + " text not null);";
            db.execSQL(newTableQueryString);
        }
        // This method only runs when we increment DB_VERSION
        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion) {
        }
    }
}



