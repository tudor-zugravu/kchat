package com.example.user.kchat01;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.example.user.kchat01.R.id.profile;

/**
 * Created by user on 17/02/2017.
 */

public class UserListActivity extends CustomActivity {

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private LinearLayoutManager ILayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);

        // apply toolbar title
        toolbarTitle.setText(R.string.toolbar_title);
        toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));

        List<ItemObject> rowListItem = getAllItemList();
        ILayout = new LinearLayoutManager(UserListActivity.this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.reciclerView);
        recyclerView.setLayoutManager(ILayout);

        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(UserListActivity.this, rowListItem);
        recyclerView.setAdapter(recyclerViewAdapter);

        //recognise the bottom navi.
        final BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        for (int i=0; i<4; i++){
            bottomNavigationView.getMenu().getItem(i).setEnabled(true);
            bottomNavigationView.getMenu().getItem(i).setChecked(false);
        }

        /*
    Bottom navigation settings
    Future work: implement as another class
    */

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.chats:
                                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                                bottomNavigationView.getMenu().getItem(1).setChecked(false);
                                bottomNavigationView.getMenu().getItem(2).setChecked(false);
                                bottomNavigationView.getMenu().getItem(3).setChecked(false);
                                Toast.makeText(getApplication(), "chats is clicked.", Toast.LENGTH_LONG).show();
                                break;
                            case R.id.groups:
                                bottomNavigationView.getMenu().getItem(0).setChecked(false);
                                bottomNavigationView.getMenu().getItem(1).setChecked(true);
                                bottomNavigationView.getMenu().getItem(2).setChecked(false);
                                bottomNavigationView.getMenu().getItem(3).setChecked(false);
                                Toast.makeText(getApplication(), "groups is clicked.", Toast.LENGTH_LONG).show();
                                break;
                            case R.id.contacts:
                                bottomNavigationView.getMenu().getItem(0).setChecked(false);
                                bottomNavigationView.getMenu().getItem(1).setChecked(false);
                                bottomNavigationView.getMenu().getItem(2).setChecked(true);
                                bottomNavigationView.getMenu().getItem(3).setChecked(false);
                                Toast.makeText(getApplication(), "contacts is clicked.", Toast.LENGTH_LONG).show();
                                break;
                            case profile:
                                //finish();
                                bottomNavigationView.getMenu().getItem(0).setChecked(false);
                                bottomNavigationView.getMenu().getItem(1).setChecked(false);
                                bottomNavigationView.getMenu().getItem(2).setChecked(false);
                                bottomNavigationView.getMenu().getItem(3).setChecked(true);
                                Intent profileIntent = new Intent(getApplication(), ProfileActivity.class);
                                startActivity(profileIntent);
                                //bottomNavigationView.getMenu().getItem(3).setCheckable(true);
                                //Toast.makeText(getApplication(), "profile is clicked.", Toast.LENGTH_LONG).show();
                                break;
                        }
                        return true;
                    }

                });


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
