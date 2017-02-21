package IMPL;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tudor on 2/20/2017.
 */

public class InfoRetreiver extends AsyncTask <String, Void, String>{

    String url;
    Context context;
    ArrayList<HashMap<String, String>> contactList;

    public  InfoRetreiver(String url,Context context){
        this.url = url;
        this.context = context;
    }
    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        // String login_url = "http://192.168.1.6/login.php";
        String login_url = this.url;

        if(type.equals("weather")) {
            try {
                Log.d("SERVERCONNECT","Reached here 1");

                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line="";

                while((line = bufferedReader.readLine())!= null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;

            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.i("Website data", result);
        try {
            contactList = new ArrayList<>();
            JSONObject jsonObj = new JSONObject(result);

            // Getting JSON Array node
            JSONArray contacts = jsonObj.getJSONArray("contacts");

            // looping through All Contacts
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject c = contacts.getJSONObject(i);

                String id = c.getString("id");
                String name = c.getString("name");
                String email = c.getString("email");
                String address = c.getString("address");
                String gender = c.getString("gender");

                // Phone node is JSON Object
                JSONObject phone = c.getJSONObject("phone");
                String mobile = phone.getString("mobile");
                String home = phone.getString("home");
                String office = phone.getString("office");

                // tmp hash map for single contact
                HashMap<String, String> contact = new HashMap<>();

                // adding each child node to HashMap key => value
                contact.put("id", id);
                contact.put("name", name);
                contact.put("email", email);
                contact.put("mobile", mobile);

                // adding contact to contact list
                contactList.add(contact);
                Log.i("JSON Parser", Integer.toString(contactList.size()));
                Log.i("JSON Parser", contact.get("name"));
            }
            Log.i("JSON Parser", contactList.get(0).get("name"));

        } catch (final JSONException e) {
            Log.e("JSON ERROR", "Json parsing error: " + e.getMessage());
                    Toast.makeText(this.context, "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
