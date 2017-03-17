package com.example.user.kchat01;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
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
    private RecyclerView recyclerView;
   // private SearchView searchView;
    private ChatsAdapter adapter;
    public static ArrayList<IMessage> dataList;
    private String username,message;
    private Socket mSocket;
    public static Bitmap contactsBitmap;
    private String userId;
    private int contactId;
    private CharSequence dateText;
    private int counter =2;
    public static boolean isAtTop = false;
    public static boolean didOverscroll = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textViewChatUser = (TextView) findViewById(R.id.textViewChatUser);
        // and show chatting username on toolber
        Intent intent = getIntent();
        String chatUser = intent.getStringExtra("type");
        if(chatUser!=null&&chatUser.equals("contact")){
            this.userId = intent.getStringExtra("userid");
            this.username = intent.getStringExtra("username");
            this.contactId = intent.getIntExtra("contactid", 0);
            byte [] byteArray = getIntent().getByteArrayExtra("contactbitmap");
            this.contactsBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        }
        try {
            mSocket = IO.socket("http://188.166.157.62:3000");
            mSocket.connect();
            mSocket.on("room_created",stringReply2);
            mSocket.emit("create_room", userId, MasterUser.usersId);
            mSocket.on("update_chat", serverReplyLogs); // sends server messages
            mSocket.on("send_recent_messages", getallmessages); // sends server messages

        } catch (URISyntaxException e){
        }

        textViewChatUser.setText(chatUser);
        textViewChatUser.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        dataList = new ArrayList<>();
        adapter = new ChatsAdapter(ChatsActivity.this,dataList);
        recyclerView.setAdapter(adapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

//
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                                             @Override
//                                             public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                                                 super.onScrolled(recyclerView, dx, dy);
//                                                 {
////                   Log.d("SCROLLING","i have reached the top");
////                    mSocket.emit("get_recent_messages",MasterUser.usersId,userId,20*counter);
////                    counter++;
//                                                 }
//                                             }
//                                         };

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
                        mSocket.emit("get_recent_messages",MasterUser.usersId,userId,20*counter);
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
                    //before i am outputting to the screen i will take the text of the message and any other user related information
                    //and i will send it to the server using the socket.io
                    if(mSocket.connected()){
                        mSocket.emit("send_chat",message);
                    }else{
                        Log.d("MESSAGEERROR", "Cannot send message:" + message);
                    }
                }
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
                    String receivedMessages = (String) args [0];
                    JsonDeserialiser messageDeserialise = new JsonDeserialiser(receivedMessages,"message",ChatsActivity.this);
                    Log.d("MESSAGEERROR", args[0].toString());
                    int latestPosition = adapter.getItemCount();
                    recyclerView.setAdapter(adapter);
                    adapter.notifyItemInserted(latestPosition);//"0" means insertion to the top of display
                    if(counter!=2){
                        stupidScrolling(false);
                    }else{
                        stupidScrolling(true);

                    }
                }
            });
        }
    };


    private void stupidScrolling(boolean type){

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
                mSocket.emit("add_user",receivedMessage,MasterUser.usersId);
                mSocket.on(receivedMessage,messageReceiver);

                mSocket.emit("get_recent_messages",MasterUser.usersId,userId,20);
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
                        recyclerView.setAdapter(adapter);
                        adapter.notifyItemInserted(latestPosition);//"0" means insertion to the top of display
                        stupidScrolling(true);
                    } catch (ParseException e) {
                    }
                }
            });
        }
    };


}