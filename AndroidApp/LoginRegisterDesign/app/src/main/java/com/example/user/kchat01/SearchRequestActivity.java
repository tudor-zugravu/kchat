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

import java.net.URISyntaxException;

import API.IContacts;
import IMPL.Contacts;
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
        Contacts.searchList.clear();
        try {
            mSocket = IO.socket("http://188.166.157.62:3000");
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
            adapter = new SearchRequestAdapter(SearchRequestActivity.this, Contacts.searchList, 0) {
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
                mSocket.on("search_user_received",onlineJoin);
                adapter.getFilter().filter(query);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    private Emitter.Listener onlineJoin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            SearchRequestActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Contacts.searchList.clear();
                    String serverresult = (String) args[0];
                    JsonDeserialiser jsonDeserialiser = new JsonDeserialiser(serverresult,"filterlist",SearchRequestActivity.this);

                    adapter = new SearchRequestAdapter(SearchRequestActivity.this, Contacts.searchList, 0) {
                        //By clicking a card, show dialog whether sending request or not
                        @Override
                        public void onClick(SearchRequestViewHolder holder) {
                            int position = recyclerView.getChildAdapterPosition(holder.itemView);
                            IContacts filteredContact = Contacts.searchList.get(position);
                            // when clicking one user, show dialog
                            AlertDialog.Builder builder = new AlertDialog.Builder(SearchRequestActivity.this);
                            builder.setTitle("Send Request Confirmation");
                            builder.setMessage("Do you send contact request to "+filteredContact.getContactName()+" ?");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // to change this to send request operation
                                    Toast.makeText(SearchRequestActivity.this, "sending request", Toast.LENGTH_SHORT).show();
                                }
                            });
                            builder.setNegativeButton("Cancel", null);
                            alertDialog = builder.create();
                            alertDialog.show();
                        }
                    };
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }
            });

        }
    };

    private Emitter.Listener jsonFilter = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
                String serverresult2 = (String) args[0];
                Log.d("FILTERLIST", serverresult2);


//            recyclerView.setAdapter(adapter); gives the error                                                                                      android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
//            android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.

        }
    };
}