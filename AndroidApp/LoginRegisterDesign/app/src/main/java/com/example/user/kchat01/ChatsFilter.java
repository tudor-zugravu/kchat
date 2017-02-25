package com.example.user.kchat01;

import android.widget.Filter;

import java.util.ArrayList;

import API.IMessage;

/**
 * Created by user on 23/02/2017.
 */

public class ChatsFilter extends Filter {

    ChatsAdapter adapter;
    ArrayList<IMessage> filterList;

    //constructor
    public ChatsFilter(ArrayList<IMessage> filterList, ChatsAdapter adapter){
        this.adapter = adapter;
        this.filterList= filterList;
    }

    //perform filter by checking input data (constraint)
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        //whether search words are input or not
        if (constraint != null && constraint.length() > 0){
            constraint = constraint.toString().toUpperCase();
            ArrayList<IMessage> filteredChats = new ArrayList<>();

            for (int i=0; i < filterList.size();i++){
                //matching between constraint and message
                if (filterList.get(i).getMessage().toUpperCase().contains(constraint)){
                    filteredChats.add(filterList.get(i));
                }
            }
            results.count = filteredChats.size();
            results.values = filteredChats;
        }else
        {
            results.count=filterList.size();
            results.values=filterList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.objectList = (ArrayList<IMessage>) results.values;
        adapter.notifyDataSetChanged();
    }
}
