package com.yuri_khechoyan.hw4_broadcastreceiver;

/**
 * Created by Yuri Khechoyan on 3/4/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import java.util.ArrayList;

class DBHandler extends SQLiteOpenHelper {

    // database version
    private static final int DATABASE_VERSION = 1;
    // database name
    private static final String DATABASE_NAME = "webaddress";
    // table address
    private static final String TABLE_ADDRESS = "address";
    // column id
    private static final String KEY_ID = "id";
    // column web site
    private static final String KEY_WEBSITE = "website";
    // holds context for application
    private Context context;


    //Constructor for DBHandler
    DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    //Creates a table - if it is needed
    @Override
    public void onCreate(SQLiteDatabase db) {
        // String that creates a table if needed
        String create_address_table = "CREATE TABLE " + TABLE_ADDRESS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY, " + KEY_WEBSITE + " TEXT)";
        db.execSQL(create_address_table);
    }

    //This will upgrade the the database - if needed
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop older table if exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDRESS);
        // create new database
        onCreate(db);
    }

    //Adds a new row to the database, when needed
    void addSite(String site){
        //SQL query command for receiving the values
        String query = "SELECT * FROM " + TABLE_ADDRESS;
        //SQL query command to open the database
        SQLiteDatabase db = this.getWritableDatabase();
        //SQL query command for running the query
        Cursor cursor = db.rawQuery(query, null);
        //Get values from cursor
        if (cursor.moveToFirst()){
            do{
                //if value already - exists exit method
                if(cursor.getString(1).equals(site)){
                    return;
                }
            }while(cursor.moveToNext());
        }
        //Close cursor
        cursor.close();
        //Creates new content values
        ContentValues values = new ContentValues();
        //Adds values to the content
        values.put(KEY_WEBSITE, site);
        //Inserts values to the database
        db.insert(TABLE_ADDRESS, null, values);
        //Closes the database when done
        db.close();
    }


    //Adds all of the websites into an ArrayList - of type String
    void getAllSites(ArrayList<String> siteList) {
        // SQL query command to retrieve values
        String query = "SELECT * FROM " + TABLE_ADDRESS;
        //SQL query command to open the database
        SQLiteDatabase db = this.getWritableDatabase();
        // SQL query command to run query
        Cursor cursor = db.rawQuery(query, null);
        // get values from cursor
        if (cursor.moveToFirst()){
            do{
                //Adds values to ArrayList
                siteList.add(cursor.getString(1));
            }while(cursor.moveToNext());
        }
        // close cursor
        cursor.close();
        // close database
        db.close();
    }

    //Deletes the row from the database
    void deleteSite(int id){
        //Identifies that the frirst 3 sites cannort be deleted
        if(id > 3) {
            //Opens the database
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_ADDRESS, KEY_ID + "=?",
                    //Deletes the website row
                    new String[]{String.valueOf(id)});
            //Closes the database
            db.close();
        }
        else{
            //Throws toast to let user know that the website cannot be deleted
            Toast.makeText(context, "Cannot delete element #" + id + " from List", Toast.LENGTH_SHORT).show();
        }
    }


    //Get method that retrieves the amount of rows in the database
    int getCount(){
        //Query command to get the value
        String query = "SELECT * FROM " + TABLE_ADDRESS;
        //Opens the database
        SQLiteDatabase db = this.getReadableDatabase();
        //Runs the SQL query
        Cursor cursor = db.rawQuery(query, null);
        //Try to gt the value from the cursor
        try{
            cursor.moveToFirst();
            //Closes the cursor
            cursor.close();
            //Closes the database
            db.close();
        }
        //Throws an exception if the database is empty and returns 0
        catch(Exception e){
            return 0;
        }
        //Returns the number of entries in the database
        return cursor.getCount();
    }


    //Retrieves the values from the website row in database
    int getSiteID(String site){
        // query command to get row number of website from database
        String query = "SELECT * FROM " + TABLE_ADDRESS + " WHERE " + KEY_WEBSITE + "='" + site + "'";
        //Opens the database
        SQLiteDatabase db = this.getWritableDatabase();
        //Runs the query
        Cursor cursor = db.rawQuery(query, null);
        //Initializes the integer value for the row number - 0
        int a = 0;
        // get values from cursor
        if(cursor.moveToFirst()){
            //Sets the ro number to 0
            a = Integer.parseInt(cursor.getString(0));
        }
        //Closes the cursor
        cursor.close();
        //Closes the database
        db.close();
        //Returns the the row number for the toast
        return a;
    }
}