package com.example.user.kchat01;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import API.IContacts;
import IMPL.Contacts;

/**
 * Created by user on 22/02/2017.
 */

/* This class is the adapter to deal with users to add to new group, simply by checking box*/

public class AddGroupAdapter extends RecyclerView.Adapter<AddGroupViewHolder> implements Filterable{

    private LayoutInflater inflater;
    //public ArrayList<IContacts> objectList;
    public ArrayList<IContacts> filterList;
    public ArrayList<Integer> groupUsersId;
    public static ArrayList<IContacts> checkedUsers=new ArrayList<>();
    private AddGroupViewHolder holder;
    AddGroupFilter filter;
    Context context;
    private int groupLimit = 0;
    private int type=-1;


    //constructor
    public AddGroupAdapter(Context context, ArrayList<IContacts> objectList) {
        inflater = LayoutInflater.from(context);
      //  this.objectList = objectList;
        this.filterList = objectList;
        this.context = context;
    }

    //constructor
    public AddGroupAdapter(Context context, ArrayList<IContacts> objectList, int type) {
        inflater = LayoutInflater.from(context);
        //  this.objectList = objectList;
        this.filterList = objectList;
        this.context = context;
        this.type = type;
    }

    //create view holder
    @Override
    public AddGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //convert item layout to view object
        View view = inflater.inflate(R.layout.item_add_group, parent, false);
        //create view holder
        AddGroupViewHolder holder = new AddGroupViewHolder(view);
        return holder;
    }

    //bind data to view including listener to obtain user checked box
    @Override
    public void onBindViewHolder(AddGroupViewHolder holder, int position) {
        for (int i = 0; i < Contacts.contactList.size(); i++) {
            String result = Contacts.contactList.get(i).getContactName();
            Log.d("DATACHECKER", " Iha here for the data checker --->>>" + result );
        }
        final IContacts current = (IContacts)filterList.get(position);

        if(Contacts.contactList.get(position).getBitmap()==null) {
            Drawable d = ContextCompat.getDrawable(context, R.drawable.profile_logo);
            holder.imageProfile.setImageDrawable(d);
        }else{
            holder.imageProfile.setImageBitmap(current.getBitmap());
        }
        holder.textViewUsername.setText(current.getContactName());
        holder.checkBox.setTag(current);

        holder.setAddGroupListener(new AddGroupViewHolder.AddGroupListener() {
            @Override
            public void onItemClick(View view, int position) {
                CheckBox cb = (CheckBox) view;

                if(cb.isChecked()){
                    groupLimit = groupLimit + 1;
                    if (type == -1) { // ADD Group
                        if (groupLimit > 6) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                            AddGroupActivity.textViewDone.setVisibility(TextView.GONE);
                            builder1.setTitle("Cannot create Group");
                            builder1.setMessage("You have added too many contacts, please choose less than 6 contacts.");
                            builder1.setCancelable(true);
                            builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        } else {
                            AddGroupActivity.textViewDone.setVisibility(TextView.VISIBLE);
                        }
                    }else if (type == 1){ // Add Contact to group
                        checkedUsers.clear();
                        // TO check the number of current members in group
//                        Log.d("ADDCONTACT_GROUP_MEM", String.valueOf(groupUsersId.size()));
                        if (groupLimit > 6) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                            AddContactActivity.textViewDone.setVisibility(TextView.GONE);
                            builder1.setTitle("Cannot add the contact");
                            builder1.setMessage("This group will have too many contacts, please choose less than 6 contacts.");
                            builder1.setCancelable(true);
                            builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        } else {
                            AddContactActivity.textViewDone.setVisibility(TextView.VISIBLE);
                        }
                    }
                    checkedUsers.add(current);
                }else if (!cb.isChecked()){
                    groupLimit = groupLimit - 1 ;
                    if (type == -1) { //ADD Group
                        if (groupLimit < 6) {
                            AddGroupActivity.textViewDone.setVisibility(TextView.GONE);
                        } else {
                            AddGroupActivity.textViewDone.setVisibility(TextView.VISIBLE);
                        }
                    }else if (type == 1){  //Add Contact to group
                        if (groupLimit < 6) {
                            AddContactActivity.textViewDone.setVisibility(TextView.GONE);
                        } else {
                            AddContactActivity.textViewDone.setVisibility(TextView.VISIBLE);
                        }
                    }
                    checkedUsers.remove(current);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        //return objectList.size();
        return filterList.size();
    }

    // return selected user list to MainActivity after being checked
    public ArrayList<IContacts> getUserList(){
        //return objectList;
        return filterList;
    }

    //filter
    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new AddGroupFilter(filterList, this);
        }
        return filter;
    }
}
