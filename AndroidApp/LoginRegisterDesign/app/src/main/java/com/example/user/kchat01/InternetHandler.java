package com.example.user.kchat01;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.github.nkzawa.socketio.client.IO;

import java.net.URISyntaxException;

import IMPL.Contacts;

/**
 * Created by Tudor Vasile on 3/20/2017.
 */

public class InternetHandler {

//send messages from post
    public static boolean hasInternetConnection(Context context, int num){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            if(num ==1){
                if(ContactsActivity.mSocket.connected()!=true){
                    Log.d("Burger","Yellow");
                    ContactsActivity.mSocket.connect();
                }

            }
            return true;
        }

        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            if(num ==1){
                if(ContactsActivity.mSocket.connected()!=true){
                    Log.d("Burger","Yellow");
                    ContactsActivity.mSocket.connect();
                }
            }
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            if(num ==1){
                if(ContactsActivity.mSocket.connected()!=true){
                    Log.d("Burger","Yellow");
                    ContactsActivity.mSocket.connect();
                }
            }
            return true;
        }

        if(num ==0 ) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("No Internet detected please connect to the internet to continue")
                    .setNegativeButton("ok", null).create().show();
        }

        if(num ==1) {
            if(ContactsActivity.mSocket.connected()==true){
                Log.d("Burger","Yellow");
                ContactsActivity.mSocket.disconnect();
            }
        }

        return false;
    }
}
