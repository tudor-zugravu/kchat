package com.example.user.kchat01;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import API.IContacts;
import IMPL.Contacts;
import IMPL.JsonDeserialiser;
import IMPL.MasterUser;
import IMPL.RESTApi;

/**
 * Created by user on 22/02/2017.
 */

/* This is main activity to create adding group page */
    // User list checked to add to the group is stored in the variable of "checkedString".

public class AddContactActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolbarTitle;
    public static TextView textViewDone; //"textViewDone" is clickable tv on toolbar
    private EditText editTextGroupNmame, editTextDescription;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private AddGroupAdapter adapter;
    private ArrayList<IContacts> memberList;
    private StringBuffer checkedString = null;
    private Activity activity;
    ArrayList<Integer> usersId, groupUsersId;
    ImageView camera, gallery, canvas;
    Bitmap bitmap;
    DataManager dm;
    Socket getmSocket;
    String ownerId, groupId, groupName, description;

    static final Integer CAMERA = 0x5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("CONTACTLIST", "I have data here 98765432");

        dm = new DataManager(AddContactActivity.this);
        usersId = new ArrayList<>();

        Intent intent = getIntent();
        this.ownerId = intent.getStringExtra("ownerId");
        this.groupUsersId = intent.getIntegerArrayListExtra("usernames");
        this.groupName = intent.getStringExtra("groupName");

        setContentView(R.layout.activity_add_contact);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        // apply toolbar title
        toolbarTitle.setText("Add Contact");
        toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));

        //get contactList Here
        MasterUser man = new MasterUser();
        if (dm.selectAllContacts().getCount() > 0) {
            dm.selectAllContacts();
        } else {
            if (InternetHandler.hasInternetConnection(AddContactActivity.this,0) == false) {

            } else {
                try {
                    String type2 = "getcontacts";
                    String contacts_url = "http://188.166.157.62:3000/contacts";
                    ArrayList<String> paramList2 = new ArrayList<>();
                    paramList2.add("userId");
                    RESTApi backgroundasync2 = new RESTApi(AddContactActivity.this, contacts_url, paramList2);
                    String result2 = backgroundasync2.execute(type2, man.getuserId()).get();
                    JsonDeserialiser deserialiser = new JsonDeserialiser(result2, "getcontacts", AddContactActivity.this);

                } catch (InterruptedException e) {
                } catch (ExecutionException f) {
                }

            }

            }
            adapter = new AddGroupAdapter(AddContactActivity.this, Contacts.contactList, 1);// "1" : AddContactActivity

            textViewDone = (TextView) findViewById(R.id.textViewDone);

            textViewDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    usersId.clear();
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
                    JSONArray jsonArray = new JSONArray(usersId);
//                mSocket.emit("create_group", groupName, description, MasterUser.usersId, "true", jsonArray);
                    Toast.makeText(AddContactActivity.this,"addcontact: "+jsonArray+"\n ContactName: "+String.valueOf(checkedString)+"\nGroupID: "+ownerId, Toast.LENGTH_SHORT).show();
                }
            });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
        });

        }
/*
    private Emitter.Listener getGroupId = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            AddContactActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String receivedMessages = (String) args [0];
                    if(receivedMessages.equals("fail")){
                        Log.d("USERSLIST", receivedMessages);
                    }else {
                        receivedMessages = receivedMessages.substring(5);
                        Log.d("USERSLIST", " i have received from server    "+receivedMessages);
                        groupId = receivedMessages;

                        if (groupName.isEmpty() || description.isEmpty()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddContactActivity.this);
                            builder.setMessage("Group Name and Description fields are required.").setNegativeButton("Back", null).create().show();
                        } else if (adapter.checkedUsers.size() == 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddContactActivity.this);
                            builder.setMessage("Please select at least one user.").setNegativeButton("Back", null).create().show();//
                        } else {

                            if (canvas.getDrawable() == null) {
                                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.add_group);
                                return;
                            } else {
                                bitmap = ((BitmapDrawable) canvas.getDrawable()).getBitmap();
                                if(InternetHandler.hasInternetConnection(AddContactActivity.this)==false){

                                }else {
                                    if (bitmap != null) {
                                        Log.d("USERSLIST", " i am sending the groups picture");
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
                                        RESTApi backgroundasync = new RESTApi(AddContactActivity.this, login_url, paramList);
                                        backgroundasync.execute(type, "profileImageChange", imagetosend);
                                    }
                                }
                            }

                            if (AddGroupAdapter.checkedUsers != null) {

                                String imageLocation = "group_picture"+groupId+".jpg";
                                IGroups group = new Groups(groupName,description,MasterUser.usersId,imageLocation,usersId);
                                group.setGroupImage(bitmap);
                                Groups.groupList.add(group);
                                //after sending data, back to contact page
                                Toast.makeText(AddContactActivity.this, "Group: " + groupName + "\n Description: " + description + "\n User: " + checkedString.toString() + " was added.", Toast.LENGTH_SHORT).show();
                                Log.d("AddGroup_userId", usersId.toString());
                                Intent myIntent = new Intent(AddContactActivity.this, ContactsActivity.class);
                                startActivity(myIntent);
                            }
                        }
                    }
                }
            });
        }
    };
*/


}