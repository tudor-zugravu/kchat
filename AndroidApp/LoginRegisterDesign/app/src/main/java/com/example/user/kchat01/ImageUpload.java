package com.example.user.kchat01;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.Manifest;
import android.widget.Toast;

import org.json.JSONObject;

import IMPL.JsonSerialiser;
import IMPL.MasterUser;
import IMPL.RESTApi;


public class ImageUpload extends AppCompatActivity {

    ImageView camera, gallery, upload,canvas;

    static final Integer WRITE_EXST = 0x3;
    static final Integer READ_EXST = 0x4;
    static final Integer CAMERA = 0x5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        camera = (ImageView) findViewById(R.id.iVCamera);
        gallery = (ImageView) findViewById(R.id.iVGallery);
        upload = (ImageView) findViewById(R.id.iVUpload);
        canvas = (ImageView) findViewById(R.id.canvas);
        camera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                askForPermission(Manifest.permission.CAMERA,CAMERA);
                Activity activity = (Activity)v.getContext();
                takePhoto(activity, 150);
                //startActivityForResult(intent,SELECTED_PICTURE);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (Activity) v.getContext();
                showFileChooser(activity, 100);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(canvas.getDrawable()==null){
                return;
            }else{
                Bitmap bitmap = ((BitmapDrawable)canvas.getDrawable()).getBitmap();
                String codedImage = getStringImage(bitmap);
                JsonSerialiser imageSerialiser = new JsonSerialiser();
                MasterUser man = new MasterUser();
                String imagetosend = imageSerialiser.serialiseProfileImage(man.getuserId(),codedImage);
                String type = "updateImage";
                String login_url = "http://188.166.157.62:3000/imageupload";
                ArrayList<String> paramList= new ArrayList<>();
                paramList.add("request");
                paramList.add("json");
                RESTApi backgroundasync = new RESTApi(ImageUpload.this,login_url,paramList);
                backgroundasync.execute(type, "profileImageChange", imagetosend);
             }
            }
        });
    }

    public static void takePhoto(Activity activity, int requestCode){
        activity.startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), requestCode);
    }
    private static void showFileChooser(Activity activity, int requestCode){ // shows the gallery to the user where he can select the image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                canvas.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 150 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            canvas.setImageBitmap(bitmap);
        } else if (requestCode == 200 && resultCode == RESULT_OK) {
            Uri filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                canvas.setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace();
            }
        }else if (requestCode == 250 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            canvas.setImageBitmap(bitmap);
        }
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(ImageUpload.this, permission) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ImageUpload.this, permission)) {
                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(ImageUpload.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(ImageUpload.this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    private static String getStringImage(Bitmap bmp){//encoding the image to base 64 string  via compression
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte []imageBytes = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {
                //Location
                case 1:
                    break;
                //Call
                case 2:
                    break;
                //Write external Storage
                case 3:
                    break;
                //Read External Storage
                case 4:
                    Intent imageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(imageIntent, 11);
                    break;
                //Camera
                case 5:
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, 12);
                    }
                    break;
            }
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }



}



//
//
//    private static boolean bitmapProfileChecker(Context context){
//
//        if(profileOption.getDrawable() == null){
//            Drawable myDrawable = context.getResources().getDrawable(R.drawable.navybluewallpaper3);
//            // Bitmap bitmap= ((BitmapDrawable) myDrawable).getBitmap();
//            return true;
//        }
//        return false;
//    }
//
//    private static boolean bitmapBackgroundChecker(Context context){
//
//        if(backgroundOption.getDrawable() == null){
//            Drawable myDrawable = context.getResources().getDrawable(R.drawable.navybluewallpaper3);
//            // Bitmap bitmap = ((BitmapDrawable) myDrawable).getBitmap();
//            return true;
//        }
//        return false;
//    }
//
//