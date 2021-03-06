package com.example.user.kchat01;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
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
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import API.IContacts;
import API.IGroups;
import API.IMessage;
import IMPL.Contacts;
import IMPL.Groups;
import IMPL.JsonDeserialiser;
import IMPL.MasterUser;
import IMPL.RESTApi;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.example.user.kchat01.R.id.contacts;
import static com.example.user.kchat01.R.id.groups;

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
    public static Socket mSocket;
    static NotificationCompat.Builder notification;
    public static final int uniqueId = 45611;
    LinearLayoutManager layoutManager;
    public static ArrayList<IMessage> bufferdList = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        if (tabId == contacts) {
            try {
                    if(InternetHandler.hasInternetConnection(ContactsActivity.this,1)==false){
                    }else {
                        Log.d("CALLEDSTATUS", "onresume");
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

        if (tabId == groups) {
//            mSocket.on("sent_group_chats", currentGroups);
//            Log.d("Chickensz","re-showing the groups again");
//           mSocket.emit("get_group_chats", MasterUser.usersId);

        }
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dm = new DataManager(ContactsActivity.this);
             if(man.getProfileLocation()!=null) {
                 if(InternetHandler.hasInternetConnection(ContactsActivity.this,0)==false){
                 }else {
                         String picture_url = "http://188.166.157.62/profile_pictures/" + "profile_picture" + man.getuserId() + ".jpg";
                         String type = "getImage";
                         ArrayList<String> paramList = new ArrayList<>();
                         paramList.add("picture");
                         RESTApi backgroundasync = new RESTApi(ContactsActivity.this, picture_url, paramList);
                         backgroundasync.execute(type);
                 }
        }
        IO.Options opts = new IO.Options();
        opts.forceNew = true;
            try {
                mSocket = IO.socket("http://188.166.157.62:3000",opts);
                Log.d("SOCKETSTATUS", mSocket.toString());
            } catch (URISyntaxException e) {
            }

        if(InternetHandler.hasInternetConnection(ContactsActivity.this,1)==false){
           // mSocket.disconnect();
        }else {
           // mSocket.connect();
            mSocket.on("connect", startConnection);
            mSocket.on("accepted_my_contact_request",refreshLayout); //-----------refresh
            mSocket.on("disconnected", disconnectManager);
            mSocket.on("authenticated", authenticate);////
            mSocket.on("sent_chats", currentChats);
            mSocket.on("sent_group_chats", currentGroups);
            mSocket.on("you_received_contact_request",receivedContactRequest);
            mSocket.on("you_were_deleted",contactDelete);///-----------refresh
            mSocket.on("you_were_deleted_from_group",groupDelete);
            mSocket.on("accepted_my_contact_request",refreshLayout);
            mSocket.on("you_have_been_added_to_group",youHaveBeenAdded);
            mSocket.emit("get_chats", MasterUser.usersId);
            mSocket.emit("get_group_chats", MasterUser.usersId);
            mSocket.on("global_private_messages",newMessagesListener);
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

         layoutManager = new LinearLayoutManager(this);
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
                    Log.d("TABPRESS","i selected chats");
                    mSocket.emit("get_chats", MasterUser.usersId);
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
                            contactsIntent.putExtra("contactname",contact.getContactName());
                            contactsIntent.putExtra("contactid",contact.getContactId());
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            contact.getBitmap().compress(Bitmap.CompressFormat.JPEG,100,stream);
                            byte [] byteArray = stream.toByteArray();
                            contactsIntent.putExtra("contactbitmap",byteArray);
                            startActivity(contactsIntent);
                        }

                    };
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);

                }
                if (tabId == R.id.groups) {
                    //mSocket.emit("get_group_chats", MasterUser.usersId);   //////
                    Log.d("TABPRESS","i selected groups");
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
                            groupIntent.putExtra("usernames",group.getUsersAsID());////////////////////////////////////
                            groupIntent.putExtra("groupName",group.getName());
                            groupIntent.putExtra("groupDesc",group.getDescription());
                            groupIntent.putExtra("groupDesc2", group.getDescription2());
                            Log.d("GROUPSRECEIVED","I pass this->  "+ group.getActualOwnerId());
                            groupIntent.putExtra("actualOwnerId",group.getActualOwnerId());
                            startActivity(groupIntent);
                        }
                    };
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }
                if (tabId == contacts) {
                    Log.d("TABPRESS","i selected contact");

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
                        Log.d("CONTACTSSIZE", "i reached here");
                    }else{
                    try {
                        if(InternetHandler.hasInternetConnection(ContactsActivity.this,1)==false){

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
                            Log.d("CONTACTSSIZE", "the size of the contacts list is "+Integer.toString(Contacts.contactList.size()));
                            Log.d("CONTACTSSIZE", "the position is is "+Integer.toString(position));
                            IContacts contact = Contacts.contactList.get(position);
                            Intent contactsIntent = new Intent(ContactsActivity.this, ChatsActivity.class);
                            String type = "contact";
                            contactsIntent.putExtra("type",type);
                            contactsIntent.putExtra("userid",contact.getUserId());
                            contactsIntent.putExtra("username",contact.getUsername());
                            contactsIntent.putExtra("contactid",contact.getContactId());
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            if(contact.getBitmap()!=null) {
                                contact.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                byte[] byteArray = stream.toByteArray();
                                contactsIntent.putExtra("contactbitmap", byteArray);
                            }else{
                                Bitmap humanIcon = BitmapFactory.decodeResource(getResources(), R.drawable.human);
                                contact.setBitmap(humanIcon);
                                contact.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                byte[] byteArray = stream.toByteArray();
                                contactsIntent.putExtra("contactbitmap", byteArray);
                            }
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
                            profileIntent.putExtra("contact_biography", contact.getBiography());//need to implement contact.getBiography()
                            profileIntent.putExtra("contacts_bitmap", contact.getBitmap());
                            profileIntent.putExtra("type", "contactsprofile");
                            startActivity(profileIntent);
                        }
                    };
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }
                if (tabId == R.id.profile) {
                    Log.d("TABPRESS","i selected profile");

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
            Intent intent = new Intent(ContactsActivity.this, AddGroupActivity.class);
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
    private Emitter.Listener disconnectManager = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ContactsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String receivedMessages = (String) args [0]; // room number
                    Log.d("AUTHENTICATE", "REACHHHH1");

                    Log.d("AUTHENTICATE", receivedMessages);
                    if(receivedMessages!=null&&receivedMessages.equals("disconnect")){
                        Context context = getApplicationContext();
                        CharSequence text = "Double sign-in detected";
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        mSocket.disconnect();
                        Intent loginIntent = new Intent(ContactsActivity.this,LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                    }
                }
            });
        }
    };

    private Emitter.Listener currentChats = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ContactsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String receivedMessages = (String) args [0];
                    JsonDeserialiser messageDeserialise = new JsonDeserialiser(receivedMessages,"chats",ContactsActivity.this);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
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




    private Emitter.Listener newMessagesListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ContactsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String receivedMessages = (String) args [0];
                    Log.d("RECEIVEDNEWCHAT", "here"+receivedMessages);

                    if(receivedMessages.startsWith("pri")) {
                        Log.d("RECEIVEDNEWCHAT", receivedMessages);
                        mSocket.emit("get_chats", MasterUser.usersId);
                        Log.d("RECEIVEDNEWCHAT", "new chat list message list is " + Integer.toString(Contacts.activeChat.size()));
                        recyclerView.swapAdapter(adapter, false);
                        recyclerView.setLayoutManager(layoutManager);
                        adapter.notifyDataSetChanged();
                    }else if(receivedMessages.startsWith("gr")){
                            Log.d("RECEIVEDNEWCHAT", receivedMessages);
                            mSocket.emit("get_group_chats", MasterUser.usersId);
                            recyclerView.swapAdapter(adapter, false);
                            recyclerView.setLayoutManager(layoutManager);
                            adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    };

    private Emitter.Listener groupDelete = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ContactsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String received = (String) args [0]; // id to delete from active chats
                    Log.d("DELETEEMITTER","Deleted here");

                    if(!Groups.groupList.isEmpty()) {
                        for (int i = 0; i < Groups.groupList.size(); i++) {
                            if (Groups.groupList.get(i).getOwnerId()== Integer.parseInt(received)){
                                Groups.groupList.remove(i);
                                Log.d("EMITTER","Deleted here");
                            }
                        }
                    }

                    mSocket.emit("get_group_chats", MasterUser.usersId);
                    recyclerView.swapAdapter(adapter, false);
                    recyclerView.setLayoutManager(layoutManager);
                    adapter.notifyDataSetChanged();
                    notificationRevealer("Group Add","You have been deleted from a group ",ContactsActivity.this);
                }
            });
        }
    };
    private Emitter.Listener contactDelete = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ContactsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String received = (String) args [0]; // id to delete from active chats

                    if(dm!=null&& dm.selectAllContacts().getCount()>0){
                        dm.deleteContact(received);
                        Log.d("DELETESTATUS", "i reached here");
                    }

                    if(!Contacts.contactList.isEmpty()) {
                        for (int i = 0; i < Contacts.contactList.size(); i++) {
                            if (Contacts.contactList.get(i).getUserId()== received){
                                Contacts.contactList.remove(i);
                                Log.d("EMITTER","Deleted here");
                            }
                        }
                    }

                    try {
                        if(InternetHandler.hasInternetConnection(ContactsActivity.this,1)==false){

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
                    }
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
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

                }
            });
        }
    };
    public static String roomnumber="";
    public static String grouproomnumber="";

    private Emitter.Listener notifications = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ContactsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String receivedMessages = (String) args [0]; // room number
                    Log.d("NOTIFICATIONVIEW",receivedMessages);
                    Log.d("NOTIFICATIONVIEW",roomnumber + "-=-=-=-=-=--");
                    if(grouproomnumber.equals(receivedMessages)){
                        return;
                    }

                    if(roomnumber.equals(receivedMessages)){
                        return;
                    }

                    if(!grouproomnumber.equals(receivedMessages)){
                        Log.d("LASTTEST","part1" + receivedMessages);
                        Log.d("LASTTEST","part2" + grouproomnumber);
                        int receivedMessages2 = (Integer) args [1]; // message id
                        Log.d("NOTIFICATIONVIEW",Integer.toString(receivedMessages2));
                        String receivedMessages3 = (String) args [2]; // username
                        Log.d("NOTIFICATIONVIEW",receivedMessages3);
                        String receivedMessages4 = (String) args [3]; // message
                        Log.d("NOTIFICATIONVIEW",receivedMessages4);
                        String receivedMessages5 = (String) args [4]; // timestamp
                        Log.d("NOTIFICATIONVIEW",receivedMessages5);
                        if(!man.getUsername().equals(receivedMessages3)) {
                            notificationRevealer(receivedMessages3, receivedMessages4, ContactsActivity.this);
                        }
                    }else if(!roomnumber.equals(receivedMessages)){

                        Log.d("LASTTEST","part1111" + receivedMessages);
                        Log.d("LASTTEST","part2222" + grouproomnumber);
                        int receivedMessages2 = (Integer) args [1]; // message id
                        Log.d("NOTIFICATIONVIEW",Integer.toString(receivedMessages2));
                        String receivedMessages3 = (String) args [2]; // username
                        Log.d("NOTIFICATIONVIEW",receivedMessages3);
                        String receivedMessages4 = (String) args [3]; // message
                        Log.d("NOTIFICATIONVIEW",receivedMessages4);
                        String receivedMessages5 = (String) args [4]; // timestamp
                        Log.d("NOTIFICATIONVIEW",receivedMessages5);
                        if(!man.getUsername().equals(receivedMessages3)) {
                            notificationRevealer(receivedMessages3, receivedMessages4, ContactsActivity.this);
                        }
                    }
                }
            });
        }
    };

    public static void notificationRevealer(String title, String content,Context con){
        notification.setSmallIcon(R.drawable.profile_logo);
        notification.setTicker("This is the ticker");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("Message from:" +title);
        notification.setContentText(content);

        notification.setSound(Uri.parse("android.resource://" + con.getPackageName() + "/" + R.raw.notification));
        showNotification(con);
    }

    public static void showNotification (Context con){
        Intent intent = new Intent(con,ContactsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(con,0,new Intent(),0);
        notification.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) con.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(uniqueId,notification.build());
    }

    private Emitter.Listener chatUpdater = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ContactsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String receivedMessages = (String) args [0];
                    Log.d("SENTNEWCHAT",receivedMessages);
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
                    recyclerView.swapAdapter(adapter, false);
                    recyclerView.setLayoutManager(layoutManager);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    };

    private Emitter.Listener youHaveBeenAdded = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ContactsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String sendersName = (String) args [0];
                    Log.d("GROUPPPP"," "+ sendersName);
                    Log.d("GROUPPPP"," "+ "Reached here part 1111");

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // this code will be executed after 2 seconds
                        }
                    }, 5000);
                   // mSocket.on("sent_group_chats", currentGroups);
                    mSocket.emit("get_group_chats", MasterUser.usersId);
                    recyclerView.swapAdapter(adapter, false);
                    recyclerView.setLayoutManager(layoutManager);
                    adapter.notifyDataSetChanged();
                    notificationRevealer("Group Add","You have been added to a group ",ContactsActivity.this);
                    Log.d("GROUPPPP"," "+ sendersName);
                }
            });
        }
    };

    private Emitter.Listener receivedContactRequest = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ContactsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String sendersName = (String) args [0];
                    Log.d("CONTACTREQUEST","received from"+ sendersName);
                    notificationRevealer("Friend Request","You have received a friend request from: " + sendersName,ContactsActivity.this);
                }
            });
        }
    };

    private Emitter.Listener refreshLayout = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ContactsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(InternetHandler.hasInternetConnection(ContactsActivity.this,1)==false){

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
                    }
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recyclerView.getRecycledViewPool().clear();
        adapter.notifyDataSetChanged();
        //mSocket.disconnect();  //re-comment out?
    }
}