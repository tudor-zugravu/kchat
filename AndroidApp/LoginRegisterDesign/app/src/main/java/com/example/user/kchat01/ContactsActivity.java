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
import android.widget.TextView;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import API.IGroups;
import IMPL.Groups;

import static com.example.user.kchat01.R.id.contacts;

/**
 * Created by user on 22/02/2017.
 */

/* This is main activity to create contacts */
    // For local test, sample data is generated in "ItemContacts" class. "getObject" method calls the data in this activity.

public class ContactsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private RecyclerView recyclerView;
    private SearchView searchView;
    ContactsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        // apply toolbar title
        toolbarTitle.setText("Contacts");
        toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // getObjectList is to generate sample data in ItemContacs class.
        adapter = new ContactsAdapter(ContactsActivity.this, Groups.getObjectList()) {
            //By clicking a card, the username is got
            @Override
            public void onClick(ContactsViewHolder holder) {
                int position = recyclerView.getChildAdapterPosition(holder.itemView);
                IGroups contact = Groups.getObjectList().get(position);
                //makeText(getApplicationContext(), "clicked= " + contact.getUsername(), Toast.LENGTH_SHORT).show();
                Intent contactsIntent = new Intent(getApplicationContext(), ChatsActivity.class);
                contactsIntent.putExtra("username", contact.getName());
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
        bottomBar.setDefaultTab(R.id.contacts);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {

            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.chats) {
                    //Intent chatsIntent = new Intent(getApplicationContext(),old_ChatActivity.class);
                    //startActivity(chatsIntent);
                    Toast.makeText(getApplicationContext(), "Chats", Toast.LENGTH_SHORT).show();
                }
                if (tabId == R.id.groups) {
                    //Intent groupIntent = new Intent(getApplicationContext(),GroupActivity.class);
                    //startActivity(groupIntent);
                    Toast.makeText(getApplicationContext(), "Group", Toast.LENGTH_SHORT).show();
                }
                if (tabId == contacts) {
                    //Intent contactsIntent = new Intent(getApplicationContext(), ContactsActivity.class);
                    //startActivity(contactsIntent);
                    //Toast.makeText(getApplicationContext(), "Contacts", Toast.LENGTH_SHORT).show();
                }
                if (tabId == R.id.profile) {
                    Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(profileIntent);
                    //Toast.makeText(getApplicationContext(), "Profile", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == contacts) {
                    Intent contactsIntent = new Intent(getApplicationContext(), ContactsActivity.class);
                    startActivity(contactsIntent);
                }
            }
        });

    /*
    From here, Bottom navigation settings
    Future work: implement as another class
    item0: Chats, item1: Groups, item2: Contacts, item3: Profile
    */
        //recognise the bottom navi
   /*
        final BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().getItem(0).setChecked(false);
        bottomNavigationView.getMenu().getItem(1).setChecked(false);
        bottomNavigationView.getMenu().getItem(2).setChecked(true);
        bottomNavigationView.getMenu().getItem(3).setChecked(false);

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
                                Intent chatIntent = new Intent(getApplicationContext(), old_ChatActivity.class);
                                startActivity(chatIntent);
                                break;
                            case R.id.groups:
                                bottomNavigationView.getMenu().getItem(0).setChecked(false);
                                bottomNavigationView.getMenu().getItem(1).setChecked(true);
                                bottomNavigationView.getMenu().getItem(2).setChecked(false);
                                bottomNavigationView.getMenu().getItem(3).setChecked(false);
                                Toast.makeText(getApplicationContext(), "groups is clicked.", Toast.LENGTH_LONG).show();
                                break;
                            case R.id.contacts:
                                bottomNavigationView.getMenu().getItem(0).setChecked(false);
                                bottomNavigationView.getMenu().getItem(1).setChecked(false);
                                bottomNavigationView.getMenu().getItem(2).setChecked(true);
                                bottomNavigationView.getMenu().getItem(3).setChecked(false);
                                Intent contactsIntent = new Intent(getApplicationContext(), ContactsListActivity.class);
                                startActivity(contactsIntent);
                                break;
                            case profile:
                                //finish();
                                bottomNavigationView.getMenu().getItem(0).setChecked(false);
                                bottomNavigationView.getMenu().getItem(1).setChecked(false);
                                bottomNavigationView.getMenu().getItem(2).setChecked(false);
                                bottomNavigationView.getMenu().getItem(3).setChecked(true);
                                Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                                startActivity(profileIntent);
                                break;
                        }
                        return true;
                    }

                });
*/
    }
}