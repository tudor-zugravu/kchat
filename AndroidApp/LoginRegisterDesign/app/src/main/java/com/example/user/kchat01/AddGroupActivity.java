package com.example.user.kchat01;

import android.Manifest;
import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Socket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import API.IContacts;
import API.IGroups;
import IMPL.Contacts;
import IMPL.Groups;
import IMPL.JsonDeserialiser;
import IMPL.MasterUser;
import IMPL.RESTApi;

/**
 * Created by user on 22/02/2017.
 */

/* This is main activity to create adding group page */
    // User list checked to add to the group is stored in the variable of "checkedString".

public class AddGroupActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolbarTitle, textViewDone; //"textViewDone" is clickable tv on toolbar
    private EditText editTextGroupNmame, editTextDescription;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private AddGroupAdapter adapter;
    private ArrayList<IContacts> memberList;
    private StringBuffer checkedString=null;
    private Socket mSocket;
    private Activity activity;
    ArrayList<Integer> usersId = new ArrayList<>();
    ImageView camera, gallery, canvas;
    Bitmap bitmap;
    DataManager dm;

    static final Integer WRITE_EXST = 0x3;
    static final Integer READ_EXST = 0x4;
    static final Integer CAMERA = 0x5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dm = new DataManager(AddGroupActivity.this);

        setContentView(R.layout.activity_add_group);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        // apply toolbar title
        toolbarTitle.setText("Add Group");
        toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));

        //get contactList Here
        MasterUser man = new MasterUser();
        if(dm.selectAllContacts().getCount()>0){
            dm.selectAllContacts();
        }else {
            try {
                Log.d("CALLEDSTATUS", "i made a rest request");
                String type2 = "getcontacts";
                String contacts_url = "http://188.166.157.62:3000/contacts";
                ArrayList<String> paramList2 = new ArrayList<>();
                paramList2.add("userId");
                RESTApi backgroundasync2 = new RESTApi(AddGroupActivity.this, contacts_url, paramList2);
                String result2 = backgroundasync2.execute(type2, man.getuserId()).get();
                JsonDeserialiser deserialiser = new JsonDeserialiser(result2, "getcontacts", AddGroupActivity.this);

            } catch (InterruptedException e) {
            } catch (ExecutionException f) {
            }
        }
        adapter = new AddGroupAdapter(AddGroupActivity.this, Contacts.contactList);

        textViewDone = (TextView) findViewById(R.id.textViewDone);

        //group image
        camera = (ImageView) findViewById(R.id.iVCamera);
        gallery = (ImageView) findViewById(R.id.iVGallery);
        canvas = (ImageView) findViewById(R.id.canvas);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AddGroupActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    askForPermission(Manifest.permission.CAMERA, CAMERA);
                } else {
                    Toast.makeText(AddGroupActivity.this, "" + Manifest.permission.CAMERA + " is already granted.", Toast.LENGTH_SHORT).show();

                    if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT){
                        activity = (Activity) ((ContextWrapper) v.getContext()).getBaseContext();
                    }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        activity = (Activity) v.getContext();
                    }
                    takePhoto(activity, 150);
                    //startActivityForResult(intent,SELECTED_PICTURE);
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT){
                    activity = (Activity) ((ContextWrapper) v.getContext()).getBaseContext();
                }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    activity = (Activity) v.getContext();
                }
                showFileChooser(activity, 100);
            }
        });

        textViewDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        // set listener on "Done" text view in toolbar to get user list checked
                // get Group Name and Description
                editTextGroupNmame = (EditText) findViewById(R.id.editTextGroupName);
                editTextDescription = (EditText) findViewById(R.id.editTextGroupDescription);
                String groupName = editTextGroupNmame.getText().toString();
                String description = editTextDescription.getText().toString();

                // get Checkbox condition of adding group member. checking is operated in AddGroupAdapter class.
                // "checkedString" keep usernames to be checked
                checkedString = new StringBuffer();
                for (IContacts contact : adapter.checkedUsers) {
                    checkedString.append(contact.getContactName());
                    checkedString.append("\n");
                }

                if (groupName.isEmpty() || description.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddGroupActivity.this);
                    builder.setMessage("Group Name and Description fields are required.")
                            .setNegativeButton("Back", null)
                            .create()
                            .show();
                } else if (adapter.checkedUsers.size() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddGroupActivity.this);
                    builder.setMessage("Please select at least one user.")
                            .setNegativeButton("Back", null)
                            .create()
                            .show();//
                } else {

                    if (canvas.getDrawable() == null) {
                        return;
                    } else {
                        bitmap = ((BitmapDrawable) canvas.getDrawable()).getBitmap();
                    }

                    if (AddGroupAdapter.checkedUsers != null) {
                        for (int i = 0; i < AddGroupAdapter.checkedUsers.size(); i++) {
                            usersId.add(Integer.parseInt(AddGroupAdapter.checkedUsers.get(i).getUserId()));
                        }
                        IGroups group = new Groups(1, groupName, description, 1, usersId, bitmap);
                        Groups.groupList.add(group);
                        //after sending data, back to contact page
                        Toast.makeText(AddGroupActivity.this, "Group: " + groupName + "\n Description: " + description + "\n User: " + checkedString.toString() + " was added.", Toast.LENGTH_SHORT).show();
                        Log.d("AddGroup_userId", usersId.toString());
                        Intent myIntent = new Intent(AddGroupActivity.this, ContactsActivity.class);
                        startActivity(myIntent);
                    }
                }
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
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
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(AddGroupActivity.this, permission)) {
            //This is called if user has denied the permission before
            //In this case I am just asking the permission again
            ActivityCompat.requestPermissions(AddGroupActivity.this, new String[]{permission}, requestCode);
        } else {
            ActivityCompat.requestPermissions(AddGroupActivity.this, new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
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
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

}