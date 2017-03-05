package com.example.user.kchat01;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import API.IContacts;
import API.IGroups;
import IMPL.Contacts;
import IMPL.Groups;
import IMPL.JsonDeserialiser;
import IMPL.MasterUser;
import IMPL.RESTApi;

import static com.example.user.kchat01.R.id.cancel_action;
import static com.example.user.kchat01.R.id.contacts;

/**
 * Created by user on 22/02/2017.
 */

/* This is main activity to create contacts */
    // For local test, sample data is generated in "ItemContacts" class. "getObject" method calls the data in this activity.

public class ContactsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private RecyclerView recyclerView;
    private SearchView searchView;
    ContactsAdapter adapter;
    public static int tabId;
    private BottomBar bottomBar;
    private Socket mSocket;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("CALLEDSTATUS","Contacts Contacts Activity was called");

        MasterUser man = new MasterUser();
        try {
            mSocket = IO.socket("http://188.166.157.62:3000");
            mSocket.on("user_connect",onlineJoin);
            mSocket.connect();
            mSocket.emit("user_connect", man.getUsername());
        }catch (URISyntaxException e){
        }

        if(man.getProfileLocation()!=null) {
            try {
                Log.d("CALLEDSTATUS","Getting the data from the server");

                String picture_url = "http://188.166.157.62/profile_pictures/" + "profile_picture" + man.getuserId() + ".jpg";
                String type = "getImage";
                ArrayList<String> paramList = new ArrayList<>();
                paramList.add("picture");
                RESTApi backgroundasync = new RESTApi(ContactsActivity.this, picture_url, paramList);
                String result = backgroundasync.execute(type).get();
            }catch(InterruptedException e){
            }catch(ExecutionException f){
            }
        }
        Log.d("CALLEDSTATUS","resuming with the ui draw");

        setContentView(R.layout.activity_contacts);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        // apply toolbar title
        toolbarTitle.setText("Contacts");
        toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        this. bottomBar = (BottomBar) findViewById(R.id.bottomNavi);

        Log.d("CALLEDSTATUS","bottom bar id is:" + bottomBar.getCurrentTabId());
            int bottomBarNum = bottomBar.getCurrentTabPosition();
        Log.d("CALLEDSTATUS","bottom bar id is:" + bottomBar.getCurrentTabId());
        adapter = new ContactsAdapter(ContactsActivity.this, Groups.getObjectList(),0);
        recyclerView.setAdapter(adapter);
        if(ContactsActivity.tabId==2131624157){ // id for defaults and chat
            Log.d("CALLEDSTATUS", "contacts has been clicked");
            // getObjectList is to generate sample data in ItemContacs class.
            adapter = new ContactsAdapter(ContactsActivity.this, Contacts.contactList,1) {
                //By clicking a card, the username is got
                @Override
                public void onClick(ContactsViewHolder holder) {
                    int position = recyclerView.getChildAdapterPosition(holder.itemView);
                    IGroups contact = Groups.getObjectList().get(position);
                    //makeText(getApplicationContext(), "clicked= " + contact.getUsername(), Toast.LENGTH_SHORT).show();
                    Intent contactsIntent = new Intent(getApplicationContext(), ChatsActivity.class);
                    contactsIntent.putExtra("username", contact.getName());
                    startActivity(contactsIntent);
                }
            };
            recyclerView.setAdapter(adapter);

        }else if (ContactsActivity.tabId==2131624158) { //tab id for groups
            // getObjectList is to generate sample data in ItemContacs class.
            Log.d("CALLEDSTATUS", "groups has been clicked");

            adapter = new ContactsAdapter(ContactsActivity.this, Groups.getObjectList(),0) {
                //By clicking a card, the username is got
                @Override
                public void onClick(ContactsViewHolder holder) {
                    int position = recyclerView.getChildAdapterPosition(holder.itemView);
                    IGroups contact = Groups.getObjectList().get(position);
                    //makeText(getApplicationContext(), "clicked= " + contact.getUsername(), Toast.LENGTH_SHORT).show();
                    Intent contactsIntent = new Intent(getApplicationContext(), ChatsActivity.class);
                    contactsIntent.putExtra("username", contact.getName());
                    startActivity(contactsIntent);
                }
            };
            recyclerView.setAdapter(adapter);

        }else if (ContactsActivity.tabId==2131624159) { // for contacts
            Log.d("CALLEDSTATUS", "something has been clicked");
            // getObjectList is to generate sample data in ItemContacs class.
            adapter = new ContactsAdapter(ContactsActivity.this, Contacts.contactList,1) {
                //By clicking a card, the username is got
                @Override
                public void onClick(ContactsViewHolder holder) {
                    int position = recyclerView.getChildAdapterPosition(holder.itemView);
                    IGroups contact = Groups.getObjectList().get(position);
                    //makeText(getApplicationContext(), "clicked= " + contact.getUsername(), Toast.LENGTH_SHORT).show();
                    Intent contactsIntent = new Intent(getApplicationContext(), ChatsActivity.class);
                    contactsIntent.putExtra("username", contact.getName());
                    startActivity(contactsIntent);
                }
            };
            recyclerView.setAdapter(adapter);

        }
        recyclerView.setAdapter(adapter);

        //set linearLayoutManager to recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        /*
        From here, search function is performed
         */

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

        /*
        From here, Bottom Bar is implemented
        */
        bottomBar.setDefaultTab(R.id.chats);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                ContactsActivity.tabId = tabId;
                if (tabId == R.id.chats) {
                    ContactsActivity.tabId=tabId;
                    Log.d("CALLEDSTATUS","bottom bar chats id is:"+ ContactsActivity.tabId);
                    //Intent chatsIntent = new Intent(getApplicationContext(),old_ChatActivity.class);
                    //startActivity(chatsIntent);
                    Toast.makeText(getApplicationContext(), "Chats", Toast.LENGTH_SHORT).show();
                }
                if (tabId == R.id.groups) {
                    ContactsActivity.tabId=tabId;
                    Log.d("CALLEDSTATUS","bottom bar group id is:" + ContactsActivity.tabId);
                    adapter = new ContactsAdapter(ContactsActivity.this, Groups.getObjectList(),0) {
                        //By clicking a card, the username is got
                        @Override
                        public void onClick(ContactsViewHolder holder) {
                            int position = recyclerView.getChildAdapterPosition(holder.itemView);
                            IGroups contact = Groups.getObjectList().get(position);
                            //makeText(getApplicationContext(), "clicked= " + contact.getUsername(), Toast.LENGTH_SHORT).show();
                            Intent contactsIntent = new Intent(getApplicationContext(), ChatsActivity.class);
                            contactsIntent.putExtra("username", contact.getName());
                            startActivity(contactsIntent);
                        }
                    };
                    recyclerView.setAdapter(adapter);
                    //Intent groupIntent = new Intent(getApplicationContext(),GroupActivity.class);
                    //startActivity(groupIntent);
                    Toast.makeText(getApplicationContext(), "Group", Toast.LENGTH_SHORT).show();
                }
                if (tabId == contacts) {
                    MasterUser man = new MasterUser();
                    ContactsActivity.tabId=tabId;
                    try{
                    Log.d("CALLEDSTATUS", "i made a rest request");
                    String type2 = "getcontacts";
                    String contacts_url = "http://188.166.157.62:3000/contacts";
                    ArrayList<String> paramList2 = new ArrayList<>();
                    paramList2.add("userId");
                    RESTApi backgroundasync2 = new RESTApi(ContactsActivity.this, contacts_url, paramList2);
                    String result2 = backgroundasync2.execute(type2, man.getuserId()).get();
                        JsonDeserialiser deserialiser = new JsonDeserialiser(result2,"getcontacts",ContactsActivity.this);

                }catch(InterruptedException e){
                }catch(ExecutionException f){
                }

                    Log.d("CALLEDSTATUS","The size of the Arraylist for contacts is part2  :" + Contacts.getContactList().size());

                    adapter.notifyDataSetChanged();
                    adapter = new ContactsAdapter(ContactsActivity.this, Contacts.contactList,1);
                    recyclerView.setAdapter(adapter);
//                    try {
//                        Log.d("DESERIALISER", "i made a rest request");
//                        String type = "getcontacts";
//                        String contacts_url = "http://188.166.157.62:3000/contacts";
//                        ArrayList<String> paramList = new ArrayList<>();
//                        paramList.add("userId");
//                        RESTApi backgroundasync = new RESTApi(ContactsActivity.this, contacts_url, paramList);
//                        MasterUser man = new MasterUser();
//                        String result = backgroundasync.execute(type, man.getuserId()).get();
//                    }catch (InterruptedException e){
//                    }catch (ExecutionException f){
//                    }
                }
                if (tabId == R.id.profile) {
                    ContactsActivity.tabId=tabId;
                    Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                    MasterUser man = new MasterUser();
                    profileIntent.putExtra("users_username", man.getUsername());
                    profileIntent.putExtra("users_email", man.getEmail());
                    profileIntent.putExtra("users_phonenumber", man.getTelephonenumber());
                    profileIntent.putExtra("users_biography", man.getBiography());
                    startActivity(profileIntent);
                    //Toast.makeText(getApplicationContext(), "Profile", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId != ContactsActivity.tabId) {
                        Intent contactsIntent = new Intent(getApplicationContext(), ContactsActivity.class);
                        startActivity(contactsIntent);
                }
            }
        });


    /*
    From here, Bottom navigation settings
    Future work: implement as another class
    item0: Chats, item1: Groups, item2: Contacts, item3: Profile
    */
        //recognise the bottom navi
   /*
        final BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().getItem(0).setChecked(false);
        bottomNavigationView.getMenu().getItem(1).setChecked(false);
        bottomNavigationView.getMenu().getItem(2).setChecked(true);
        bottomNavigationView.getMenu().getItem(3).setChecked(false);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.chats:
                                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                                bottomNavigationView.getMenu().getItem(1).setChecked(false);
                                bottomNavigationView.getMenu().getItem(2).setChecked(false);
                                bottomNavigationView.getMenu().getItem(3).setChecked(false);
                                Intent chatIntent = new Intent(getApplicationContext(), old_ChatActivity.class);
                                startActivity(chatIntent);
                                break;
                            case R.id.groups:
                                bottomNavigationView.getMenu().getItem(0).setChecked(false);
                                bottomNavigationView.getMenu().getItem(1).setChecked(true);
                                bottomNavigationView.getMenu().getItem(2).setChecked(false);
                                bottomNavigationView.getMenu().getItem(3).setChecked(false);
                                Toast.makeText(getApplicationContext(), "groups is clicked.", Toast.LENGTH_LONG).show();
                                break;
                            case R.id.contacts:
                                bottomNavigationView.getMenu().getItem(0).setChecked(false);
                                bottomNavigationView.getMenu().getItem(1).setChecked(false);
                                bottomNavigationView.getMenu().getItem(2).setChecked(true);
                                bottomNavigationView.getMenu().getItem(3).setChecked(false);
                                Intent contactsIntent = new Intent(getApplicationContext(), ContactsListActivity.class);
                                startActivity(contactsIntent);
                                break;
                            case profile:
                                //finish();
                                bottomNavigationView.getMenu().getItem(0).setChecked(false);
                                bottomNavigationView.getMenu().getItem(1).setChecked(false);
                                bottomNavigationView.getMenu().getItem(2).setChecked(false);
                                bottomNavigationView.getMenu().getItem(3).setChecked(true);
                                Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                                startActivity(profileIntent);
                                break;
                        }
                        return true;
                    }

                });
*/
    }
    private Emitter.Listener onlineJoin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String serverresult = (String) args[0];
            Log.d("MESSAGEERROR", serverresult.toString());
        }
    };




}