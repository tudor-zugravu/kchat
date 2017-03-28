package com.example.user.kchat01;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import API.IContacts;
import API.IMessage;
import IMPL.Groups;
import IMPL.JsonDeserialiser;
import IMPL.MasterUser;
import IMPL.Message;

import static com.example.user.kchat01.R.id.leaveGroup;

/**
 * Created by user on 22/02/2017.
 */

/* This is main activity of chats */
    // For local test, sample data is generated in "ItemChats" class. "getObject" method calls the data in this activity.

public class GroupChatsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView textViewChatUser, textViewGroupDesc;
    private ImageButton imageUpload;
    private RecyclerView recyclerView;
   // private SearchView searchView;
    private ChatsAdapter adapter;
    public static ArrayList<IMessage> dataList;
    private String username,message,groupName,groupDescription,groupDescription2;
    public static Bitmap contactsBitmap;
    private int GROUPID; // this is the id of the group
    private String ownerId; // this is the id of the group
    private CharSequence dateText;
    private int counter =2;
    private ArrayList<Integer> usernames;
    public static boolean isAtTop = false;
    public static boolean didOverscroll = false;
    public static ArrayList <IContacts> contactsInChat;
    DataManager dm;
    public static String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        this.ownerId = intent.getStringExtra("ownerId");
        this.groupName = intent.getStringExtra("groupName");
        this.groupDescription = intent.getStringExtra("groupDesc");
        this.groupDescription2 = intent.getStringExtra("groupDesc2");//Description
        this.GROUPID = intent.getIntExtra("actualOwnerId",0);
        dataList = new ArrayList<>();
        usernames = new ArrayList<>();
        dm = new DataManager(GroupChatsActivity.this);
        groupId = ownerId;

        if(InternetHandler.hasInternetConnection(GroupChatsActivity.this,0)==false){
                ContactsActivity.mSocket.disconnect();
               dm.selectAllGroupMessages(Integer.parseInt(groupId),MasterUser.usersId);
//                recyclerView.setNestedScrollingEnabled(false);
            }else {
                ContactsActivity.mSocket.connect();
                ContactsActivity.mSocket.on("send_group_members", groupList);
                ContactsActivity.mSocket.emit("get_group_members", ownerId);
                ContactsActivity.mSocket.on("update_chat", serverReplyLogs); // sends server messages
                ContactsActivity.mSocket.on("send_recent_group_messages", getallmessages); // sends server messages
                ContactsActivity.mSocket.on("group_room_created", stringReply2);
                ContactsActivity.mSocket.emit("create_group_room", ownerId);
                ContactsActivity.mSocket.on("group_deleted",groupDeleted);
                ContactsActivity.mSocket.on("group_left",groupLeft);
            }

        setContentView(R.layout.activity_chats);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageUpload = (ImageButton)findViewById(R.id.imageUpload);
        textViewChatUser = (TextView) findViewById(R.id.textViewChatUser);
        textViewGroupDesc = (TextView) findViewById(R.id.textViewGroupDesc);

        textViewChatUser.setText(groupName);
        textViewChatUser.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));
        textViewGroupDesc.setText(groupDescription2);
        textViewGroupDesc.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String[] str_items = {
                        "CAMERA",
                        "FOLDER",
                        "CANCEL"};
                new AlertDialog.Builder(GroupChatsActivity.this)
                        .setTitle("What type of image to upload")
                        .setItems(str_items, new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch(which)
                                        {
                                            case 0:
                                                //Camera
                                                Toast.makeText(GroupChatsActivity.this,"Camera Selected", Toast.LENGTH_SHORT).show();
                                                break;
                                            case 1:
                                                //Folder
                                                Toast.makeText(GroupChatsActivity.this,"Folder Selected", Toast.LENGTH_SHORT).show();
                                                break;
                                            default:
                                                //Cancel
                                                break;
                                        }
                                    }
                                }
                        ).show();
            }
        });

        adapter = new ChatsAdapter(GroupChatsActivity.this,dataList,0);
        recyclerView.setAdapter(adapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    isAtTop = false;
                }
            }
                @Override
                public void onScrollStateChanged (RecyclerView recyclerView,int newState){
                super.onScrollStateChanged(recyclerView, newState);
                int firstVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition();
                    if (newState == 0) {
                        if (firstVisibleItem == 0) isAtTop = true;
                        else isAtTop = false;
                    }
                    if (newState == 1 && isAtTop) {
                        didOverscroll = true;
                    }
                    if (newState == 2 && isAtTop && didOverscroll) {
                        ContactsActivity.mSocket.emit("get_recent_group_messages",ownerId,20*counter);
                        counter++;
                        didOverscroll = false;
                    }
            }
            });

        //when inputting to EditText and pushing send button
        Button sendButton = (Button) findViewById(R.id.btn_sendMessage);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextMessage = (EditText) findViewById(R.id.editTextMessage);
                message = editTextMessage.getText().toString();
                if (message.equals("")) {
                    return;
                }else {
                    editTextMessage.setText("");
                    if (InternetHandler.hasInternetConnection(GroupChatsActivity.this, 2) == false) {
                        //insert into database
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        dm.insertIntoBufferTable(MasterUser.usersId, Integer.parseInt(groupId), message, timestamp.toString(), "");
                        IMessage offlineMessage = new Message(MasterUser.usersId, Integer.parseInt(groupId), message, timestamp.toString());
                        offlineMessage.setMe(true);
                        dataList.add(offlineMessage);
                        adapter.notifyDataSetChanged();
                    } else {
                        //before i am outputting to the screen i will take the text of the message and any other user related information
                        //and i will send it to the server using the socket.io
                        if (ContactsActivity.mSocket.connected()) {
                            ContactsActivity.mSocket.emit("send_group_chat", message);
                        } else {
                            Log.d("MESSAGEERROR", "Cannot send message:" + message);
                        }
                    }
                }
            }
        });
    }

    private Emitter.Listener serverReplyLogs = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            GroupChatsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String receivedMessage = (String) args [0];
                    Log.d("GROUPFUNCTION", receivedMessage);
                }
            });
        }
    };

    private Emitter.Listener getallmessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            GroupChatsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dataList.clear();
                    String receivedMessages = (String) args [0];
                   JsonDeserialiser messageDeserialise = new JsonDeserialiser(receivedMessages,"groupmessage",GroupChatsActivity.this);
                    Log.d("GROUPFUNCTION", "-----"+ args[0].toString());

                    int latestPosition = adapter.getItemCount();
                    recyclerView.setAdapter(adapter);
                  //  adapter.notifyItemInserted(latestPosition);//"0" means insertion to the top of display
                    adapter.notifyDataSetChanged();
                    if(counter!=2){
                        scrolling(false);
                    }else{
                        scrolling(true);
                    }
                }
            });
        }
    };


    private void scrolling(boolean type){
        if(type==true){
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        }else{
            recyclerView.scrollToPosition(0);
        }
    }

    private Emitter.Listener stringReply2 = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String receivedMessage = (String) args [0];
            if(receivedMessage.equals("fail")){
                Log.d("GROUPFUNCTION", "---I HAVE FAILED HERE -----");

            }else {
                Log.d("Chickensz","roomnumber is:" + ContactsActivity.roomnumber);
                Log.d("Chickensz","group room number is  is:" + receivedMessage);

                //the rooms id is received
                ContactsActivity.mSocket.on(receivedMessage,messageReceiver);
                Log.d("GROUPFUNCTION", "---REACHED HERE PART 0-----");
                ContactsActivity.mSocket.emit("get_recent_group_messages",ownerId,20);
            }
            Log.d("GROUPFUNCTION", receivedMessage);
        }
    };

    private Emitter.Listener groupList = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String receivedMessage = (String) args [0];
            if(receivedMessage.equals("fail")){

            }else {
              Log.d("GROUPFUNCTION",receivedMessage);
                JsonDeserialiser messageDeserialise = new JsonDeserialiser(receivedMessage,"getgroupcontacts",GroupChatsActivity.this);
               contactsInChat = messageDeserialise.groupContactDdeserialiser();
                for(int i=0; i<contactsInChat.size(); i++){
                    Log.d("Chickenz","id is+   " + contactsInChat.get(i).getUserId());
                    Log.d("Chickenz","name  is+   " + contactsInChat.get(i).getContactName());
                }
            }
        }
    };

    private Emitter.Listener messageReceiver = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            GroupChatsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int messageid = (int) args[0];
                    String username = (String) args[1];
                    String message = (String) args[2];
                    String timestamp = (String) args[3];
                    int latestPosition = adapter.getItemCount();
                    try {
                        Date date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(timestamp);
                        String newDate = new SimpleDateFormat("dd MMM HH:mm").format(date);
                        IMessage messageObject = new Message(messageid, Integer.parseInt(username), message, newDate);//This is used to add actual message
                        if (Integer.parseInt(username) == MasterUser.usersId) {
                            messageObject.setMe(true);//if the message is sender, set "true". if not, set "false".
                        } else {
                            messageObject.setMe(false);//if the message is sender, set "true". if not, set "false".
                        }
                        dataList.add(latestPosition, messageObject);//"0" means top of array
                        recyclerView.setAdapter(adapter);
                        //adapter.notifyItemInserted(latestPosition);//"0" means insertion to the top of display
                        adapter.notifyDataSetChanged();
                        scrolling(true);
                    } catch (ParseException e) {
                    }
                }
            });
        }
    };
    //MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_group_menu, menu);
        if(MasterUser.usersId!=GROUPID){ // if i am not the owner

            MenuItem item = menu.findItem(R.id.addContact);
            item.setVisible(false);
            MenuItem item2 = menu.findItem(R.id.deleteGroup);
            item2.setVisible(false);
            }else{
            MenuItem item = menu.findItem(R.id.addContact);
            if(contactsInChat!=null&&contactsInChat.size()>5){ // if i am not the owner
                item.setVisible(false);
            }else{
                item.setVisible(true);
            }
            MenuItem item2 = menu.findItem(R.id.deleteGroup);
            item2.setVisible(true);
            MenuItem item3 = menu.findItem(R.id.leaveGroup);
            item3.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == leaveGroup) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(GroupChatsActivity.this);
            builder1.setTitle("Leave Group Confirmation");
            builder1.setMessage("Do you want to leave group: " + groupName + " ?");
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(ContactsActivity.mSocket.connected()){
                                Log.d("Chickenz","leaving the group part 1");
                                ContactsActivity.mSocket.emit("leave_group",MasterUser.usersId, ownerId);
                            }
                            dialog.cancel();
                        }
                    });
            builder1.setNegativeButton("Cancel", null);
            AlertDialog alert11 = builder1.create();
            alert11.show();
            return true;
        } else if (id == R.id.addContact) {
            Intent addContactIntent = new Intent(getApplicationContext(), AddContactActivity.class);
            addContactIntent.putExtra("ownerId", ownerId);
            addContactIntent.putExtra("groupName", groupName);
            addContactIntent.putExtra("contactsizenum", contactsInChat.size());
            for(int i = 0; i<contactsInChat.size(); i++){
                usernames.add(Integer.parseInt(contactsInChat.get(i).getUserId()));
            }
            addContactIntent.putExtra("usernames", usernames);
            Log.d("Chicken","chick77777   , contacts in chat is---- " + usernames.size());
            startActivity(addContactIntent);
            return true;

        } else if (id == R.id.report) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(GroupChatsActivity.this);
            builder1.setTitle("Report Confirmation");
            builder1.setMessage("Do you report conversation?");
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Need to implement REPORT TO SERVER
                            dialog.cancel();
                        }
                    });
            builder1.setNegativeButton("Cancel", null);
            AlertDialog alert11 = builder1.create();
            alert11.show();
            return true;
        }else if (id == R.id.deleteGroup) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(GroupChatsActivity.this);
            builder1.setTitle("Delete Group Confirmation");
            builder1.setMessage("Do you wish to delete this group?");
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(MasterUser.usersId==GROUPID) {
                                dialog.cancel();
                                ContactsActivity.mSocket.emit("delete_group",MasterUser.usersId,groupId);
                            }

                        }
                    });
            builder1.setNegativeButton("Cancel", null);
            AlertDialog alert11 = builder1.create();
            alert11.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private Emitter.Listener groupDeleted = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            GroupChatsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String received = (String) args[0]; // id to delete from active chats

                    if( received.equals("success")){
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        Context context = getApplicationContext();
                        CharSequence text = "Group has been sucessfully deleted";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, text, duration);
                        for(int i = 0; i<Groups.groupList.size();i++){
                            if(Groups.groupList.get(i).getOwnerId()==Integer.parseInt(groupId)){
                                Groups.groupList.remove(i);
                            }
                        }
                        toast.show();
                        finish();
                    }else if (received.equals("fail")){
                        Context context = getApplicationContext();
                        CharSequence text = "Cannot delete group problem with the server";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                }
            });

        }
    };

    private Emitter.Listener addedContacts = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            GroupChatsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String received = (String) args[0]; // id to delete from active chats

                }
            });

        }
    };

    private Emitter.Listener groupLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            GroupChatsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String received = (String) args[0]; // id to delete from active chats

                    if(received.equals("success")){
                        for(int i=0; i<Groups.groupList.size(); i++){
                            if(Groups.groupList.get(i).getOwnerId()==Integer.parseInt(ownerId)){
                                Groups.groupList.remove(i);
                            }
                        }
                        finish();
                        Toast.makeText(GroupChatsActivity.this, "You have left the group " + groupName+" successfully", Toast.LENGTH_LONG).show();
                        return;
                    }else if (received.equals("fail")){
                        Toast.makeText(GroupChatsActivity.this, "Cannot leave the group " + groupName+" problem with server", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Toast.makeText(GroupChatsActivity.this, "Server Problem", Toast.LENGTH_LONG).show();
                }
            });

        }
    };



    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataList.clear();
        Log.d("Chickensz","roomnumber is:" + ContactsActivity.roomnumber);

        Log.d("DATALIST","roomnumber is:" + ContactsActivity.roomnumber);
        if(  ContactsActivity.mSocket!=null) {
            ContactsActivity.mSocket.off(ContactsActivity.roomnumber);
        }
        ContactsActivity.roomnumber = "";
        recyclerView.getRecycledViewPool().clear();
        adapter.notifyDataSetChanged();
    }



}