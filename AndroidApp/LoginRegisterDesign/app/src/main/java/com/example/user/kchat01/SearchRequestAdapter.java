package com.example.user.kchat01;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

import API.IContacts;
import API.IGroups;

import static IMPL.Contacts.contactList;

/**
 * Created by user on 08/03/2017.
 */

/* This class is the adapter to deal with profile image and text in user contacts */
// ItemContacts type is defined as another class in the same package

public class SearchRequestAdapter extends RecyclerView.Adapter<SearchRequestViewHolder> implements SearchRequestViewHolder.SearchRequestViewHolderListener, Filterable {

    private LayoutInflater inflater;
    public ArrayList filterList;
    private SearchRequestFilter filter;
    private int type;
    Context context;

    //constructor
    public SearchRequestAdapter(Context context, ArrayList<?> objectList, int type) {
        inflater = LayoutInflater.from(context);
        this.filterList = objectList;
        this.type = type;
        this.context = context;
    }

    //create view holder
    @Override
    public SearchRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //convert item layout to view object
        View view = inflater.inflate(R.layout.item_request, parent, false);
        //create view holder
        SearchRequestViewHolder holder = new SearchRequestViewHolder(view);
        return holder;
    }

    //bind data to view
    @Override
    public void onBindViewHolder(SearchRequestViewHolder holder, int position) {
        if (type == 0) { // to change this to search users list
            IGroups current = (IGroups) filterList.get(position);
            holder.imageProfile.setImageResource(current.getImageId());
            holder.textViewUsername.setText(current.getName());
            holder.textViewDescription.setText(current.getDescription());
        }
        if (type == 1) { // to change this to sent requests list
            if (!contactList.isEmpty()) {
                IContacts current2 = (IContacts) filterList.get(position);
                if (contactList.get(position).getBitmap() == null) {
                    Drawable d = ContextCompat.getDrawable(context, R.drawable.profile_logo);
                    holder.imageProfile.setImageDrawable(d);
                } else {
                    holder.imageProfile.setImageBitmap(current2.getBitmap());
                }
                holder.textViewUsername.setText(current2.getContactName());
                holder.textViewDescription.setText(current2.getEmail());
            }
        }
        if (type == 2) { // to change this to received requests list
            if (!contactList.isEmpty()) {
                IContacts current3 = (IContacts) filterList.get(position);
                if (contactList.get(position).getBitmap() == null) {
                    Drawable d = ContextCompat.getDrawable(context, R.drawable.profile_logo);
                    holder.imageProfile.setImageDrawable(d);
                } else {
                    holder.imageProfile.setImageBitmap(current3.getBitmap());
                }
                holder.textViewUsername.setText(current3.getContactName());
                holder.textViewDescription.setText(current3.getEmail());
            }

        }
        //set click listener
        holder.setListener(this);
    }

    //only definition
    @Override
    public void onClick(SearchRequestViewHolder holder) {
    }


    @Override
    public int getItemCount() {
        return filterList.size();
    }


    //filter
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new SearchRequestFilter(filterList, this);
        }
        return filter;
    }
}