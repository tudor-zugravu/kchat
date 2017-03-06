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

/* This class is the adapter to deal with each group data */

public class GroupsAdapter extends RecyclerView.Adapter<GroupsViewHolder> implements GroupsViewHolder.GroupsViewHolderListener, Filterable{

    private LayoutInflater inflater;
    public ArrayList<IGroups> objectList;
    public ArrayList<IGroups> filterList;
    private GroupsViewHolder holder;
    private GroupsFilter filter;

    //constructor
    public GroupsAdapter(Context context, ArrayList<IGroups> objectList) {
        inflater = LayoutInflater.from(context);
        this.objectList = objectList;
        this.filterList = objectList;
    }

    //create view holder
    @Override
    public GroupsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //convert item layout to view object
        View view = inflater.inflate(R.layout.item_groups, parent, false);
        //create view holder
        final GroupsViewHolder holder = new GroupsViewHolder(view);
        return holder;
    }

    //bind data to view
    @Override
    public void onBindViewHolder(GroupsViewHolder holder, int position) {
        IGroups current = objectList.get(position);
        holder.imageProfile.setImageResource(current.getImageId());
        holder.textViewGroupname.setText(current.getName());

        //set click listener
        holder.setListener(this);
    }

    //only definition
    @Override
    public void onClick(GroupsViewHolder holder){
    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    //filter
    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new GroupsFilter(filterList, this);
        }
        return filter;
    }
}
