package IMPL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.user.kchat01.ContactsActivity;
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

/**
 * Created by tudor on 2/17/2017.
 */

public class ProfileIconGetter extends AsyncTask<String,Void,Bitmap> {
    Context context;
    String url;
    String [] stringParams;
    String type;

    public ProfileIconGetter(Context ctx, String url) {
        context = ctx;
        this.url = url;
    }
    //
    @Override
    protected Bitmap doInBackground(String... params) {

        this.stringParams=params;
        this.type = params[0];

        if(type.equals("getIcon")) {
            try {
                URL url = new URL(this.url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);

                int width = myBitmap.getWidth();
                int height = myBitmap.getHeight();
                float scaleWidth = ((float) 50) / width;
                float scaleHeight = ((float) 50) / height;
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);
                Bitmap resizedBitmap = Bitmap.createBitmap(myBitmap, 0, 0, width, height,matrix, false);
                return resizedBitmap;
            } catch (FileNotFoundException e) {
                Log.d("PROFILE","file not found for PART 10    " + this.url);
                Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.human);
                return icon;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
    }


    @Override
    protected void onPostExecute(Bitmap result) {
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}