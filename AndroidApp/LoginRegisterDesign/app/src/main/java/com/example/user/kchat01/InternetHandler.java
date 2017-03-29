package com.example.user.kchat01;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.github.nkzawa.socketio.client.IO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import API.IMessage;
import IMPL.Contacts;
import IMPL.RESTApi;

/**
 * Created by Tudor Vasile on 3/20/2017.
 */

public class InternetHandler {

    static DataManager dm;

//send messages from post
    public static boolean hasInternetConnection(Context context, int num){
        dm = new DataManager(context);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            if(num ==1){
                if(ContactsActivity.mSocket.connected()!=true){
                    Log.d("Burger","Yellow");
                    ContactsActivity.mSocket.connect();
                    if(ChatsActivity.bufferdList!=null) {
                        dm.getAllBufferedMessages();
                        sendToServer(context);
                        ChatsActivity.bufferdList.clear();
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
                    if(ChatsActivity.bufferdList!=null) {
                        Log.d("MESSI","PART1");
                        dm.getAllBufferedMessages();
                        sendToServer(context);
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
                    if(ChatsActivity.bufferdList!=null) {
                        Log.d("MESSI","PART22");
                        dm.getAllBufferedMessages();
                        sendToServer(context);
                        ChatsActivity.bufferdList.clear();
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

    public static void sendToServer(Context context){
        if(ChatsActivity.bufferdList!=null) {
            JSONArray jsonArray = new JSONArray();

            for (int i=0; i < ChatsActivity.bufferdList.size(); i++) {
                jsonArray.put(getJSONObject(Integer.toString(ChatsActivity.bufferdList.get(i).getSenderId()),
                                            Integer.toString(ChatsActivity.bufferdList.get(i).getReceiverId()),
                                            ChatsActivity.bufferdList.get(i).getMessage(),
                                             ChatsActivity.bufferdList.get(i).getTimestamp()));
            }
            Log.d("MESSI", "reeached here to send");

            for(int i=0; i<ChatsActivity.bufferdList.size();i++){
                Log.d("MESSI","Printed messages are: " + ChatsActivity.bufferdList.get(i).getMessage());
            }
            Log.d("MESSI", "json arraylise is: " + jsonArray.length());

            String type = "bufferSend";
            String login_url = "http://188.166.157.62:3000/bufferUpload";
            ArrayList<String> paramList = new ArrayList<>();
            paramList.add("type");
            paramList.add("contents");
            RESTApi backgroundasync = new RESTApi(context, login_url, paramList);
            Log.d("MESSI", "I send this to ther server:  " + jsonArray.toString());
            backgroundasync.execute(type, "buffer", jsonArray.toString());
        }

    }

    public static JSONObject getJSONObject(String senderId, String receiverId, String message, String type) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("sender", senderId);
            obj.put("receiver", receiverId);
            obj.put("message", message);
            obj.put("messageType", type);
        } catch (JSONException e) {
        }
        return obj;
    }
}
