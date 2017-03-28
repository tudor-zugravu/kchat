package com.example.user.kchat01;

import android.app.Activity;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import API.IContacts;
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
    String ownerId, groupId, groupName, contactSize;

    static final Integer CAMERA = 0x5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dm = new DataManager(AddContactActivity.this);
        usersId = new ArrayList<>();

        ContactsActivity.mSocket.on("added_to_group",addedToGroup);
        Intent intent = getIntent();
        this.ownerId = intent.getStringExtra("ownerId");
        this.groupUsersId = intent.getIntegerArrayListExtra("usernames");
        this.groupName = intent.getStringExtra("groupName");
        this.contactSize = intent.getStringExtra("contactsizenum");

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

            if(Contacts.contactList!=null&&groupUsersId!=null&&!groupUsersId.isEmpty()) {
                HashMap<String, IContacts> tempContacts = new HashMap<>();
                for (IContacts contact : Contacts.contactList) {
                    tempContacts.put(contact.getUserId(), contact);
                }
                for (Integer user : groupUsersId) {
                    if (tempContacts.containsKey(user.toString())) {
                        tempContacts.remove(user.toString());
                        Log.d("Chicken","chick333   , hit here");
                    }
                }
                memberList = new ArrayList<>(tempContacts.values());
                tempContacts.clear();
            }else{
                memberList = Contacts.contactList;
            }

            adapter = new AddGroupAdapter(AddContactActivity.this, memberList, 1);// "1" : AddContactActivity
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
                    ContactsActivity.mSocket.emit("add_to_group",ownerId, jsonArray);
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


    private Emitter.Listener addedToGroup = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            AddContactActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String received = (String) args[0]; // id to delete from active chats
                    Log.d("LeChecker",received);
                    if(received.equals("success")){
                    finish();
                    }else{

                    }
                }
            });

        }
    };



}