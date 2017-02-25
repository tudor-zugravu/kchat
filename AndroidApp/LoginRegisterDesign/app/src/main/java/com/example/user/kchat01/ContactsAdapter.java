package com.example.user.kchat01;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

import API.IGroups;

/**
 * Created by user on 22/02/2017.
 */

/* This class is the adapter to deal with profile image and text in user contacts */
// ItemContacts type is defined as another class in the same package

public class ContactsAdapter extends RecyclerView.Adapter<ContactsViewHolder> implements ContactsViewHolder.ContactsViewHolderListener, Filterable{

    private LayoutInflater inflater;
    public ArrayList<IGroups> objectList;
    public ArrayList<IGroups> filterList;
    private ContactsViewHolder holder;
    private ContactsFilter filter;

    //constructor
    public ContactsAdapter(Context context, ArrayList<IGroups> objectList) {
        inflater = LayoutInflater.from(context);
        this.objectList = objectList;
        this.filterList = objectList;
    }

    //create view holder
    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //convert item layout to view object
        View view = inflater.inflate(R.layout.item_contacts, parent, false);
        //create view holder
        final ContactsViewHolder holder = new ContactsViewHolder(view);
        return holder;
    }

    //bind data to view
    @Override
    public void onBindViewHolder(ContactsViewHolder holder, int position) {
        IGroups current = objectList.get(position);
        holder.imageProfile.setImageResource(current.getImageId());
        holder.textViewUsername.setText(current.getName());
        holder.textViewMessage.setText(current.getDescription());

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
