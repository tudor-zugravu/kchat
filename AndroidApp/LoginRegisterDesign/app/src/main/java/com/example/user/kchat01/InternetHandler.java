package com.example.user.kchat01;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.github.nkzawa.socketio.client.IO;

import org.json.JSONArray;

import java.net.URISyntaxException;
import java.util.ArrayList;

import API.IMessage;
import IMPL.Contacts;
import IMPL.RESTApi;

/**
 * Created by Tudor Vasile on 3/20/2017.
 */

public class InternetHandler {
    public static ArrayList<IMessage> bufferdList;
    static DataManager dm;

//send messages from post
    public static boolean hasInternetConnection(Context context, int num){
        bufferdList = new ArrayList<>();
        dm = new DataManager(context);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            if(num ==1){
                if(ContactsActivity.mSocket.connected()!=true){
                    Log.d("Burger","Yellow");
                    ContactsActivity.mSocket.connect();
                    if(bufferdList!=null) {
                        dm.getAllBufferedMessages();
                        sendToServer(context, bufferdList);
                        bufferdList.clear();
                    }
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
                    if(bufferdList!=null) {
                        Log.d("MESSI","PART1");
                        dm.getAllBufferedMessages();
                        sendToServer(context, bufferdList);
                        bufferdList.clear();
                    }
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
                    if(bufferdList!=null) {
                        Log.d("MESSI","PART22");
                        dm.getAllBufferedMessages();
                        sendToServer(context, bufferdList);
                        bufferdList.clear();
                    }
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

    public static void sendToServer(Context context, ArrayList<IMessage> bufferdList){
        JSONArray jsonAraay = new JSONArray(bufferdList);
        Log.d("MESSI","reeached here to send");

        String type = "bufferSend";
        String login_url = "http://188.166.157.62:3000/bufferUpload";
        ArrayList<String> paramList = new ArrayList<>();
        paramList.add("type");
        paramList.add("contents");
        RESTApi backgroundasync = new RESTApi(context, login_url, paramList);
        backgroundasync.execute(type, "buffer", jsonAraay.toString());
    }
}
