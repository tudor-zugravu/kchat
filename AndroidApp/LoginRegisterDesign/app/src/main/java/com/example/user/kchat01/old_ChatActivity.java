package com.example.user.kchat01;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
/**
 * Created by user on 19/02/2017.
 */

public class old_ChatActivity extends CustomActivity{

    private Toolbar toolbar;
    private TextView toolbarTitle, textviewChatUser;
    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private old_ChatsListAdapter adapter;
    private ArrayList<old_ListItemChat> chatHistory;

    @Override
    protected void onCreate (Bundle savedInstatnceState) {
        super.onCreate(savedInstatnceState);
        setContentView(R.layout.old_activity_chat);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        textviewChatUser = (TextView) findViewById(R.id.textViewChatUser);

        // apply toolbar title
        toolbarTitle.setText(R.string.toolbar_title);
        // set font type to toolbar title
        toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));

        /*
        From here, receive intent with username from ContactsActivity
        show chatting username under toolber title
         */
        Intent intent = getIntent();
        String chatUser = intent.getStringExtra("username");
        textviewChatUser.setText("Chat with " + chatUser);

        /*
        From here, display chat messages
         */

        initControls();
    }

    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.listViewChat);
        messageET = (EditText) findViewById(R.id.editTextMessage);
        sendBtn = (Button) findViewById(R.id.btn_sendMessage);

        RelativeLayout container = (RelativeLayout) findViewById(R.id.containerChat);

        loadDummyHistory();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                old_ListItemChat chatMessage = new old_ListItemChat();
                chatMessage.setId(122);//dummy
                chatMessage.setMessage(messageText);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(true);

                messageET.setText("");

                displayMessage(chatMessage);
            }
        });


    }

    public void displayMessage(old_ListItemChat message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void loadDummyHistory(){

        chatHistory = new ArrayList<old_ListItemChat>();

        old_ListItemChat msg = new old_ListItemChat();
        msg.setId(1);
        msg.setMe(false);
        msg.setMessage("Hi");
        msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg);

        old_ListItemChat msg1 = new old_ListItemChat();
        msg1.setId(2);
        msg1.setMe(true);
        msg1.setMessage("Hello! How are you????");
        msg1.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg1);

        old_ListItemChat msg2 = new old_ListItemChat();
        msg2.setId(3);
        msg2.setMe(false);
        msg2.setMessage("Fine! How about you?");
        msg2.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg2);

        old_ListItemChat msg3 = new old_ListItemChat();
        msg3.setId(4);
        msg3.setMe(true);
        msg3.setMessage("Really good!");
        msg3.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg3);

        old_ListItemChat msg4 = new old_ListItemChat();
        msg4.setId(5);
        msg4.setMe(false);
        msg4.setMessage("It was really fun playing with you last month.");
        msg4.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg4);

        old_ListItemChat msg5 = new old_ListItemChat();
        msg5.setId(6);
        msg5.setMe(true);
        msg5.setMessage("Well, the meal in particular was good flavour. If you have time, I'd like to go to dinner with you again.");
        msg5.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg5);

        adapter = new old_ChatsListAdapter(old_ChatActivity.this, new ArrayList<old_ListItemChat>());
        messagesContainer.setAdapter(adapter);

        for(int i=0; i<chatHistory.size(); i++) {
            old_ListItemChat message = chatHistory.get(i);
            displayMessage(message);
        }

    }

}
