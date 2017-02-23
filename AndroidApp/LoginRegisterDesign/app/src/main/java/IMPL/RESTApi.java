package IMPL;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.user.kchat01.ContactsListActivity;

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

/**
 * Created by tudor on 2/17/2017.
 */

public class RESTApi extends AsyncTask<String,Void,String> {
    Context context;
    AlertDialog alertDialog;
    String url;
    ArrayList<String> urlPostParams;
    String [] stringParams;

    public RESTApi (Context ctx, String url,ArrayList<String> urlPostParams) {
        context = ctx;
        this.url = url;
        this.urlPostParams=urlPostParams;
    }
    //
    @Override
    protected String doInBackground(String... params) {
        this.stringParams=params;
        String type = params[0];
        String login_url = this.url;

        if(type.equals("login")||type.equals("register")) {
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
            Log.d("SERVERRRRR",result + "Sent from the server");
            CharSequence text ="Hello From server, your username and password is : " +  result;
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }else{
            Log.d("SERVERRRRR" , "Bad Result from the server");
        }

        Intent loginIntent = new Intent(context, ContactsListActivity.class);
        context.startActivity(loginIntent);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}