package com.example.user.kchat01;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import API.IContacts;
import API.IMessage;
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
    private TextView textViewChatUser;
    private ImageButton imageUpload;
    private RecyclerView recyclerView;
   // private SearchView searchView;
    private ChatsAdapter adapter;
    public static ArrayList<IMessage> dataList;
    private String username,message, groupName,groupDescription;
    private Socket mSocket;
    public static Bitmap contactsBitmap;
    private String ownerId;
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
        this.usernames = intent.getIntegerArrayListExtra("usernames");
        this.groupName = intent.getStringExtra("groupName");
        this.groupDescription = intent.getStringExtra("groupDesc");
        dataList = new ArrayList<>();
        dm = new DataManager(GroupChatsActivity.this);
        groupId = ownerId;
        try {
            mSocket = IO.socket("http://188.166.157.62:3000");
        } catch (URISyntaxException e){
        }

            if(InternetHandler.hasInternetConnection(GroupChatsActivity.this,0)==false){
                mSocket.disconnect();
               dm.selectAllGroupMessages(Integer.parseInt(groupId),MasterUser.usersId);
//                recyclerView.setNestedScrollingEnabled(false);
            }else {
                mSocket.connect();
                mSocket.on("send_group_members", groupList);
                mSocket.emit("get_group_members", ownerId);
                mSocket.on("update_chat", serverReplyLogs); // sends server messages
                mSocket.on("send_recent_group_messages", getallmessages); // sends server messages
                mSocket.on("group_room_created", stringReply2);
                mSocket.emit("create_group_room", ownerId);
            }

        setContentView(R.layout.activity_chats);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageUpload = (ImageButton)findViewById(R.id.imageUpload);
        textViewChatUser = (TextView) findViewById(R.id.textViewChatUser);

        textViewChatUser.setText(groupName);
        textViewChatUser.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        imageUpload.setOnClickListener(new ImageListener(this,"GroupChat"));
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
                        mSocket.emit("get_recent_group_messages",ownerId,20*counter);
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
                        if (mSocket.connected()) {
                            mSocket.emit("send_group_chat", message);
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
                // print error cannot connect
            }else {
                //the rooms id is received
                mSocket.on(receivedMessage,messageReceiver);
                mSocket.emit("get_recent_group_messages",ownerId,20);
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
                   Log.d("GROUPFUNCTION", "size of contacts in chat is " + contactsInChat.size());
            }
         //   Log.d("GROUPFUNCTION", receivedMessage);
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
                            Toast.makeText(GroupChatsActivity.this, "GroupID: " + ownerId + "\n UserID: " + MasterUser.usersId, Toast.LENGTH_SHORT).show();
//                            if(mSocket.connected()){
//                                mSocket.emit("key of leave group",groupid, userID);
//                            }else{
//                                Log.d("MESSAGEERROR", "Cannot leave group");
//                            }
                            dialog.cancel();
                        }
                    });
            builder1.setNegativeButton("Cancel", null);
            AlertDialog alert11 = builder1.create();
            alert11.show();
            return true;
        } else if (id == R.id.addContact) {
//            Log.d("ADDCONTACT_usernames", String.valueOf(usernames.size()));
            Intent addContactIntent = new Intent(getApplicationContext(), AddContactActivity.class);
            addContactIntent.putExtra("ownerId", ownerId);
            addContactIntent.putExtra("usernames", usernames);
            addContactIntent.putExtra("groupName", groupName);
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
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onDestroy() {
        super.onDestroy();
        groupId =null;
        dataList.clear();
        Log.d("DATALIST","roomnumber is:" + ContactsActivity.roomnumber);
        if(mSocket!=null) {
            mSocket.off(ContactsActivity.roomnumber);
        }
        ContactsActivity.roomnumber = "";
        recyclerView.getRecycledViewPool().clear();
        adapter.notifyDataSetChanged();
    }
}