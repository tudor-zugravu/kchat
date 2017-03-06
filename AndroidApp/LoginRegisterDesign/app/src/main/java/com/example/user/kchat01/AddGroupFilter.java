package com.example.user.kchat01;

import android.widget.Filter;

import java.util.ArrayList;

import API.IContacts;

/**
 * Created by user on 23/02/2017.
 */

public class AddGroupFilter extends Filter {

    AddGroupAdapter adapter;
    ArrayList<IContacts> filterList;

    //constructor
    public AddGroupFilter(ArrayList<IContacts> filterList, AddGroupAdapter adapter){
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
            ArrayList<IContacts> filteredContacts = new ArrayList<>();

            for (int i=0; i < filterList.size();i++){
                //matching between constraint and username
                if (filterList.get(i).getUsername().toUpperCase().contains(constraint)){
                    filteredContacts.add(filterList.get(i));
                }
            }
            results.count = filteredContacts.size();
            results.values = filteredContacts;
        }else {
            results.count=filterList.size();
            results.values=filterList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.objectList = (ArrayList<IContacts>) results.values;
        adapter.notifyDataSetChanged();
    }
}
