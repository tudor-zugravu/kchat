package com.example.user.kchat01;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
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

import API.IGroups;
import IMPL.Contacts;

/**
 * Created by user on 22/02/2017.
 */

/* This class is the adapter to deal with profile image and text in user contacts */
// ItemContacts type is defined as another class in the same package

public class ContactsAdapter extends RecyclerView.Adapter<ContactsViewHolder> implements ContactsViewHolder.ContactsViewHolderListener, Filterable{

    private LayoutInflater inflater;
    public ArrayList objectList;
    private ArrayList filterList;
    private ContactsFilter filter;
    private int type;
    Context context;
    static int counter = 0;

    public void swap(ArrayList<?> datas){
        objectList.clear();
        objectList.addAll(datas);
        filterList.clear();
        filterList.addAll(datas);
        notifyDataSetChanged();
    }

    //constructor
    public ContactsAdapter(Context context, ArrayList<?> objectList, int type) {
        inflater = LayoutInflater.from(context);
        this.objectList = objectList;
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

        for (int i = 0; i < Contacts.contactList.size(); i++) {
            String result = Contacts.contactList.get(i).getContactName();
            Log.d("DATACHECKER", " Iha here for the data checker --->>>" + result );
        }

            if(type == 0) {
            IGroups current = (IGroups) objectList.get(position);
            holder.imageProfile.setImageResource(current.getImageId());
            holder.textViewUsername.setText(current.getName());
            holder.textViewMessage.setText(current.getDescription());
        }if (type ==1){
            if(!Contacts.contactList.isEmpty()) {
                //for (int i = 0; i < Contacts.contactList.size(); i++) {
                    Log.d("DATACHECKER", " Ihave got here for the data checker");
                    Log.d("DATACHECKER", " counter value:" + this.counter);
                if(Contacts.contactList.get(position).getBitmap()!=null){
                    Log.d("picturestatus", " i have a picture");
                    // new BitmapDrawable(getResources(), bitmap);
                    Drawable d = new BitmapDrawable(Contacts.contactList.get(position).getBitmap());
                    holder.imageProfile.setImageDrawable(d);
                }else {
                    Drawable d = ContextCompat.getDrawable(context, R.drawable.profile_logo);
                    holder.imageProfile.setImageDrawable(d);
                }
                    //holder.textViewUsername.setText("my counter value is :" + this.counter); // change back to object contact.getcontact name
                holder.textViewUsername.setText(Contacts.contactList.get(position).getContactName());
                holder.textViewMessage.setText(Contacts.contactList.get(position).getEmail());
                //}
                this.counter++; // remove
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
    public int getItemCount() {
        return objectList.size();
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
