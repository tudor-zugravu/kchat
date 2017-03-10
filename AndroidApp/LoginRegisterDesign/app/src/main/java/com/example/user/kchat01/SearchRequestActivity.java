package com.example.user.kchat01;

import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import API.IGroups;
import IMPL.Contacts;
import IMPL.Groups;
import IMPL.JsonDeserialiser;

/**
 * Created by user on 22/02/2017.
 */

/* This is main activity to search contacts, show sent requests and received requests */

public class SearchRequestActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private AlertDialog alertDialog;
    private RecyclerView recyclerView;
    private SearchView searchView;
    SearchRequestAdapter adapter;
    private Socket mSocket;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        try {
            mSocket = IO.socket("http://188.166.157.62:3000");
            mSocket.on("search_user_filter",jsonFilter);
            mSocket.connect();
        }catch (URISyntaxException e){
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_request);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");

        if (type.equals("searchContacts")){
            toolbarTitle.setText("Search Contacts");
            toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));
            // to change this list to search users list
            // Anyway, set listener on each user
            adapter = new SearchRequestAdapter(SearchRequestActivity.this, Groups.testList, 0) {
                //By clicking a card, show dialog whether sending request or not
                @Override
                public void onClick(SearchRequestViewHolder holder) {
                    int position = recyclerView.getChildAdapterPosition(holder.itemView);
                    IGroups group = Groups.testList.get(position);
                    // when clicking one user, show dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(SearchRequestActivity.this);
                    builder.setTitle("Send Request Confirmation");
                    builder.setMessage("Do you send contact request to "+group.getName()+" ?");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // to change this to send request operation
                            Toast.makeText(getApplicationContext(), "sending request", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    alertDialog = builder.create();
                    alertDialog.show();
                }
            };

        }else if (type.equals("sendRequest")){
            toolbarTitle.setText("Sent Requests");
            toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));
            // to change this list to sent requests list
            adapter = new SearchRequestAdapter(SearchRequestActivity.this, Contacts.contactList, 1);

        }else if (type.equals("receiveRequest")){
            toolbarTitle.setText("Received Requests");
            toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));
            // to change this list to received requests list
            adapter = new SearchRequestAdapter(SearchRequestActivity.this, Contacts.contactList, 2);
        }

        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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

                mSocket.emit("search_user_filter",query);
                adapter.getFilter().filter(query);
                return false;
            }
        });
    }

    private Emitter.Listener jsonFilter = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                JSONArray serverresult = new JSONArray(args[0]);
                String serverresult2 = (String) args[0];
                Log.d("FILTERLIST", serverresult2);

            }catch (JSONException e){

            }catch ( ClassCastException f){

            }
            //String serverresult = (String) args[0];
            Groups.testList.clear();

         //   JsonDeserialiser userSearchDeserialiser = new JsonDeserialiser();

            adapter = new SearchRequestAdapter(SearchRequestActivity.this, Groups.testList, 0) {
                //By clicking a card, show dialog whether sending request or not
                @Override
                public void onClick(SearchRequestViewHolder holder) {
                    int position = recyclerView.getChildAdapterPosition(holder.itemView);
                    IGroups group = Groups.testList.get(position);
                    // when clicking one user, show dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(SearchRequestActivity.this);
                    builder.setTitle("Send Request Confirmation");
                    builder.setMessage("Do you send contact request to "+group.getName()+" ?");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // to change this to send request operation
                            Toast.makeText(getApplicationContext(), "sending request", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    alertDialog = builder.create();
                    alertDialog.show();
                }
            };
            recyclerView.setAdapter(adapter);
        }
    };
}