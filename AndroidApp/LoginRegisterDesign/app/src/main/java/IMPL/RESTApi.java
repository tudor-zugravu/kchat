package IMPL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.user.kchat01.ChatsActivity;
import com.example.user.kchat01.ContactsActivity;
import com.example.user.kchat01.ContactsListActivity;
import com.example.user.kchat01.LoginActivity;
import com.example.user.kchat01.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
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

/**
 * Created by tudor on 2/17/2017.
 */

public class RESTApi extends AsyncTask<String,Void,String> {
    Context context;
    AlertDialog alertDialog;
    String url;
    ArrayList<String> urlPostParams;
    String [] stringParams;
    String type;

    public RESTApi (Context ctx, String url,ArrayList<String> urlPostParams) {
        context = ctx;
        this.url = url;
        this.urlPostParams=urlPostParams;
    }
    //
    @Override
    protected String doInBackground(String... params) {
        this.stringParams=params;
        this.type = params[0];
        String login_url = this.url;

        if(type.equals("getImage")) {
            getBitmapFromURL(this.url);
        }
        if(type.equals("login")||type.equals("register")||type.equals("updateImage")) {
            try {
                URL url = new URL(login_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data2 = "";
                for(int i=0; i<urlPostParams.size();i++){
                    post_data2 = post_data2 + URLEncoder.encode(urlPostParams.get(i),"UTF-8")+"="+URLEncoder.encode(params[i+1],"UTF-8")+"&";
                }
                if(post_data2.endsWith("&")) {
                    post_data2.substring(0, post_data2.length() - 1);
                }

                bufferedWriter.write(post_data2);

                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream(); ///
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

            }catch(MalformedURLException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        //  alertDialog = new AlertDialog.Builder(context).create();
        //  alertDialog.setTitle("Login Status");
    }

    @Override
    protected void onPostExecute(String result) {
        if(result!=null){
            boolean  b = result.startsWith("{");  // true
            MasterUser man = new MasterUser();
            if(b && type.equals("login")){
                Log.d("SERVERRESULT","Sent from the server:" + result);
                result = result.replace("login_sucess", "");
                JsonDeserialiser deserialiser = new JsonDeserialiser(result,this.type);

                if(man.getUsername()!=null) {
                    if(LoginActivity.pref.getAll()!=null) {
                        Log.d("SERVERRESULT","I have reached here to save");
                        LoginActivity.editor.putString("usernamelogin", stringParams[1]); // Storing login
                        LoginActivity.editor.putString("usernamepassword", stringParams[2]); // Storing password
                        LoginActivity.editor.commit(); // commit changes
                    }
                    Intent loginIntent = new Intent(context, ContactsActivity.class);
                    context.startActivity(loginIntent);
                    ((Activity) context).finish();
                }else {
                    Log.d("SERVERRESULT","Cannot Log in");
                }
            }else if (result.contains("fail")){
                Log.d("SERVERRESULT","Cannot Log in");
            }else{
                Log.d("SERVERRESULT","Sent from the server:" + result);
            }

        switch(result){
            case "":
                break;
            case"True":
                break;
            case"fail":
                Log.d("SERVERRESULT","Cannot Log in");
                break;
            case"Username Already Exists":
                break;
            case"Email Already Exists":
                break;
            case "Incorrect Password":
                break;
        }
            Log.d("SERVERRESULT","Sent from the server:" + result);


            CharSequence text ="Hello From server, your username and password is : " +  result;
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }else{
            Log.d("SERVERRRRR" , "Bad Result from the server");
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    public void getBitmapFromURL(String src) {
        try {
            Log.d("PROFILE",src);

            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            resizedBitmap(myBitmap,450,450);
        } catch (FileNotFoundException e) {
            MasterUser man = new MasterUser();
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.human);
            man.setUsersImage(icon);
    } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap resizedBitmap(Bitmap bm, int newHeight, int newWidth) {
                   Log.d("PROFILE","reached here for image");

        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,matrix, false);
        MasterUser man = new MasterUser();
        man.setUsersImage(resizedBitmap);
        Log.d("PROFILE","reached here for image 22");
        return resizedBitmap;
    }
}