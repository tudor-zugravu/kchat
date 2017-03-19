package IMPL;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.user.kchat01.ChatsActivity;
import com.example.user.kchat01.ContactsActivity;
import com.example.user.kchat01.DataManager;
import com.example.user.kchat01.GroupChatsActivity;
import com.example.user.kchat01.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import API.IContacts;
import API.IGroups;
import API.IMessage;

/**
 * Created by tudor on 2/20/2017.
 */

public class JsonDeserialiser {

    JSONObject jObject;
    String serverResult;
    Context context;
    DataManager dm;

    public JsonDeserialiser(String serverResult, String deserializeType,Context context) {
        this.serverResult = serverResult;
        this.context = context;
        dm = new DataManager(this.context);

        try {
            this.jObject = new JSONObject(serverResult);
        } catch (final JSONException e) {
            Log.e("JSON ERROR", "Json parsing error: " + e.getMessage());
        }
        if(deserializeType.equals("login")){
            loginDeserializer(this.jObject);
        }else if (deserializeType.equals("getcontacts")) {
            contactDeserializer(0);
        }else if (deserializeType.equals("message")) {
            messageDeserialiser(0,"sender_id");
        }else if (deserializeType.equals("filterlist")) {
            userFilterDeserializer();
        }else if (deserializeType.equals("getcontactrequests")) {
            contactDeserializer(1);
        }else if (deserializeType.equals("getcontactinvites")) {
            contactDeserializer(2);
        }else if (deserializeType.equals("chats")) {
            chatDeserialiser();
        }else if (deserializeType.equals("groups")) {
            groupDeserialiser();
        }else if (deserializeType.equals("getgroupcontacts")){
            //groupContactDdeserialiser();
        }else if (deserializeType.equals("groupmessage")) {
            messageDeserialiser(1,"user_id");
        }
    }

    private void loginDeserializer(JSONObject jObject){
        try{
        String id=jObject.getString("user_id");
        String name=jObject.getString("name");
        String email=jObject.getString("email");
        String username=jObject.getString("username");
        String phone_number=jObject.getString("phone_number");
        String blocked=jObject.getString("blocked");
        String session=jObject.getString("session");
        String profile_picture=jObject.getString("profile_picture");
        MasterUser man=new MasterUser(Integer.parseInt(id),name,email,username,phone_number,Integer.parseInt(blocked),Integer.parseInt(session),profile_picture);
        }catch(final JSONException e){
        Log.e("JSON ERROR","Json parsing error: "+e.getMessage());
            }
        }

    private void messageDeserialiser(int type,String value){
        if(ChatsActivity.dataList!=null && type ==0 )ChatsActivity.dataList.clear();
        if(GroupChatsActivity.dataList!=null && type ==1 )GroupChatsActivity.dataList.clear();
        try {
            JSONArray jArr = new JSONArray(this.serverResult);
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject obj = jArr.getJSONObject(i);
                String messageid = obj.getString("message_id");
                String username="";
                if(type==0){
                    username = obj.getString(value);
                }else if(type==1){
                    username = obj.getString(value);
                }
                String message = obj.getString("message");
                String messagetimestamp = obj.getString("timestmp");
                IMessage messageObject = new Message(Integer.parseInt(messageid), Integer.parseInt(username), message, messagetimestamp);//This is used to add actual message
                if(Integer.parseInt(username) == MasterUser.usersId){
                    messageObject.setMe(true);//if the message is sender, set "true". if not, set "false".
                }else{
                    messageObject.setMe(false);//if the message is sender, set "true". if not, set "false".
                }
                if(ChatsActivity.dataList!=null && type ==0 )ChatsActivity.dataList.add(0, messageObject);
                if(GroupChatsActivity.dataList!=null && type ==1 ) {
                    GroupChatsActivity.dataList.add(0, messageObject);
                }
            }
        }catch( final JSONException e){
                Log.e("JSON ERROR", "Json parsing error: " + e.getMessage());
            }
    }

