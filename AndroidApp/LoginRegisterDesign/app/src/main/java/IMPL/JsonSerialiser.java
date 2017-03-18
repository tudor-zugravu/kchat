package IMPL;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import API.IMessage;

/**
 * Created by Tudor Vasile on 2/25/2017.
 */

public class JsonSerialiser {
    JSONObject json;
    Bundle bundle;

    public JsonSerialiser(){
        json = new JSONObject();
        bundle = new Bundle();
    }

    public String serialiseMessage(IMessage message, String groupname){
        try {
            json.put("type", "sentmessage");
            json.put("sender", message.getSenderId());
            json.put("groupId", message.getGroupId());
            json.put("receiver", message.getReceiverId());
            json.put("message", message.getMessage());
            json.put("timestamp", message.getTimestamp().toString());
            json.put("groupname", groupname);
        }catch (JSONException e){
            Log.e("Json Exception", "Cannot parse to json");
        }
        bundle.putString("json", json.toString());
        return this.json.toString();
    }


    public String serialiseProfileImage(String usersid, String base64Image){
        try {
            json.put("type", "profileImageChange");
            json.put("sender", usersid);
            json.put("usersImage", base64Image);
        }catch (JSONException e){
            Log.e("Json Exception", "Cannot parse to json");
        }
        bundle.putString("json", json.toString());
        return this.json.toString();
    }

    public String serialiseGroupRequest(ArrayList<String> usernames,String groupChatId){
        try {
            json.put("type", "grouprequest");
            json.put("group_id", groupChatId);
            for(int i=0; i<usernames.size();i++){
                json.put("user", usernames.get(i));
            }
        }catch (JSONException e){
            Log.e("Json Exception", "Cannot parse to json");
        }
        bundle.putString("json", json.toString());
        return null;
    }

    public String serialiseAddUserToGroupChat(String user, String groupChatId){
        try {
            json.put("type", "addusertochat");
            json.put("user", user);
            json.put("add_group", groupChatId);
        }catch (JSONException e){
            Log.e("Json Exception", "Cannot parse to json");
        }
        bundle.putString("json", json.toString());
        return null;
    }

    public String serialiseContactRequest(String myUsername,String contactsUsername){
        try {
            json.put("type", "request");
            json.put("requester", myUsername);
            json.put("contactor", contactsUsername);
        }catch (JSONException e){
            Log.e("Json Exception", "Cannot parse to json");
        }
        bundle.putString("json", json.toString());
        return null;
    }

}
