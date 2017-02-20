package IMPL;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tudor on 2/20/2017.
 */

public class JsonDeserialiser {

    ArrayList<HashMap<String, String>> messageList;

    public JsonDeserialiser(String serverREsult){
        messageList = new ArrayList<>();
        try {
        JSONObject jsonObj = new JSONObject(serverREsult);

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
