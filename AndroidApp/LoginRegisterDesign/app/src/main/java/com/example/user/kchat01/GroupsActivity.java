package com.example.user.kchat01;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;

import API.IGroups;
import IMPL.Groups;
import IMPL.MasterUser;
import IMPL.RESTApi;

import static com.example.user.kchat01.R.id.contacts;
import static com.example.user.kchat01.R.id.groups;

/**
 * Created by user on 22/02/2017.
 */

/* This is main activity to show groups */

public class GroupsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private RecyclerView recyclerView;
    private SearchView searchView;
    GroupsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MasterUser man = new MasterUser();
        if(man.getProfileLocation()!=null) {
            String picture_url = "http://188.166.157.62/profile_pictures/" + "profile_picture" + man.getuserId() + ".jpg";
            String type = "getImage";
            ArrayList<String> paramList= new ArrayList<>();
            paramList.add("picture");
            RESTApi backgroundasync = new RESTApi(GroupsActivity.this,picture_url,paramList);
            backgroundasync.execute(type);
        }

        setContentView(R.layout.activity_groups);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        // apply toolbar title
        toolbarTitle.setText("Groups");
        toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        adapter = new GroupsAdapter(GroupsActivity.this, Groups.getObjectList()) {
            //By clicking a card, the groupname is gotten
            @Override
            public void onClick(GroupsViewHolder holder) {
                int position = recyclerView.getChildAdapterPosition(holder.itemView);
                IGroups contact = Groups.getObjectList().get(position);
                //pass the groupname to chat page
                Intent contactsIntent = new Intent(getApplicationContext(), ChatsActivity.class);
                contactsIntent.putExtra("groupname", contact.getName());
                startActivity(contactsIntent);
            }
        };

        recyclerView.setAdapter(adapter);

        //set linearLayoutManager to recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());


        /*
        From here, search function is performed
         */

        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.onActionViewExpanded();
        searchView.setIconified(false);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });
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

        /*
        From here, Bottom Bar is implemented
        */

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomNavi);
        bottomBar.setDefaultTab(R.id.groups);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {

            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.chats) {
                    //Intent chatsIntent = new Intent(getApplicationContext(),old_ChatActivity.class);
                    //startActivity(chatsIntent);
                    Toast.makeText(getApplicationContext(), "Chats", Toast.LENGTH_SHORT).show();
                }
                if (tabId == groups) {
                    //Intent groupIntent = new Intent(getApplicationContext(),GroupsActivity.class);
                    //startActivity(groupIntent);
                }
                if (tabId == contacts) {
                    Intent contactsIntent = new Intent(getApplicationContext(), ContactsActivity.class);
                    startActivity(contactsIntent);
                }
                if (tabId == R.id.profile) {
                    Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                    MasterUser man = new MasterUser();
                    profileIntent.putExtra("users_username", man.getUsername());
                    profileIntent.putExtra("users_email", man.getEmail());
                    profileIntent.putExtra("users_phonenumber", man.getTelephonenumber());
                    profileIntent.putExtra("users_biography", man.getBiography());
                    startActivity(profileIntent);
                    //Toast.makeText(getApplicationContext(), "Profile", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == contacts) {
                    Intent contactsIntent = new Intent(getApplicationContext(), GroupsActivity.class);
                    startActivity(contactsIntent);
                }
            }
        });
    }

    //"Add Group" icon is deployed on toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.groups_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add) {
            Intent intent = new Intent(getApplicationContext(), AddGroupActivity.class);
            startActivity(intent);
        }
        return true;
    }



}