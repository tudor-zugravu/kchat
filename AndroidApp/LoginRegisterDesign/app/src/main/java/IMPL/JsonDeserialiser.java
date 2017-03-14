package IMPL;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.example.user.kchat01.ContactsActivity;
import com.example.user.kchat01.DataManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
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
    DataManager dm;

    public JsonDeserialiser(String serverResult, String deserializeType,Context context) {
        this.serverResult = serverResult;
        this.context = context;
        dm = new DataManager(this.context);

        try {
            this.jObject = new JSONObject(serverResult);
        } catch (final JSONException e) {
            Log.e("JSON ERROR", "Json parsing error: " + e.getMessage());
            // Toast.makeText(this.context, "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        if(deserializeType.equals("login")){
            loginDeserializer(this.jObject);
        }else if (deserializeType.equals("getcontacts")) {
            contactDeserializer(this.jObject,0);
        }else if (deserializeType.equals("message")) {
            messageList = new ArrayList<>();
        }else if (deserializeType.equals("filterlist")) {
            userFilterDeserializer(this.jObject);
        }else if (deserializeType.equals("getcontactrequests")) {
            contactDeserializer(this.jObject, 1);
        }else if (deserializeType.equals("getcontactinvites")) {
            contactDeserializer(this.jObject, 2);
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
            }
        }

    private void contactDeserializer(JSONObject jobject ,int num){
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
                    int blocked = Integer.parseInt(obj.getString("blocked"));
                    int session = Integer.parseInt(obj.getString("session"));
                    String contactPicture = obj.getString("profile_picture");
                    String contactbiography = obj.getString("biography");
                    dm.insert(contactName,phonenumber);
                    IContacts contact = new Contacts(contactId, timestamp, userId, contactName, email, username, phonenumber, blocked, session, contactPicture);
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
                                        /*
                                        byte[] image = cursor.getBlob(1);

                                         */
                                        byte [] image = getBytes(contactsBitmap);
                                     //   dm.insertContact(contactId,timestamp,Integer.parseInt(userId),contactName,email,username,phonenumber,contactPicture,contactbiography,image);
                                   //     adaptor.addToContactsTable(contactId, requestNum, timestamp, userId, contactName, email, username, phonenumber, blocked, session, contactPicture,contactsBitmap);
                                    //    Log.d("DATABASETEST", adaptor.getContact(contactId).getContactName());
                                    }
                                } catch (InterruptedException e) {
                                } catch (ExecutionException f) {
                                }
                    }
                    if(num==0) {
                        Contacts.contactList.add(contact);
                        // Bitmap profilePicture;
                    }else if (num ==1){
                        Contacts.sentRequests.add(contact);
                    }else if (num ==2){
                        Contacts.receivedRequests.add(contact);
                    }
                }
                Log.d("CALLEDSTATUS", "object size: " + Contacts.contactList.size());
                showData(dm.selectAll());
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

    }
    public void showData(Cursor c){
        while (c.moveToNext()){
            Log.i(c.getString(1), c.getString(2));
            Log.d("DATABASERESULT",c.getString(1));
            Log.d("DATABASERESULT",c.getString(2));
        }
    }

     private void userFilterDeserializer(JSONObject jobject){
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


    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
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