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

import org.json.JSONArray;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;

import API.IMessage;
import IMPL.JsonSerialiser;
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
    private IMessage messageObject; // This is the object that represents each chat
    private ArrayList<IMessage> dataList;
    private String username,message;
    private Socket mSocket;
    private Bitmap contactsBitmap;
    private String userId;
    private int contactId;
    private CharSequence dateText;

    public void onMessageReceived(IMessage message){
    }
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
            Log.d("PRIVATECHAT","reached here");
            this.userId = intent.getStringExtra("userid");
            this.username = intent.getStringExtra("username");
            this.contactId = intent.getIntExtra("contactid", 0);
            byte [] byteArray = getIntent().getByteArrayExtra("contactbitmap");
            this.contactsBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        }
        try {
            mSocket = IO.socket("http://188.166.157.62:3000");
            mSocket.connect();
            //mSocket.on("updatechat", stringReply); // -<
            Log.d("PRIVATE_roomId", "room"+contactId);
            mSocket.on("room"+ contactId, stringReply); // -<
        } catch (URISyntaxException e){
        }

        textViewChatUser.setText(chatUser);
        textViewChatUser.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        dataList = new ArrayList<>();
        adapter = new ChatsAdapter(ChatsActivity.this,dataList);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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
                        Date date = new Date();

                        IMessage messageobj = new Message(MasterUser.usersId,0,Integer.parseInt(userId),message,date);
                        JsonSerialiser messageSerialize = new JsonSerialiser();
                       String messageResult= messageSerialize.serialiseMessage(messageobj,"0");
                        Log.d("PRIVATECHAT", "Reached here:" + userId);
                        mSocket.emit("sendchat",message);
                    }else{
                        Log.d("MESSAGEERROR", "Cannot send message:" + message);
                    }
                    mSocket.connect();

                    int latestPosition = adapter.getItemCount();
                    dateText  = android.text.format.DateFormat.format("E, kk:mm", java.util.Calendar.getInstance());
                    IMessage messageObject = new Message(1,message, String.valueOf(dateText),"user01",null);//This is used to add actual message
                    messageObject.setMe(true);
                    dataList.add(latestPosition, messageObject);//"0" means top of array
                    recyclerView.setAdapter(adapter);
                    adapter.notifyItemInserted(latestPosition);//"0" means insertion to the top of display
                    recyclerView.scrollToPosition(adapter.getItemCount()-1);
                }
            }
        });
        /*
        From here, search function is performed
         */
        /*
        searchView = (SearchView) findViewById(searchView);
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
        */
    }

    private Emitter.Listener messageRetreiver = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ChatsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String serverresult = (String) args[0];
                    Log.d("MESSAGEERROR", serverresult.toString());

                    int latestPosition = adapter.getItemCount();
                    //dateText  = android.text.format.DateFormat.format("E, dd/MM/ mm:ss", java.util.Calendar.getInstance());
                    IMessage messageObject = new Message(1, serverresult.toString(), String.valueOf(dateText), "user01", contactsBitmap);//This is used to add actual message
                    messageObject.setMe(false);//if the message is sender, set "true". if not, set "false".
                    dataList.add(latestPosition, messageObject);//"0" means top of array
                    recyclerView.setAdapter(adapter);
                    adapter.notifyItemInserted(latestPosition);//"0" means insertion to the top of display
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                }
            });
        }
    };

    private Emitter.Listener stringReply = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ChatsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String receivedMessage = (String) args [1];
                    Log.d("PRIVATECHAT", receivedMessage);
                    int latestPosition = adapter.getItemCount();
                    dateText  = android.text.format.DateFormat.format("E, kk:mm", java.util.Calendar.getInstance());
                    IMessage messageObject = new Message(1, receivedMessage, String.valueOf(dateText), "user01", contactsBitmap);//This is used to add actual message
                    messageObject.setMe(false);//if the message is sender, set "true". if not, set "false".
                    dataList.add(latestPosition, messageObject);//"0" means top of array
                    recyclerView.setAdapter(adapter);
                    adapter.notifyItemInserted(latestPosition);//"0" means insertion to the top of display
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                }
            });
        }
    };

    private Emitter.Listener jsonReply = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
           // JSONObject data = (JSONObject) args[0];
            JSONArray jsonArray = (JSONArray) args[0]; // Exception.
            Log.d("MESSAGEERROR", jsonArray.toString());

        }


            //JSONObject data = (JSONObject) args[0];
            // Log.d("MESSAGEERROR", data.toString());

            //  int numUsers;
//            try {
//               // numUsers = data.getInt("numUsers");
//            } catch (JSONException e) {
//                return;
//            }

    };
}