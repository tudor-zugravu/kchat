package com.example.user.kchat01;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.ByteArrayOutputStream;
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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.example.user.kchat01.R.id.contacts;

/**
 * Created by user on 22/02/2017.
 */

/* This is main activity to create contacts */
    // For local test, sample data is generated in "ItemContacts" class. "getObject" method calls the data in this activity.
public class ContactsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton btn_sendRequest, btn_receiveRequest, btn_searchContacts;
    private TextView toolbarTitle;
    private RecyclerView recyclerView;
    private SearchView searchView;
    ContactsAdapter adapter;
    public static int tabId;
    private BottomBar bottomBar;
    MasterUser man = new MasterUser();
    DataManager dm;
    private Socket mSocket;
    NotificationCompat.Builder notification;
    public static final int uniqueId = 45611;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dm = new DataManager(ContactsActivity.this);
             if(man.getProfileLocation()!=null) {
                 if(InternetHandler.hasInternetConnection(ContactsActivity.this)==false){
                 }else {
                     try {
                         String picture_url = "http://188.166.157.62/profile_pictures/" + "profile_picture" + man.getuserId() + ".jpg";
                         String type = "getImage";
                         ArrayList<String> paramList = new ArrayList<>();
                         paramList.add("picture");
                         RESTApi backgroundasync = new RESTApi(ContactsActivity.this, picture_url, paramList);
                         String result = backgroundasync.execute(type).get();
                     } catch (InterruptedException e) {
                     } catch (ExecutionException f) {
                     }
                 }
        }

        try {
            mSocket = IO.socket("http://188.166.157.62:3000");
        } catch (URISyntaxException e){
        }
        if(InternetHandler.hasInternetConnection(ContactsActivity.this)==false){
            mSocket.disconnect();
        }else {
            mSocket.connect();
            mSocket.on("connect", startConnection);
            mSocket.on("authenticated", authenticate);////
            mSocket.on("sent_chats", currentChats);
            mSocket.on("sent_group_chats", currentGroups);
            mSocket.emit("get_chats", MasterUser.usersId);
            mSocket.emit("get_group_chats", MasterUser.usersId);
        }
        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);
        setContentView(R.layout.activity_contacts);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);

        btn_sendRequest = (ImageButton)findViewById(R.id.btn_sendRequest);
        btn_receiveRequest = (ImageButton)findViewById(R.id.btn_receiveRequest);
        btn_searchContacts = (ImageButton)findViewById(R.id.btn_searchContacts);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        this. bottomBar = (BottomBar) findViewById(R.id.bottomNavi);

        btn_searchContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(ContactsActivity.this, SearchRequestActivity.class);
                registerIntent.putExtra("type","searchContacts");
                ContactsActivity.this.startActivity(registerIntent);
            }
        });
        btn_sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(ContactsActivity.this, SearchRequestActivity.class);
                registerIntent.putExtra("type","sendRequest");
                ContactsActivity.this.startActivity(registerIntent);
            }
        });
        btn_receiveRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(ContactsActivity.this, SearchRequestActivity.class);
                registerIntent.putExtra("type","receiveRequest");
                ContactsActivity.this.startActivity(registerIntent);
            }
        });

            btn_receiveRequest.setVisibility(GONE);
            btn_sendRequest.setVisibility(GONE);
            btn_searchContacts.setVisibility(GONE);
            ContactsActivity.showPlus=false;
            invalidateOptionsMenu();
            // getObjectList is to generate sample data in ItemContacs class.
            adapter = new ContactsAdapter(ContactsActivity.this, Contacts.activeChat,0) {
                @Override
                public void onClick(ContactsViewHolder holder) {
                    int position = recyclerView.getChildAdapterPosition(holder.itemView);
                    IContacts contact = Contacts.activeChat.get(position);
                    Intent contactsIntent = new Intent(getApplicationContext(), ChatsActivity.class);
                    contactsIntent.putExtra("contactObj", contact.getContactName());
                    startActivity(contactsIntent);
                }
            };
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);

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
                    btn_receiveRequest.setVisibility(GONE);
                    btn_sendRequest.setVisibility(GONE);
                    btn_searchContacts.setVisibility(GONE);
                    ContactsActivity.showPlus=false;
                    toolbarTitle.setText("Chats");
                    toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));
                    invalidateOptionsMenu();
                    ContactsActivity.tabId=tabId;
                    adapter = new ContactsAdapter(ContactsActivity.this, Contacts.activeChat,0) {
                        @Override
                        public void onClick(ContactsViewHolder holder) {
                            int position = recyclerView.getChildAdapterPosition(holder.itemView);
                            IContacts contact = Contacts.activeChat.get(position);
                            Intent contactsIntent = new Intent(ContactsActivity.this, ChatsActivity.class);
                            String type = "contact";
                            contactsIntent.putExtra("type",type);
                            contactsIntent.putExtra("userid",Integer.toString(contact.getContactId()));
                            //contactsIntent.putExtra("username",contact.getUsername());
                            contactsIntent.putExtra("contactname",contact.getContactName());
                            contactsIntent.putExtra("contactid",contact.getContactId());
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            contact.getBitmap().compress(Bitmap.CompressFormat.JPEG,100,stream);
                            byte [] byteArray = stream.toByteArray();
                            contactsIntent.putExtra("contactbitmap",byteArray);
                            startActivity(contactsIntent);
                        }
                        @Override
                        public void onLongClick(ContactsViewHolder holder){
                            int position = recyclerView.getChildAdapterPosition(holder.itemView);
                            IContacts contact = Contacts.activeChat.get(position);
                            // move to Profile
                            Intent profileIntent = new Intent(ContactsActivity.this, ProfileActivity.class);
                            profileIntent.putExtra("contact_username", contact.getUsername());
                            profileIntent.putExtra("contact_email", contact.getEmail());
                            profileIntent.putExtra("contact_phonenumber", contact.getPhoneNumber());
                            //profileIntent.putExtra("contact_biography", "NOTHING");
                            profileIntent.putExtra("contacts_bitmap", contact.getBitmap());
                            profileIntent.putExtra("type", "contactsprofile");
                            startActivity(profileIntent);
                        }
                    };
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);

                }
                if (tabId == R.id.groups) {
                    btn_receiveRequest.setVisibility(GONE);
                    btn_sendRequest.setVisibility(GONE);
                    btn_searchContacts.setVisibility(GONE);
                    ContactsActivity.showPlus=true;
                    toolbarTitle.setText("Groups");
                    toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));
                    invalidateOptionsMenu();
                    ContactsActivity.tabId=tabId;
                    Log.d("CALLEDSTATUS","bottom bar group id is:" + ContactsActivity.tabId);
                    adapter = new ContactsAdapter(ContactsActivity.this, Groups.groupList,2) {
                        @Override
                        public void onClick(ContactsViewHolder holder) {
                            int position = recyclerView.getChildAdapterPosition(holder.itemView);
                            IGroups group = Groups.groupList.get(position);
                            Intent groupIntent = new Intent(ContactsActivity.this, GroupChatsActivity.class);
                            groupIntent.putExtra("ownerId",Integer.toString(group.getOwnerId()));
                            groupIntent.putExtra("usernames",group.getUsersAsID());
                            groupIntent.putExtra("groupName",group.getName());
                            groupIntent.putExtra("groupDesc",group.getDescription());
                            startActivity(groupIntent);
                        }
                    };
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }
                if (tabId == contacts) {
                    btn_receiveRequest.setVisibility(VISIBLE);
                    btn_sendRequest.setVisibility(VISIBLE);
                    btn_searchContacts.setVisibility(VISIBLE);
                    ContactsActivity.showPlus=false;
                    toolbarTitle.setText("Contacts");
                    toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));
                    invalidateOptionsMenu();
                    ContactsActivity.tabId=tabId;
                    Log.d("RAR",Integer.toString(dm.selectAllContacts().getCount()));
                    if(dm.selectAllContacts().getCount()>0){
                    dm.selectAllContacts();
                    }else{
                    try {
                        if(InternetHandler.hasInternetConnection(ContactsActivity.this)==false){

                        }else {
                            Log.d("CALLEDSTATUS", "i made a rest request to get the  contacts");
                            String type2 = "getcontacts";
                            String contacts_url = "http://188.166.157.62:3000/contacts";
                            ArrayList<String> paramList2 = new ArrayList<>();
                            paramList2.add("userId");
                            RESTApi backgroundasync2 = new RESTApi(ContactsActivity.this, contacts_url, paramList2);
                            String result2 = backgroundasync2.execute(type2, man.getuserId()).get();
                            JsonDeserialiser deserialiser = new JsonDeserialiser(result2, "getcontacts", ContactsActivity.this);
                        }
                }catch(InterruptedException e){
                }catch(ExecutionException f){
                }}
                    adapter.notifyDataSetChanged();
                    adapter = new ContactsAdapter(ContactsActivity.this, Contacts.contactList,1){// move to profile
                        //By clicking a card, the username is got
                        @Override
                        public void onClick(ContactsViewHolder holder) {
                            int position = recyclerView.getChildAdapterPosition(holder.itemView);
                            IContacts contact = Contacts.getContactList().get(position);
                            Intent contactsIntent = new Intent(ContactsActivity.this, ChatsActivity.class);
                            String type = "contact";
                            contactsIntent.putExtra("type",type);
                            contactsIntent.putExtra("userid",contact.getUserId());
                            contactsIntent.putExtra("username",contact.getUsername());
                            contactsIntent.putExtra("contactid",contact.getContactId());
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            contact.getBitmap().compress(Bitmap.CompressFormat.JPEG,100,stream);
                            byte [] byteArray = stream.toByteArray();
                            contactsIntent.putExtra("contactbitmap",byteArray);
                            startActivity(contactsIntent);
                        }
                        @Override
                        public void onLongClick(ContactsViewHolder holder){
                            int position = recyclerView.getChildAdapterPosition(holder.itemView);
                            IContacts contact = Contacts.getContactList().get(position);
                            // move to Profile
                            Intent profileIntent = new Intent(ContactsActivity.this, ProfileActivity.class);
                            profileIntent.putExtra("contact_username", contact.getUsername());
                            profileIntent.putExtra("contact_userid", contact.getUserId());
                            profileIntent.putExtra("contact_contactname", contact.getContactName());
                            profileIntent.putExtra("contact_contactid",contact.getContactId());
                            profileIntent.putExtra("position", position);
                            profileIntent.putExtra("contact_email", contact.getEmail());
                            profileIntent.putExtra("contact_phonenumber", contact.getPhoneNumber());
                            //profileIntent.putExtra("contact_biography", "NOTHING");//need to implement contact.getBiography()
                            profileIntent.putExtra("contacts_bitmap", contact.getBitmap());
                            profileIntent.putExtra("type", "contactsprofile");
                            startActivity(profileIntent);
                        }
                    };
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }
                if (tabId == R.id.profile) {
                    ContactsActivity.tabId=tabId;
                    Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                    MasterUser man = new MasterUser();
                    profileIntent.putExtra("users_username", man.getUsername());
                    profileIntent.putExtra("users_email", man.getEmail());
                    profileIntent.putExtra("users_phonenumber", man.getTelephonenumber());
                    profileIntent.putExtra("users_biography", man.getBiography());
                    profileIntent.putExtra("type", "usersprofile");
                    startActivity(profileIntent);
                }
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId != ContactsActivity.tabId) {
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add) {
            Intent intent = new Intent(getApplicationContext(), AddGroupActivity.class);
            startActivity(intent);
        }
        return true;
    }

    static Boolean showPlus = false;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(showPlus==false){
            return false;
        }
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.groups_menu, menu);
        return true;
    }

    private Emitter.Listener currentChats = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ContactsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String receivedMessages = (String) args [0];
                    JsonDeserialiser messageDeserialise = new JsonDeserialiser(receivedMessages,"chats",ContactsActivity.this);
                }
            });
        }
    };

    private Emitter.Listener startConnection = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ContactsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MasterUser man = new MasterUser();
                   mSocket.emit("authenticate",man.getuserId(),man.getUsername());
                }
            });
        }
    };

    private Emitter.Listener authenticate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ContactsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSocket.on("update_chat",chatUpdater);
                    mSocket.on("global_private_messages",notifications);

                    String receivedMessages = (String) args [0]; // room number
                        if(receivedMessages!=null&&receivedMessages.equals("disconnect")){
                            mSocket.disconnect();

                        }

                }
            });
        }
    };
    public static String roomnumber="";

    private Emitter.Listener notifications = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ContactsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String receivedMessages = (String) args [0]; // room number
                    if(!roomnumber.equals(receivedMessages)){
                        int receivedMessages2 = (Integer) args [1]; // message id
                        Log.d("UPDATECHAT",Integer.toString(receivedMessages2));
                        String receivedMessages3 = (String) args [2]; // username
                        Log.d("UPDATECHAT",receivedMessages3);
                        String receivedMessages4 = (String) args [3]; // message
                        Log.d("UPDATECHAT",receivedMessages4);
                        String receivedMessages5 = (String) args [4]; // timestamp
                        Log.d("UPDATECHAT",receivedMessages5);
                        Log.d("UPDATECHAT","global2222!!!");
                        notification.setSmallIcon(R.drawable.profile_logo);
                        notification.setTicker("This is the ticker");
                        notification.setWhen(System.currentTimeMillis());
                        notification.setContentTitle("Message from:" +receivedMessages3);
                        notification.setContentText(receivedMessages4);
                        //notification.setSound(Uri.parse("android.resource://" + ContactsActivity.this.getPackageName() + "/" + R.raw.notification));
                        showNotification();
                    }
                }
            });
        }
    };

    private void showNotification (){
        Intent intent = new Intent(this,ContactsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(uniqueId,notification.build());

    }

    private Emitter.Listener chatUpdater = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ContactsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String receivedMessages = (String) args [0];
                    Log.d("UPDATECHAT",receivedMessages);
                }
            });
        }
    };

    private Emitter.Listener currentGroups = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ContactsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String receivedMessages = (String) args [0];
                    Log.d("GROUPSRECEIVED",receivedMessages);
                    JsonDeserialiser messageDeserialise = new JsonDeserialiser(receivedMessages,"groups",ContactsActivity.this);
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recyclerView.getRecycledViewPool().clear();
        adapter.notifyDataSetChanged();
    }
}