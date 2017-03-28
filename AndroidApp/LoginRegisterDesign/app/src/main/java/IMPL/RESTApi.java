package IMPL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.user.kchat01.ContactsActivity;
import com.example.user.kchat01.CustomActivity;
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

import API.IContacts;

//import com.example.user.kchat01.ContactsListActivity;

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
    int num;

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
            getBitmapFromURL(this.url,450,450);
        }

        if(type.equals("getIcon")) {
            getBitmapFromURL(this.url,50,50);
        }
        if(type.equals("login")||type.equals("register")||type.equals("updateImage")||type.equals("getcontacts")||type.equals("profileUpdate")||type.equals("deleteAccount")||type.equals("profileUpdate")||type.equals("bufferSend")) {
            try {
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                Log.d("REGISTER REACH", "reached here for register part 1");
                Log.d("REGISTER REACH", "reached here for register part 2" + type);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data2 = "";
                for(int i=0; i<urlPostParams.size();i++){
                    post_data2 = post_data2 + URLEncoder.encode(urlPostParams.get(i),"UTF-8")+"="+URLEncoder.encode(params[i+1],"UTF-8")+"&";
                }
                if(post_data2.endsWith("&")) {
                    post_data2.substring(0, post_data2.length() - 1);
                }
                Log.d("REGISTER REACH", post_data2);
                Log.d("REGISTER REACH", login_url);

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
    }

    @Override
    protected void onPostExecute(String result) {

        if(type.equals("bufferSend")){
            if (result == null || result.equals("") || result.equals("null")) {
                Log.d("MESSI","got a result from the server");
            }
        }

        if(type.equals("login")) {
            if (result == null || result.equals("") || result.equals("null")) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                builder.setMessage("No result, server is offline").setNegativeButton("ok", null).create().show();
                return;
            }
        }

        if(result!=null){
            boolean  b = result.startsWith("{");  // true
            MasterUser man = new MasterUser();
            if(b && type.equals("login")){
                Log.d("LOGINRESULT","Profile from the server:" + result);

                if(result.equals("{\"status\":\"failed\"}")){
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                    builder.setMessage("Incorrect credintials please try again").setNegativeButton("ok", null).create().show();
                    return;
                }

                result = result.replace("login_sucess", "");
                JsonDeserialiser deserialiser = new JsonDeserialiser(result,this.type,context);

                if(man.getUsername()!=null) {
                    if(LoginActivity.pref.getAll()!=null) {
                        Log.d("LOGINRESULT","I have reached here to save");
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
            }

            if(type.equals("register")) {
                Log.d("REGISTER","register  Sent from the server:" + result);
                if(result.equals("duplicate")){
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                    builder.setMessage("Could not create an account username already exists").setNegativeButton("ok", null).create().show();
                }else if(result.contains("success")){
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setMessage("Account has been successfully created you may now log in.").setNegativeButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent logoutIntent = new Intent(context, LoginActivity.class);
                            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(logoutIntent);
                            ((Activity) context).finish();
                        }
                    }).create().show();
                    }

                }
        }else{
            Log.d("SERVERRRRR" , "Bad Result from the server");
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    public void getBitmapFromURL(String src,int num1, int num2) {
        try {

            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            resizedBitmap(myBitmap,num1,num2);
        } catch (FileNotFoundException e) {
            MasterUser man = new MasterUser();
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.human);
            //man.setUsersImage(icon);
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
        return resizedBitmap;
    }
}