package com.example.user.kchat01;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Socket;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import API.IContacts;
import API.IGroups;
import IMPL.Contacts;
import IMPL.Groups;
import IMPL.JsonDeserialiser;
import IMPL.MasterUser;
import IMPL.RESTApi;

/**
 * Created by user on 22/02/2017.
 */

/* This is main activity to create adding group page */
    // User list checked to add to the group is stored in the variable of "checkedString".

public class AddGroupActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolbarTitle, textViewDone; //"textViewDone" is clickable tv on toolbar
    private EditText editTextGroupNmame, editTextDescription;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private AddGroupAdapter adapter;
    private ArrayList<IContacts> memberList;
    private StringBuffer checkedString=null;
    private Socket mSocket;
    ArrayList<Integer> usersId = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MasterUser man = new MasterUser();
        if(man.getProfileLocation()!=null) {
            try {
                Log.d("CALLEDSTATUS","Getting the data from the server");

                String picture_url = "http://188.166.157.62/profile_pictures/" + "profile_picture" + man.getuserId() + ".jpg";
                String type = "getImage";
                ArrayList<String> paramList = new ArrayList<>();
                paramList.add("picture");
                RESTApi backgroundasync = new RESTApi(AddGroupActivity.this, picture_url, paramList);
                String result = backgroundasync.execute(type).get();
            }catch(InterruptedException e){
            }catch(ExecutionException f){
            }
        }
        Log.d("CALLEDSTATUS","resuming with the ui draw");



        setContentView(R.layout.activity_add_group);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        // apply toolbar title
        toolbarTitle.setText("Add Group");
        toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));

        try{
            Log.d("CALLEDSTATUS", "i made a rest request");
            String type2 = "getcontacts";
            String contacts_url = "http://188.166.157.62:3000/contacts";
            ArrayList<String> paramList2 = new ArrayList<>();
            paramList2.add("userId");
            RESTApi backgroundasync2 = new RESTApi(AddGroupActivity.this, contacts_url, paramList2);
            String result2 = backgroundasync2.execute(type2, man.getuserId()).get();
            JsonDeserialiser deserialiser = new JsonDeserialiser(result2,"getcontacts",AddGroupActivity.this);

        }catch(InterruptedException e){
        }catch(ExecutionException f){
        }

        adapter = new AddGroupAdapter(AddGroupActivity.this, Contacts.contactList);

        textViewDone = (TextView)findViewById(R.id.textViewDone);

        // set listener on "Done" text view in toolbar to get user list checked
        textViewDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get Group Name and Description
                editTextGroupNmame = (EditText)findViewById(R.id.editTextGroupName);
                editTextDescription = (EditText)findViewById(R.id.editTextGroupDescription);
                String groupName = editTextGroupNmame.getText().toString();
                String description = editTextDescription.getText().toString();

                // get Checkbox condition of adding group member. checking is operated in AddGroupAdapter class.
                // "checkedString" keep usernames to be checked
                checkedString = new StringBuffer();
                for (IContacts contact : adapter.checkedUsers) {
                    checkedString.append(contact.getContactName());
                    checkedString.append("\n");
                }

                if (groupName.isEmpty() || description.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddGroupActivity.this);
                    builder.setMessage("Group Name and Description fields are required.")
                            .setNegativeButton("Back", null)
                            .create()
                            .show();
                } else if (adapter.checkedUsers.size() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddGroupActivity.this);
                    builder.setMessage("Please select at least one user.")
                            .setNegativeButton("Back", null)
                            .create()
                            .show();
                } else {
                    //in actual application, these variables are sent to the server
                    Log.i("groupName", groupName);
                    Log.i("groupDescription", description);
                    Log.i("selected username", checkedString.toString());
                    if(AddGroupAdapter.checkedUsers!=null) {
                        for(int i=0; i<AddGroupAdapter.checkedUsers.size(); i++){
                            usersId.add(Integer.parseInt(AddGroupAdapter.checkedUsers.get(i).getUserId()));
                        }
                        IGroups group = new Groups(1,groupName,description,1,usersId);
                        Groups.groupList.add(group);
                        //after sending data, back to contact page
                        Toast.makeText(AddGroupActivity.this, "Group: " + groupName + "\n Description: " + description + "\n User: " + checkedString.toString() + " was added.", Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(getApplicationContext(), ContactsActivity.class);
                        startActivity(myIntent);
                    }
                }
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
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
    }

}