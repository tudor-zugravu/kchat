package IMPL;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import API.IContacts;

/**
 * Created by tudor on 2/20/2017.
 */

public class JsonDeserialiser {

    ArrayList<HashMap<String, String>> messageList;

    JSONObject jObject;

    public JsonDeserialiser(String serverResult, String deserializeType) {
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
            JSONArray jArr = jobject.getJSONArray("");
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject obj = jArr.getJSONObject(i);
                 int contactId = Integer.parseInt(obj.getString("contact_id"));
                 int requestNum=Integer.parseInt(obj.getString("request"));
                 String timestamp= obj.getString("timestamp");
                 String userId= obj.getString("user_id");
                 String contactName= obj.getString("name");
                 String email= obj.getString("email");
                 String username= obj.getString("username");
                 String phonenumber= obj.getString("phone_number");
                 int blocked=Integer.parseInt(obj.getString("blocked"));
                 int session=Integer.parseInt(obj.getString("session"));
                 String contactPicture= obj.getString("profile_picture");
                if(contactPicture!=null){
                    //make a rest call to get image?
                }
                IContacts contact = new Contacts(contactId,requestNum,timestamp,userId,contactName,email,username,phonenumber,blocked,session,contactPicture);
                Contacts.contactList.add(contact);
                // Bitmap profilePicture;
            }
        }catch (JSONException e){

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