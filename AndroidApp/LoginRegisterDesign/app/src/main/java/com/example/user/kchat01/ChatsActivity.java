package com.example.user.kchat01;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
    private SearchView searchView;
    private ChatsAdapter adapter;
    private IMessage messageObject; // This is the object that represents each chat
    private ArrayList<IMessage> dataList;
    private String message;
    private Socket mSocket;

    public void onMessageReceived(IMessage message){


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mSocket = IO.socket("http://188.166.157.62:3000");
            mSocket.connect();
            mSocket.on("chat message", stringReply);
            mSocket.on("private_chat", messageRetreiver);
            mSocket.on("updaterooms", jsonReply);
            mSocket.emit("adduser", "Tudor");

        } catch (URISyntaxException e){
        }


        setContentView(R.layout.activity_chats);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textViewChatUser = (TextView) findViewById(R.id.textViewChatUser);

        // apply toolbar textview to chat user
        // For that, receive intent with username from ContactsActivity
        // and show chatting username on toolber
        Intent intent = getIntent();
        String chatUser = intent.getStringExtra("username");
        textViewChatUser.setText(chatUser);
        textViewChatUser.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // For local test, insert sample data which is generated ItemChats.java and
        // getObjectList method has the data.
        dataList = new ArrayList<>();
        //dataList = Message.getObjectList();
        adapter = new ChatsAdapter(ChatsActivity.this,dataList);

        recyclerView.setAdapter(adapter);
        //recyclerView.scrollToPosition(adapter.getItemCount()-1);

        //set linearLayoutManager to recyclerView
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
                        IMessage messageobj = new Message(MasterUser.usersId,0,MasterUser.usersId,message,date);
                        JsonSerialiser messageSerialize = new JsonSerialiser();
                       String messageResult= messageSerialize.serialiseMessage(messageobj,"0");
                        mSocket.emit("private_chat", messageResult);
                    }else{
                        Log.d("MESSAGEERROR", "Cannot send message:" + message);
                    }
                    mSocket.connect();

                    //set message and datetime to adapter and add them to RecyclerView
                   // message = new IMPL.Message();
                    int latestPosition = adapter.getItemCount();
                    IMessage messageObject = new Message(1,message,new Date(),"user01",R.drawable.human);//This is used to add actual message
                    messageObject.setMe(true);
                    dataList.add(0, messageObject);//"0" means top of array
                    recyclerView.setAdapter(adapter);
                    adapter.notifyItemInserted(0);//"0" means insertion to the top of display
                    recyclerView.scrollToPosition(adapter.getItemCount()-1);
                }
            }
        });

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
                    IMessage messageObject = new Message(1, serverresult.toString(), new Date(), "user01", R.drawable.human);//This is used to add actual message
                    messageObject.setMe(false);//if the message is sender, set "true". if not, set "false".
                    dataList.add(0, messageObject);//"0" means top of array
                    recyclerView.setAdapter(adapter);
                    adapter.notifyItemInserted(0);//"0" means insertion to the top of display
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                }
            });
        }
    };

    private Emitter.Listener stringReply = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            IMessage message = (IMessage) args[0];
            Log.d("PRIVATECHAT", message.getMessage() + "  " + message.getSenderId());
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