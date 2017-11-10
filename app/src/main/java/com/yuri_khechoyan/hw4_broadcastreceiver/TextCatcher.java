package com.yuri_khechoyan.hw4_broadcastreceiver;

/**
 * Created by Yuri Khechoyan on 3/2/2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class TextCatcher extends BroadcastReceiver {
    //Creates database handler
    protected DBHandler db;
    //Website name
    protected String address;


    //Empty constructor
    public TextCatcher() {
    }

    //When SMS is received, user presses the link & the link is dded to the list
    //(ArrayList & WebView inside of app is directed to that website)
    @Override @Deprecated
    public void onReceive(final Context context, final Intent intent) {
        //Creates bundle in order to save received SMS hyperlink
        Bundle bundle = intent.getExtras();
        // create instance of database handler
        db = new DBHandler(context);
        // if bundle is not null
        if (bundle != null) {
            //Get SMS data
            Object[] pdus = (Object[]) bundle.get("pdus");
            //IF PDUS IS not NULL
            if (pdus != null) {
                //Create message array
                SmsMessage[] messages = new SmsMessage[pdus.length];
                //Loop to get all messages
                for (int i = 0; i < messages.length; i++) {
                    //If SDK is above API level 23
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // get pdu format
                        String format = bundle.getString("format");
                        //Receive the message
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                        //If SDK is below API level 23
                    } else {
                        //Receive the message
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }
                    //Convert hyperlink text to String
                    String message = messages[i].getMessageBody();
                    //Convert ends of http protocols to Strings
                    if (message.contains(".com") || message.contains(".net") ||
                            // if message contains website
                            message.contains(".edu") || message.contains(".org")) {
                        //parse message by space
                        String parse[] = message.split(" ");

                        //Parse the hyperlink text in order to set proper http:// protocol
                        //parse out every part of the hyperlink
                        for (String wrd : parse) {
                            if (wrd.contains(".com") || wrd.contains(".net") ||
                                    // if any word from message is website
                                    wrd.contains(".edu") || wrd.contains(".org")) {
                                //Does not contain '@'
                                if(!wrd.contains("@")) {

                                    if(wrd.contains("http://")){
                                        //remove http:// protocol
                                        address = wrd.replaceAll("http://", "");
                                    }
                                    else if(wrd.contains("https://")){
                                        //remove https:// protocol
                                        address = wrd.replaceAll("https://", "");
                                    }
                                    //otherwise, don't change
                                    else{
                                        address = wrd;
                                    }
                                    //Adds new website to database
                                    db.addSite(address.trim());
                                }
                            }
                        }
                        // close database manager
                        db.close();
                        //Create intent to open main activity
                        Intent start = new Intent(context, MainActivity.class);
                        // Set Flag to start Task
                        start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // start activity
                        context.startActivity(start);
                    }
                }
            }
        }
    }
}
