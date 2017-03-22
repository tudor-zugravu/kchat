package com.example.user.kchat01;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import API.IMessage;
import IMPL.JsonDeserialiser;
import IMPL.MasterUser;
import IMPL.Message;

/**
 * Created by user on 22/02/2017.
 */

/* This is main activity of chats */
    // For local test, sample data is generated in "ItemChats" class. "getObject" method calls the data in this activity.

public class ChatsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView textViewChatUser;
    private ImageButton imageUpload;
    private RecyclerView recyclerView;
    private ChatsAdapter adapter;
    public static ArrayList<IMessage> dataList;
    private String username,contactname,message;
    public static Bitmap contactsBitmap;
    private String userId;
    private int contactId;
    private CharSequence dateText;
    private int counter =2;
    public static boolean isAtTop = false;
    public static boolean didOverscroll = false;
    DataManager dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dm = new DataManager(ChatsActivity.this);

        setContentView(R.layout.activity_chats);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageUpload = (ImageButton)findViewById(R.id.imageUpload);
        textViewChatUser = (TextView) findViewById(R.id.textViewChatUser);
        dataList = new ArrayList<>();
        Intent intent = getIntent();
        String chatUser = intent.getStringExtra("type");
        if(chatUser!=null&&chatUser.equals("contact")){
            this.userId = intent.getStringExtra("userid");
            this.username = intent.getStringExtra("username");
            this.contactname = intent.getStringExtra("contactname");
            this.contactId = intent.getIntExtra("contactid", 0);

            byte [] byteArray = getIntent().getByteArrayExtra("contactbitmap");
            this.contactsBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
            if (contactname!=null) {
                textViewChatUser.setText(contactname);
            }else{
                textViewChatUser.setText("");
            }
            textViewChatUser.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));
        }


        if(InternetHandler.hasInternetConnection(ChatsActivity.this,0)==false){
            ContactsActivity.mSocket.disconnect();
            dm.selectAllPrivateMessages(Integer.parseInt(userId),MasterUser.usersId);
          //  recyclerView.setNestedScrollingEnabled(false);
        }else {
            ContactsActivity.mSocket.connect();
            ContactsActivity.mSocket.on("private_room_created", stringReply2);
            ContactsActivity.mSocket.emit("create_private_room", userId, MasterUser.usersId);
            ContactsActivity.mSocket.on("update_chat", serverReplyLogs); // sends server messages
            ContactsActivity.mSocket.on("send_recent_messages", getallmessages); // sends server messages
                Log.d("OFFLINE TESTER", "Did i reach here?");
        }
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        adapter = new ChatsAdapter(ChatsActivity.this,dataList);
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
                        ContactsActivity.mSocket.emit("get_recent_messages",MasterUser.usersId,userId,20*counter);
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

                    if (InternetHandler.hasInternetConnection(ChatsActivity.this, 2) == false) {
                    //insert into database
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        dm.insertIntoBufferTable(MasterUser.usersId,Integer.parseInt(userId),message,timestamp.toString(),"");
                        IMessage offlineMessage = new Message(MasterUser.usersId,Integer.parseInt(userId),message,timestamp.toString());
                        offlineMessage.setMe(true);
                        dataList.add(offlineMessage);
                        adapter.notifyDataSetChanged();
                    } else {
                        //before i am outputting to the screen i will take the text of the message and any other user related information
                        //and i will send it to the server using the socket.io
                        if (  ContactsActivity.mSocket.connected()) {
                            ContactsActivity.mSocket.emit("send_chat", message);
                        } else {
                            Log.d("MESSAGEERROR", "Cannot send message:" + message);
                        }
                    }
                }
            }
        });
        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String[] str_items = {
                        "CAMERA",
                        "FOLDER",
                        "CANCEL"};
                new AlertDialog.Builder(ChatsActivity.this)
                        .setTitle("What type of image to upload")
                        .setItems(str_items, new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch(which)
                                        {
                                            case 0:
                                                //Camera
                                                Toast.makeText(ChatsActivity.this,"Camera Selected", Toast.LENGTH_SHORT).show();
                                                break;
                                            case 1:
                                                //Folder
                                                Toast.makeText(ChatsActivity.this,"Camera Selected", Toast.LENGTH_SHORT).show();
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
    }

    private Emitter.Listener serverReplyLogs = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ChatsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String receivedMessage = (String) args [0];
                    Log.d("PRIVATECHAT", receivedMessage);
                }
            });
        }
    };

    private Emitter.Listener getallmessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ChatsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dataList.clear();
                 //   dm.deletePrivateContactMessages(userId,Integer.toString(MasterUser.usersId));
                    String receivedMessages = (String) args [0];
                    JsonDeserialiser messageDeserialise = new JsonDeserialiser(receivedMessages,"message",ChatsActivity.this);
                    Log.d("MESSAGEERROR", args[0].toString());
                    int latestPosition = adapter.getItemCount();
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                   // adapter.notifyItemInserted(latestPosition);//"0" means insertion to the top of display
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataList.clear();
        Log.d("DATALIST","roomnumber is:" + ContactsActivity.roomnumber);
        if(  ContactsActivity.mSocket!=null) {
            ContactsActivity.mSocket.off(ContactsActivity.roomnumber);
        }
        ContactsActivity.roomnumber = "";
        recyclerView.getRecycledViewPool().clear();
        adapter.notifyDataSetChanged();
    }

    private Emitter.Listener stringReply2 = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String receivedMessage = (String) args [0];
            if(receivedMessage.equals("fail")){
                // print error cannot connect
            }else {
                ContactsActivity.roomnumber = receivedMessage;
                ContactsActivity.mSocket.off(receivedMessage);
                ContactsActivity.mSocket.on(receivedMessage,messageReceiver);
                ContactsActivity.mSocket.emit("get_recent_messages",MasterUser.usersId,userId,20);
            }
            Log.d("PRIVATECHAT", receivedMessage);
        }
    };

    private Emitter.Listener messageReceiver = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ChatsActivity.this.runOnUiThread(new Runnable() {
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
                        Log.d("DATALIST","Size of my list is111111111:" + dataList.size());
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                       // adapter.notifyItemInserted(latestPosition);//"0" means insertion to the top of display
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
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.report) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(ChatsActivity.this);
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

}