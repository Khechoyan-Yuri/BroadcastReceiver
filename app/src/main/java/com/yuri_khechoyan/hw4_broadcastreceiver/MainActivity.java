package com.yuri_khechoyan.hw4_broadcastreceiver;

/* ========================================================================== */
/*	PROGRAM Broadcast Receiver

    AUTHOR: Yuri Khechoyan
    COURSE NUMBER: CIS 472
    COURSE SECTION NUMBER: 01
    INSTRUCTOR NAME: Dr. Tian
    PROJECT NUMBER: 4
    DUE DATE: 3/9/2017

SUMMARY

    This program is designed to receive an SMS text message
    that contains a hyperlink to a website.
    When the hyperlink is received, the application
    is automatically opened.
    When the application opens, the WebView inside of the
    application is directed to the hyperlink that was sent.

    Furthermore, the hyperlink that was sent is added to the ListView
    There is a delete button at the bottom of the application that enables the user
    to delete all hyperlinks that were added  from the SMS text messages*

    * - the first 3 links (Fontbonne, Microsoft, & Amazon) cannot be deleted
    they are static hyperlinks that will never be deleted.

    If user attempts to delete them, a toast will be thrown letting the
    user know that they cannot be deleted

INPUT

        -SMS message is received
        -Broadcast Receiver is invoked
        -App opens
        -hyperlink is added to ListView
        -WebView is directed to the hyperlink that was sent

OUTPUT

    WebView will display the appropriate hyperlink

ASSUMPTIONS
- Users' phones are able to receive SMS messages
*/

//imported libraries
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //CONSTANT: Result code for real time permissions
    protected static final int PERMISSIONS_REQUEST_RECEIVE_SMS = 0;
    //ArrayList that holds website names
    protected ArrayList<String> items;
    //Button to delete sites
    protected Button delete;
    //ListView Object
    protected ListView list;
    //WebView object
    protected WebView web;
    //Database to retrieve
    protected DBHandler db;
    //Adapter to set ArrayList
    protected ArrayAdapter adapt;
    //Saved number of entries in database
    protected SharedPreferences check;
    //Website Header
    protected final String http = "http://";
    //Selected item variable
    protected int item = 1;
    //Selected site position
    protected int site = 0;
    //Size of entries - before SMS
    protected int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set check to saved shared preferences
        check = PreferenceManager.getDefaultSharedPreferences(this);
        //Gets Value of size saved - if not available, set size to 3
        size = check.getInt("size", 3);
        //Initializes ArrayList
        items = new ArrayList<>();
        //Checks if Permission is Granted
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        //Checks and asks for SMS Permission
        if (result != PackageManager.PERMISSION_GRANTED) {
            //Requests SMS Permission from user
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECEIVE_SMS},
                    PERMISSIONS_REQUEST_RECEIVE_SMS);
        }
        //Set ListView to activity main
        list = (ListView) findViewById(R.id.list);
        //Set WebView to activity main
        web = (WebView) findViewById(R.id.web);
        //Set delete button to layout
        delete = (Button) findViewById(R.id.delete);
        //Get database handler instance
        db = new DBHandler(this);
        //If database is empty initialize database
        if(db.getCount() == 0){
            //Adds Fontbonne to database
            db.addSite("www.fontbonne.edu");
            //Adds Microsoft to database
            db.addSite("www.microsoft.com/en-us");
            //Adds Amazon to database
            db.addSite("www.amazon.com");
        }
    }

    protected void onResume(){
        super.onResume();
        //Calls the getList method to set ListView from database
        getList();
        //Do not open web browser
        //If new website is added
        web.setWebViewClient(new WebViewClient());
        if(size != db.getCount()){
            //Sets new database size
            size++;
            //Sets selected site list number
            site = db.getCount() - 1;
        }
        //Sets initial website
        web.loadUrl(http + list.getItemAtPosition(site).toString());
        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            //OnClick method for the delete button
            public void onClick(View v) {
                //Deletes the item from the list
                db.deleteSite(item);
                //Adjusts the database size
                size--;
                //Sets the site back to 0
                site = 0;
                //Assigns a number to the website (database)
                item = db.getSiteID(list.getItemAtPosition(site).toString());
                //Loads URL from website
                web.loadUrl("http://" + list.getItemAtPosition(site).toString());
                //Calls method to set ListView from database
                getList();
            }
        });
    }


    //Gets the List from the Database & sets the onItemClickListener for the ListView
    protected void getList(){
        // clear array list
        items.clear();
        //Adds websites to array list
        db.getAllSites(items);
        //Creates instance of array adapter
        adapt = new ArrayAdapter<>(this, R.layout.list_item, items);
        //Sets ListView Adapter
        list.setAdapter(adapt);
        //OnItemClickListener listens for item selection
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Load selected url
                item = db.getSiteID(list.getItemAtPosition(position).toString());
                //Sets the current selection variable
                site = position;
                //Load the selected website
                web.loadUrl("http://" + list.getItemAtPosition(site).toString());
            }
        });
    }

    protected void onDestroy(){
        super.onDestroy();
        // start editor
        SharedPreferences.Editor set = check.edit();
        //Edit the size variable
        set.putInt("size", size);
        //Save shared preference data
        set.apply();
        //Close the Database Handler
        db.close();
    }
}