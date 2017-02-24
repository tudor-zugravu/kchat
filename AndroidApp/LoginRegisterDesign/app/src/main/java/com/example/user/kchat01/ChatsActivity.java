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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

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
    private ItemChats chats;
    private ArrayList<ItemChats> dataList;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        dataList = ItemChats.getObjectList();
        adapter = new ChatsAdapter(getApplicationContext(), dataList);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.getItemCount()-1);

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

                    //set message and datetime to adapter and add them to RecyclerView
                    chats = new ItemChats();
                    chats.setIsMe(true);
                    chats.setMessage(message);
                    chats.setDateTime(java.text.DateFormat.getDateTimeInstance().format(new Date()));
                    int latestPosition = adapter.getItemCount();
                    dataList.add(latestPosition, chats);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyItemInserted(latestPosition);
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
}