private void groupDeserialiser(){
    Groups.groupList.clear();
    try {
        if(this.serverResult!=null) {
            JSONArray jArr = new JSONArray(this.serverResult);
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject obj = jArr.getJSONObject(i);
                int groupId = Integer.parseInt(obj.getString("group_id")); //check id//
                String groupName = obj.getString("name");//
                String groupPicture = obj.getString("group_picture");//
                String description = obj.getString("description");//
                String message = obj.getString("message");
                String timestamp = obj.getString("timestmp");
                IGroups groups = new Groups(groupName,message,groupId,groupPicture,null);
                Log.d("GROUPSRECEIVED", "object size: " + groupId);
                Log.d("GROUPSRECEIVED", "object size: " + description);
                Groups.groupList.add(groups);
            }
        }
    }catch (JSONException e){
        e.printStackTrace();
    }
}

    private void chatDeserialiser(){
        Contacts.activeChat.clear();
        try {
            if(this.serverResult!=null) {
                JSONArray jArr = new JSONArray(this.serverResult);
                for (int i = 0; i < jArr.length(); i++) {
                    JSONObject obj = jArr.getJSONObject(i);
                    int senderId = Integer.parseInt(obj.getString("senderId")); //check id//
                    String receiverName = obj.getString("receiverName");//
                    String receiverProfilePicture = obj.getString("receiverProfilePicture");//
                    String receiverId = obj.getString("receiverId");//
                    String senderProfilePicture = obj.getString("senderProfilePicture");
                    String message = obj.getString("message");
                    String senderName = obj.getString("senderName");

                    IContacts contact = new Contacts();
                    //save only receiverId , receiver name, receiver profile picture, message, timestamp
                    if(senderId==MasterUser.usersId){
                        contact.setContactId(Integer.parseInt(receiverId));
                        contact.setContactName(receiverName);
                        contact.setContactPicture(receiverProfilePicture);
                        getImage(receiverProfilePicture,receiverId,contact);
                    } else {
                        //save senderId , sender name, sender profile picture
                        contact.setContactId(senderId);
                        contact.setContactName(senderName);
                        contact.setContactPicture(senderProfilePicture);
                        getImage(receiverProfilePicture,receiverId,contact);
                    }
                    Log.d("CALLEDCHAT", "object size: " + receiverName);
                    Log.d("CALLEDCHAT", "object size: " + senderName);

                    contact.setEmail(message); //
                    Contacts.activeChat.add(contact);
                }
                Log.d("CALLEDCHAT", "object size: " + Contacts.activeChat.size());
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private String getImage (String location, String userId,IContacts contact) {
        if (location != null && (!location.equals("null"))) {
            Bitmap contactsBitmap;
            String image = "";
            try {
                String picture_url = "http://188.166.157.62/profile_pictures/" + "profile_picture" + userId + ".jpg";
                String type = "getIcon";
                ProfileIconGetter backgroundasync = new ProfileIconGetter(context, picture_url);
                contactsBitmap = backgroundasync.execute(type).get();
                if (contactsBitmap != null) {
                    contact.setBitmap(contactsBitmap);
                    image = encodeToBase64(contactsBitmap, Bitmap.CompressFormat.JPEG, 100);
                    Log.d("DATABASE", "the client has got a image");
                    Log.d("PROFILE", "image received from the server is :" + image);
                    return image;
                } else {
                    contact.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.human));
                    image = encodeToBase64(contactsBitmap, Bitmap.CompressFormat.JPEG, 100);
                    Log.d("DATABASE", "the client does not have an image");
                    Log.d("PROFILE", "image received from the server is :" + image);
                    return image;
                }
            } catch (InterruptedException e) {
            } catch (ExecutionException f) {
            }
        }
        return null;
    }

    public ArrayList<IContacts> groupContactDdeserialiser(){
        try {
            if(this.serverResult!=null) {
                 ArrayList <IContacts> contactsInChat = new ArrayList<>();
                JSONArray jArr = new JSONArray(this.serverResult);
                for (int i = 0; i < jArr.length(); i++) {
                    JSONObject obj = jArr.getJSONObject(i);
                    String userId = obj.getString("user_id");
                    String contactName = obj.getString("name");
                    String username = obj.getString("username");
                    String contactPicture = obj.getString("profile_picture");
                    IContacts contact = new Contacts(0, null, userId, contactName, null, username, null, contactPicture);
                    getImage(contactPicture,userId,contact);
                    contactsInChat.add(contact);
                }
                Log.d("GROUPFUNCTION", "object size: " + contactsInChat.size());
                return contactsInChat;
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    private void contactDeserializer(int num){
        try {
            if(this.serverResult!=null) {
                Contacts.contactList.clear();
                JSONArray jArr = new JSONArray(this.serverResult);
                for (int i = 0; i < jArr.length(); i++) {
                    JSONObject obj = jArr.getJSONObject(i);
                    int contactId = Integer.parseInt(obj.getString("contact_id"));
                    String timestamp = obj.getString("timestmp");
                    String userId = obj.getString("user_id");
                    String contactName = obj.getString("name");
                    String email = obj.getString("email");
                    String username = obj.getString("username");
                    String phonenumber = obj.getString("phone_number");
                    String contactPicture = obj.getString("profile_picture");
                    String contactbiography = obj.getString("biography");
                    IContacts contact = new Contacts(contactId, timestamp, userId, contactName, email, username, phonenumber, contactPicture);
                    String base64REsult = getImage(contactPicture,userId,contact);
                    if(num==0) {
                        dm.insertContact(contactId,timestamp,Integer.parseInt(userId),contactName,email,username,phonenumber,contactPicture,contactbiography,base64REsult);
                        Contacts.contactList.add(contact);
                    }else if (num ==1){
                        Contacts.sentRequests.add(contact);
                    }else if (num ==2){
                        Contacts.receivedRequests.add(contact);
                    }
                }
                Log.d("CALLEDSTATUS", "object size: " + Contacts.contactList.size());
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

     private void userFilterDeserializer(){
        try {
            if(this.serverResult!=null) {
                JSONArray jArr = new JSONArray(this.serverResult);
                for (int i = 0; i < jArr.length(); i++) {
                    JSONObject obj = jArr.getJSONObject(i);
                    String userId = obj.getString("user_id");
                    String username1 = obj.getString("username");
                    String name1 = obj.getString("name");
                    IContacts contact = new Contacts(userId, username1, name1);
                    Contacts.searchList.add(contact);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

}