package com.example.user.kchat01;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

import API.IContacts;
import API.IGroups;
import IMPL.Contacts;
import IMPL.Groups;

/**
 * Created by user on 22/02/2017.
 */

/* This class is the adapter to deal with profile image and text in user contacts */
// ItemContacts type is defined as another class in the same package

public class ContactsAdapter extends RecyclerView.Adapter<ContactsViewHolder> implements ContactsViewHolder.ContactsViewHolderListener, Filterable{

    private LayoutInflater inflater;
    public ArrayList filterList;
    private ContactsFilter filter;
    private int type;
    Context context;

    //constructor
    public ContactsAdapter(Context context, ArrayList<?> objectList, int type) {
        inflater = LayoutInflater.from(context);
        this.filterList = objectList;
        this.type = type;
        this.context = context;
    }

    //create view holder
    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //convert item layout to view object
        View view = inflater.inflate(R.layout.item_contacts, parent, false);
        //create view holder
        ContactsViewHolder holder = new ContactsViewHolder(view);
        return holder;
    }

    //bind data to view
    @Override
    public void onBindViewHolder(ContactsViewHolder holder, int position) {
            if(type == 0) { // to change this to chats
            IGroups current = (IGroups) filterList.get(position);
            holder.imageProfile.setImageResource(current.getImageId());
            holder.textViewUsername.setText(current.getName());
            holder.textViewMessage.setText(current.getDescription());
        }if (type ==1){ // contacts
            if(!Contacts.contactList.isEmpty()) {
                IContacts current2 = (IContacts) filterList.get(position);
                if(Contacts.contactList.get(position).getBitmap()==null) {
                    Drawable d = ContextCompat.getDrawable(context, R.drawable.profile_logo);
                    holder.imageProfile.setImageDrawable(d);
                }else{
                    holder.imageProfile.setImageBitmap(current2.getBitmap());
                }
                holder.textViewUsername.setText(current2.getContactName());
                holder.textViewMessage.setText(current2.getEmail());
                }
        }if (type ==2){ // groups
            if(!Groups.groupList.isEmpty()) {
                //for (int i = 0; i < Contacts.contactList.size(); i++) {
                Log.d("DATACHECKER", " Ihave got here for the data checker");

                if(Groups.groupList.get(position).getGroupImage()==null) {
                    Log.d("PROFILE", " Ihave got here for the data checker"+Contacts.contactList.get(position).getContactName()+" the image is null");
                    Drawable d = ContextCompat.getDrawable(context, R.drawable.profile_logo);
                    holder.imageProfile.setImageDrawable(d);
                }else{
                    //holder.imageProfile.setImageBitmap(Contacts.contactList.get(position).getBitmap());
                    holder.imageProfile.setImageBitmap(Groups.groupList.get(position).getGroupImage());
                }
                holder.textViewUsername.setText(Groups.groupList.get(position).getName());
                holder.textViewMessage.setText(String.valueOf(Groups.groupList.get(position).getDescription()));

            }

        }
        //set click listener
        holder.setListener(this);
    }

    //only definition
    @Override
    public void onClick(ContactsViewHolder holder){
    }

    @Override
    public void onLongClick(ContactsViewHolder holder){
    }


    @Override
    public int getItemCount() {
        return filterList.size();
    }


    //filter
    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new ContactsFilter(filterList, this);
        }
        return filter;
    }
}
