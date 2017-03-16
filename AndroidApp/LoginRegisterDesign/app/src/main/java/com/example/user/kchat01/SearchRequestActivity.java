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
import IMPL.MasterUser;

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
    private static Socket mSocket;

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
            adapter = new SearchRequestAdapter(SearchRequestActivity.this, Contacts.searchList, 0);

        }else if (type.equals("sendRequest")){
            mSocket.emit("sent_contact_requests",MasterUser.usersId);
            mSocket.on("sent_requests",contactManagement);
            toolbarTitle.setText("Sent Requests");
            toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));
            adapter = new SearchRequestAdapter(SearchRequestActivity.this, Contacts.sentRequests, 1);
        }else if (type.equals("receiveRequest")){

            mSocket.emit("received_contact_requests",MasterUser.usersId);
            mSocket.on("received_requests",contactManagement2);
            //make a request and show
            toolbarTitle.setText("Received Requests");
            toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));
            // to change this list to received requests list
            adapter = new SearchRequestAdapter(SearchRequestActivity.this, Contacts.receivedRequests, 2);
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
                MasterUser man = new MasterUser();
                mSocket.emit("search_user_filter",query,man.getuserId());
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
                    if(serverresult.equals("success")){
                        toolbarTitle.clearComposingText();
                    }
                    if (serverresult.equals("fail")||serverresult.equals("[]")) {
                        Contacts.searchList.clear();
                        adapter = new SearchRequestAdapter(SearchRequestActivity.this, Contacts.searchList, 0);
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);
                    }else {
                        JsonDeserialiser jsonDeserialiser = new JsonDeserialiser(serverresult, "filterlist", SearchRequestActivity.this);
                        adapter = new SearchRequestAdapter(SearchRequestActivity.this, Contacts.searchList, 0) {
                            //By clicking a card, show dialog whether sending request or not
                            @Override
                            public void onClick(SearchRequestViewHolder holder) {
                                int position = recyclerView.getChildAdapterPosition(holder.itemView);
                                final IContacts filteredContact = Contacts.searchList.get(position);
                                // when clicking one user, show dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(SearchRequestActivity.this);
                                builder.setTitle("Send Request Confirmation");
                                builder.setMessage("Do you send contact request to " + filteredContact.getUsername() + " ?");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // to change this to send request operation
                                        mSocket.emit("send_contact_request",MasterUser.usersId,filteredContact.getUserId());
                                        mSocket.on("sent_request",onlineJoin);
                                        searchView.setQuery(null,false);
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
                }
            });
        }
    };

    private Emitter.Listener contactManagement = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            SearchRequestActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Contacts.sentRequests.clear();
                    String serverresult = (String) args[0];
                    Log.d("AAAAAA", serverresult);
                    if (serverresult.equals("success")) {
                        //toolbarTitle.clearComposingText();
                        Log.d("BBBB", "hello");
                    } else {
                        JsonDeserialiser jsonDeserialiser = new JsonDeserialiser(serverresult, "getcontactrequests", SearchRequestActivity.this);
                        adapter = new SearchRequestAdapter(SearchRequestActivity.this, Contacts.sentRequests, 1) {
                            //By clicking a card, show dialog whether sending request or not
                            @Override
                            public void onClick(SearchRequestViewHolder holder) {
                                final int position = recyclerView.getChildAdapterPosition(holder.itemView);
                                final IContacts filteredContact = Contacts.sentRequests.get(position);
                                // when clicking one user, show dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(SearchRequestActivity.this);
                                builder.setTitle("Delete Request");
                                builder.setMessage("Do you want to delete this request to : " + filteredContact.getUsername() + " ?");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // to change this to send request operation
                                        mSocket.emit("delete_contact_request", MasterUser.usersId, filteredContact.getUserId(), "success");
                                        mSocket.on("request_deleted", contactManagement);
                                        Log.d("DELETE", Integer.toString(position));
                                        Contacts.sentRequests.remove(position);
                                        adapter.notifyDataSetChanged();
                                        recyclerView.setAdapter(adapter);
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
                }
            });
        }
    };


    private Emitter.Listener contactManagement2 = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            SearchRequestActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String serverresult = (String) args[0];
                    Log.d("BBBB",serverresult);
                    if(serverresult.equals("success")){
                        //toolbarTitle.clearComposingText();
                        Log.d("BBBB","hello");
                    }
                   else if (serverresult.equals("fail")||serverresult.equals("[]")) {
                        Contacts.receivedRequests.clear();
                        adapter = new SearchRequestAdapter(SearchRequestActivity.this, Contacts.receivedRequests, 0);
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);
                    }else {
                        Contacts.receivedRequests.clear();
                        JsonDeserialiser jsonDeserialiser = new JsonDeserialiser(serverresult, "getcontactinvites", SearchRequestActivity.this);
                    adapter = new SearchRequestAdapter(SearchRequestActivity.this, Contacts.receivedRequests, 2){
                        //By clicking a card, show dialog whether sending request or not
                        @Override
                        public void onClick(SearchRequestViewHolder holder) {
                            final int position = recyclerView.getChildAdapterPosition(holder.itemView);
                            final IContacts filteredContact = Contacts.receivedRequests.get(position);
                            // when clicking one user, show dialog
                            AlertDialog.Builder builder = new AlertDialog.Builder(SearchRequestActivity.this);
                            builder.setTitle("Accept Contact Confirmation");
                            builder.setMessage("Do you wish to accept this contact: " + filteredContact.getUsername() + " ?");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // to change this to send request operation
                                    mSocket.emit("accept_contact_request",filteredContact.getUserId(),MasterUser.usersId,"success");
                                    mSocket.on("request_accepted",contactManagement2);
                                    Contacts.receivedRequests.remove(position);
                                    adapter.notifyDataSetChanged();
                                    recyclerView.setAdapter(adapter);
                                }
                            });
                            builder.setNegativeButton("Cancel", null);
                            alertDialog = builder.create();
                            alertDialog.show();
                        }
                    };
                        adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }}
            });
        }
    };

}