package IMPL;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.example.user.kchat01.ContactsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import API.IContacts;

/**
 * Created by tudor on 2/20/2017.
 */

public class JsonDeserialiser {

    ArrayList<HashMap<String, String>> messageList;

    JSONObject jObject;
    String serverResult;
    Context context;

    public JsonDeserialiser(String serverResult, String deserializeType,Context context) {
        this.serverResult = serverResult;
        this.context = context;
        try {
            this.jObject = new JSONObject(serverResult);
            Iterator<String> keys = jObject.keys();

        } catch (final JSONException e) {
            Log.e("JSON ERROR", "Json parsing error: " + e.getMessage());
            // Toast.makeText(this.context, "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        if(deserializeType.equals("login")){
            loginDeserializer(this.jObject);
        }else if (deserializeType.equals("getcontacts")) {

            contactDeserializer(this.jObject);
        }else if (deserializeType.equals("message")) {

            messageList = new ArrayList<>();
            try {
                JSONObject jsonObj = new JSONObject(serverResult);
                // Getting JSON Array node
                JSONArray contacts = jsonObj.getJSONArray("contacts");
                // looping through All Contacts
                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject c = contacts.getJSONObject(i);
                    String id = c.getString("id");
                    String name = c.getString("name");
                    String email = c.getString("email");
                    String address = c.getString("address");
                    // String gender = c.getString("gender");

                    // Phone node is JSON Object
                    JSONObject phone = c.getJSONObject("phone");
                    //          String mobile = phone.getString("mobile");
//            String office = phone.getString("office");

                    // tmp hash map for single contact
                    HashMap<String, String> contact = new HashMap<>();
                    // adding each child node to HashMap key => value
                    contact.put("id", id);
                    contact.put("name", name);
                    contact.put("email", email);
                    //   contact.put("mobile", mobile);

                    // adding contact to contact list
                    messageList.add(contact);
                    Log.i("JSON Parser", Integer.toString(messageList.size()));
                    Log.i("JSON Parser", contact.get("name"));
                }
                Log.i("JSON Parser", messageList.get(0).get("name"));

            } catch (final JSONException e) {
                Log.e("JSON ERROR", "Json parsing error: " + e.getMessage());
                // Toast.makeText(this.context, "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loginDeserializer(JSONObject jObject){
        try{
        String id=jObject.getString("user_id");
        String name=jObject.getString("name");
        String email=jObject.getString("email");
        String username=jObject.getString("username");
        String password=jObject.getString("password");
        String phone_number=jObject.getString("phone_number");
        String blocked=jObject.getString("blocked");
        String session=jObject.getString("session");
        String profile_picture=jObject.getString("profile_picture");
        MasterUser man=new MasterUser(Integer.parseInt(id),name,email,username,phone_number,Integer.parseInt(blocked),Integer.parseInt(session),profile_picture);
        }catch(final JSONException e){
        Log.e("JSON ERROR","Json parsing error: "+e.getMessage());
        // Toast.makeText(this.context, "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    private void contactDeserializer(JSONObject jobject){
        try {
            if(this.serverResult!=null) {
                Contacts.contactList.clear();
                JSONArray jArr = new JSONArray(this.serverResult);
                for (int i = 0; i < jArr.length(); i++) {
                    JSONObject obj = jArr.getJSONObject(i);
                    int contactId = Integer.parseInt(obj.getString("contact_id"));
                    int requestNum = Integer.parseInt(obj.getString("request"));
                    String timestamp = obj.getString("timestmp");
                    String userId = obj.getString("user_id");
                    String contactName = obj.getString("name");
                    String email = obj.getString("email");
                    String username = obj.getString("username");
                    String password = obj.getString("password");
                    String phonenumber = obj.getString("phone_number");
                    int blocked = Integer.parseInt(obj.getString("blocked"));
                    int session = Integer.parseInt(obj.getString("session"));
                    String contactPicture = obj.getString("profile_picture");
                    IContacts contact = new Contacts(contactId, requestNum, timestamp, userId, contactName, email, username, phonenumber, blocked, session, contactPicture);
                    if (contactPicture != null && (!contactPicture.equals("null"))) {
                        //make a rest call to get image?
                        Bitmap contactsBitmap;
                                try {
                                    String picture_url = "http://188.166.157.62/profile_pictures/" + "profile_picture" + userId + ".jpg";
                                    String type = "getIcon";
                                    ProfileIconGetter backgroundasync = new ProfileIconGetter(context, picture_url);
                                    contactsBitmap = backgroundasync.execute(type).get();
                                    if (contactsBitmap != null){
                                        Log.d("PROFILE","NULL BITMAP FROM THE SERVER");
                                        contact.setBitmap(contactsBitmap);
                                     //   DatabaseAdaptor adaptor = new DatabaseAdaptor(context);
                                   //     adaptor.addToContactsTable(contactId, requestNum, timestamp, userId, contactName, email, username, phonenumber, blocked, session, contactPicture,contactsBitmap);
                                    //    Log.d("DATABASETEST", adaptor.getContact(contactId).getContactName());
                                    }
                                } catch (InterruptedException e) {
                                } catch (ExecutionException f) {
                                }
                    }
                    Log.d("CHECKER",contact.getUserId());
                    Log.d("CHECKER",contact.getContactName());
                    Log.d("CHECKER",contact.getContactProfile());
                    Log.d("CHECKER",contact.getPhoneNumber());
                    Log.d("CHECKER",contact.getTimestamp());
                    Log.d("CHECKER",contact.getEmail());
                    Log.d("CHECKER",Integer.toString(contact.getSessionNum()));
                    Log.d("CHECKER",contact.getContactProfile());
                    Log.d("PROFILE","reached here for contacts image PART 3");

                    Contacts.contactList.add(contact);
                    // Bitmap profilePicture;
                }
                Log.d("CALLEDSTATUS", "object size: " + Contacts.contactList.size());

            }
        }catch (JSONException e){
            e.printStackTrace();
        }

    }
}

//
//    Bundle bundle = getBundleFromIntentOrWhaterver();
//    JSONObject json = null;
//try {
//        json = new JSONObject(bundle.getString("json"));
//        String key = json.getString("key");
//        } catch (JSONException e) {
//        e.printStackTrace();
//        }


//JSONArray contacts = jObject.getJSONArray("");
// String gender = c.getString("gender");
// Phone node is JSON Object
//      JSONObject phone = c.getJSONObject("phone");
//          String mobile = phone.getString("mobile");
//            String office = phone.getString("office");
//                while( keys.hasNext() ) {
//            String key = keys.next();
//            String value = jObject.getString(key);
// Log.d("DESERIALISE VALUE", value);