package com.example.user.kchat01;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
    public static boolean hasInternetConnection(Context context, int num){
        dm = new DataManager(context);
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            if(num ==1){
                if(ContactsActivity.mSocket.connected()!=true){
                    Log.d("Burger","Yellow1" + "not connected");
                    ContactsActivity.mSocket.connect();
                }
                if(ContactsActivity.mSocket.connected()==true){
                    Log.d("Burger","Yellow22" + "it is connected");
                    if(ContactsActivity.bufferdList!=null){
                        Log.d("Burger","Yellow22" + "list is not null");
                        if(dm.getAllBufferedMessages().getCount()>0){
                            sendToServer(context);
                            ContactsActivity.bufferdList.clear();
                        }

                    }else if (ContactsActivity.bufferdList==null){
                        Log.d("MESSI","null list");
                    }
                }
            }
            return true;
        }

        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            if(num ==1){
                if(ContactsActivity.mSocket.connected()!=true){
                    Log.d("Burger","Yellow1" + "not connected");
                    ContactsActivity.mSocket.connect();
                }
                if(ContactsActivity.mSocket.connected()==true){
                    Log.d("Burger","Yellow22" + "it is connected");
                    if(ContactsActivity.bufferdList!=null){
                        Log.d("Burger","Yellow22" + "list is not null");
                        if(dm.getAllBufferedMessages().getCount()>0){
                            sendToServer(context);
                            ContactsActivity.bufferdList.clear();
                        }

                    }else if (ContactsActivity.bufferdList==null){
                        Log.d("MESSI","null list");
                    }
                }
            }
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            if(num ==1){
                if(ContactsActivity.mSocket.connected()!=true){
                    Log.d("Burger","Yellow1" + "not connected");
                    ContactsActivity.mSocket.connect();
                }
                if(ContactsActivity.mSocket.connected()==true){
                    Log.d("Burger","Yellow22" + "it is connected");
                    if(ContactsActivity.bufferdList!=null){
                        Log.d("Burger","Yellow22" + "list is not null");
                        if(dm.getAllBufferedMessages().getCount()>0){
                            sendToServer(context);
                            ContactsActivity.bufferdList.clear();
                        }

                    }else if (ContactsActivity.bufferdList==null){
                        Log.d("MESSI","null list");
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
                Log.d("Burger","Yellow4");
                ContactsActivity.mSocket.disconnect();
            }
        }
        return false;
    }

    public static void sendToServer(Context context){
        if(ContactsActivity.bufferdList!=null) {
            JSONArray jsonArray = new JSONArray();

            for (int i=0; i < ContactsActivity.bufferdList.size(); i++) {
                jsonArray.put(getJSONObject(Integer.toString(ContactsActivity.bufferdList.get(i).getMessageId()),
                                            Integer.toString(ContactsActivity.bufferdList.get(i).getSenderId()),
                                            ContactsActivity.bufferdList.get(i).getMessage(),
                        ContactsActivity.bufferdList.get(i).getTimestamp()));
            }

            String type = "bufferSend";
            String login_url = "http://188.166.157.62:3000/bufferUpload";
            ArrayList<String> paramList = new ArrayList<>();
            paramList.add("type");
            paramList.add("messages");
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
