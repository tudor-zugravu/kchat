package com.example.user.kchat01;

import android.Manifest;
import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import API.IContacts;
import API.IGroups;
import IMPL.Contacts;
import IMPL.Groups;
import IMPL.JsonDeserialiser;
import IMPL.JsonSerialiser;
import IMPL.MasterUser;
import IMPL.RESTApi;

/**
 * Created by user on 22/02/2017.
 */

/* This is main activity to create adding group page */
    // User list checked to add to the group is stored in the variable of "checkedString".

public class AddGroupActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolbarTitle;
    public static  TextView textViewDone; //"textViewDone" is clickable tv on toolbar
    private EditText editTextGroupNmame, editTextDescription;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private AddGroupAdapter adapter;
    private ArrayList<IContacts> memberList;
    private StringBuffer checkedString=null;
    private Activity activity;
    ArrayList<Integer> usersId;
    ImageView camera, gallery, canvas;
    Bitmap bitmap;
    DataManager dm;
    String groupId ,groupName, description;

    static final Integer CAMERA = 0x5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dm = new DataManager(AddGroupActivity.this);
        usersId = new ArrayList<>();

           // ContactsActivity.mSocket.connect();
            ContactsActivity.mSocket.on("group_created",getGroupId);

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
            if(InternetHandler.hasInternetConnection(AddGroupActivity.this,0)==false){

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
                if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT){
                    activity = (Activity) ((ContextWrapper) v.getContext()).getBaseContext();
                }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    activity = (Activity) v.getContext();
                }
                askForPermission(Manifest.permission.CAMERA,CAMERA);
                ImageUpload.takePhoto(activity, 150);
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
                usersId.clear();
                // get Group Name and Description
                editTextGroupNmame = (EditText) findViewById(R.id.editTextGroupName);
                editTextDescription = (EditText) findViewById(R.id.editTextGroupDescription);
                 groupName = editTextGroupNmame.getText().toString();
                 description = editTextDescription.getText().toString();

                //name, description, ownerId, group_picture, userIds
                for (int i = 0; i < AddGroupAdapter.checkedUsers.size(); i++) {
                    usersId.add(Integer.parseInt(AddGroupAdapter.checkedUsers.get(i).getUserId()));
                }
                // get Checkbox condition of adding group member. checking is operated in AddGroupAdapter class.
                // "checkedString" keep usernames to be checked
                checkedString = new StringBuffer();
                for (IContacts contact : adapter.checkedUsers) {
                    checkedString.append(contact.getContactName());
                    checkedString.append("\n");
                }
                Log.d("USERSLIST", Integer.toString(usersId.size()));
                JSONArray jsonArray = new JSONArray(usersId);
                ContactsActivity.mSocket.emit("create_group", groupName, description, MasterUser.usersId, "true", jsonArray);
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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                canvas.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 150 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            canvas.setImageBitmap(bitmap);
        }
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(AddGroupActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddGroupActivity.this, permission)) {
                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(AddGroupActivity.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(AddGroupActivity.this, new String[]{permission}, requestCode);
            }
        }else {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
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

    private Emitter.Listener getGroupId = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            AddGroupActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String receivedMessages = (String) args [0];
                    if(receivedMessages.equals("fail")){
                        Log.d("GROUPLIST", receivedMessages);
                    }else {
                       // receivedMessages = receivedMessages.substring(5);
                        //receivedMessages = receivedMessages.substring(0,1);
                        Log.d("GROUPLIST", " i have received from server"+receivedMessages);
                        groupId = receivedMessages;

                        if (groupName.isEmpty() || description.isEmpty()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddGroupActivity.this);
                            builder.setMessage("Group Name and Description fields are required.").setNegativeButton("Back", null).create().show();
                        } else if (adapter.checkedUsers.size() == 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddGroupActivity.this);
                            builder.setMessage("Please select at least one user.").setNegativeButton("Back", null).create().show();//
                        } else {

                            if (canvas.getDrawable() == null) {
                                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.add_group);
                                return;
                            } else {
                                bitmap = ((BitmapDrawable) canvas.getDrawable()).getBitmap();
                                if(InternetHandler.hasInternetConnection(AddGroupActivity.this,1)==false){

                                }else {
                                    if (bitmap != null) {
                                        Log.d("GROUPLIST", " i am sending the groups picture");
                                        Bitmap bitmap = ((BitmapDrawable) canvas.getDrawable()).getBitmap();
                                        String codedImage = ImageUpload.getStringImage(bitmap);
                                        JsonSerialiser imageSerialiser = new JsonSerialiser();
                                        MasterUser man = new MasterUser();
                                        String imagetosend = imageSerialiser.serialiseProfileImage(groupId, codedImage);
                                        String type = "updateImage";
                                        String login_url = "http://188.166.157.62:3000/groupImageUpload";
                                        ArrayList<String> paramList = new ArrayList<>();
                                        paramList.add("request");
                                        paramList.add("json");
                                        RESTApi backgroundasync = new RESTApi(AddGroupActivity.this, login_url, paramList);
                                        backgroundasync.execute(type, "profileImageChange", imagetosend);
                                    }
                                }
                            }

                            if (AddGroupAdapter.checkedUsers != null) {
                                String imageLocation = "group_picture"+groupId+".jpg";
                                Log.d("GROUPLIST",imageLocation);
                                IGroups group = new Groups(groupName,description,MasterUser.usersId,imageLocation,usersId);
                                group.setActualOwnerId(MasterUser.usersId);
                                group.setGroupImage(bitmap);
                                group.setOwnerId(Integer.parseInt(groupId));
                                Groups.groupList.add(group);
                                //after sending data, back to contact page
                                Toast.makeText(AddGroupActivity.this, "Group: " + groupName + "\n Description: " + description + "\n User: " + checkedString.toString() + " was added.", Toast.LENGTH_SHORT).show();
                                Log.d("GROUPLIST", usersId.toString());
                                onBackPressed();
                            }
                        }
                    }
                }
            });
        }
    };


}