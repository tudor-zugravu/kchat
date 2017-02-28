package IMPL;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by tudor on 2/20/2017.
 */

public class JsonDeserialiser {

    ArrayList<HashMap<String, String>> messageList;



    public JsonDeserialiser(String serverResult, String deserializeType) {


        try {
        JSONObject jObject = new JSONObject(serverResult);
        Iterator<String> keys = jObject.keys();
            //JSONArray contacts = jObject.getJSONArray("");

                String id = jObject.getString("user_id");
                String name = jObject.getString("name");
                String email = jObject.getString("email");
                String username = jObject.getString("username");
                String password = jObject.getString("password");
                String phone_number = jObject.getString("phone_number");
                String blocked = jObject.getString("blocked");
                String session = jObject.getString("session");
                String profile_picture = jObject.getString("profile_picture");

                MasterUser man = new MasterUser(Integer.parseInt(id),name,email,username,phone_number,Integer.parseInt(blocked),Integer.parseInt(session),profile_picture);

                Log.d("DESERIALISE VALUE", id);
                Log.d("DESERIALISE VALUE", name);
                Log.d("DESERIALISE VALUE", email);
                Log.d("DESERIALISE VALUE", username);
                Log.d("DESERIALISE VALUE", password);
                Log.d("DESERIALISE VALUE", phone_number);
                Log.d("DESERIALISE VALUE", blocked);
                Log.d("DESERIALISE VALUE", session);
                Log.d("DESERIALISE VALUE", profile_picture);
                // String gender = c.getString("gender");

                // Phone node is JSON Object
          //      JSONObject phone = c.getJSONObject("phone");
                //          String mobile = phone.getString("mobile");
//            String office = phone.getString("office");


                while( keys.hasNext() ) {
            String key = keys.next();
            String value = jObject.getString(key);
           // Log.d("DESERIALISE VALUE", value);
        }

        } catch (final JSONException e) {
            Log.e("JSON ERROR", "Json parsing error: " + e.getMessage());
            // Toast.makeText(this.context, "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        if (deserializeType.equals("message")) {
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