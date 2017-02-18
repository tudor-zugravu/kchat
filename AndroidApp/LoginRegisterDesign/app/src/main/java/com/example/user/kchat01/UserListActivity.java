package com.example.user.kchat01;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 17/02/2017.
 */

public class UserListActivity extends CustomActivity {

    private Toolbar toolbar;
    private LinearLayoutManager ILayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        List<ItemObject> rowListItem = getAllItemList();
        ILayout = new LinearLayoutManager(UserListActivity.this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.reciclerView);
        recyclerView.setLayoutManager(ILayout);

        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(UserListActivity.this, rowListItem);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private List<ItemObject> getAllItemList() {
        List<ItemObject> allItems = new ArrayList<ItemObject>();
        allItems.add(new ItemObject("user01", "This is a hardcorded message.", R.drawable.human));
        allItems.add(new ItemObject("user02", "Hi How are you???", R.drawable.lock));
        allItems.add(new ItemObject("user03", "Yeah!", R.drawable.human));
        allItems.add(new ItemObject("user04", "Hi There!", R.drawable.lock));
        allItems.add(new ItemObject("user05", "I'm a user.", R.drawable.human));
        allItems.add(new ItemObject("user06", "Can you see me?", R.drawable.lock));
        allItems.add(new ItemObject("user07", "Good morning", R.drawable.human));
        allItems.add(new ItemObject("user08", "It's fine", R.drawable.lock));
        allItems.add(new ItemObject("user09", "Freezing", R.drawable.human));
        allItems.add(new ItemObject("user10", "Android programming is fun", R.drawable.lock));
        allItems.add(new ItemObject("user11", "How about you?", R.drawable.human));
        allItems.add(new ItemObject("user12", "This is new chat system", R.drawable.lock));
        allItems.add(new ItemObject("user13", "What did you do yesterday?", R.drawable.human));
        allItems.add(new ItemObject("user14", "Have fun!", R.drawable.lock));
        allItems.add(new ItemObject("user15", "Take care", R.drawable.human));
        allItems.add(new ItemObject("user16", "Bye!", R.drawable.lock));
        allItems.add(new ItemObject("user17", "See you tomorrow at uni", R.drawable.human));
        allItems.add(new ItemObject("user18", "Group project is good", R.drawable.lock));
        allItems.add(new ItemObject("user19", "Hello Kchat", R.drawable.human));
        allItems.add(new ItemObject("user20", "This message will be gotten from server", R.drawable.lock));

        return allItems;
    }
 }
