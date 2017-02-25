package com.example.user.kchat01;

import android.widget.Filter;

import java.util.ArrayList;

import API.IGroups;

/**
 * Created by user on 23/02/2017.
 */

public class ContactsFilter extends Filter {

    ContactsAdapter adapter;
    ArrayList<IGroups> filterList;

    //constructor
    public ContactsFilter(ArrayList<IGroups> filterList, ContactsAdapter adapter){
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
            ArrayList<IGroups> filteredContacts = new ArrayList<>();

            for (int i=0; i < filterList.size();i++){
                //matching between constraint and username or message
                if (filterList.get(i).getName().toUpperCase().contains(constraint) || filterList.get(i).getDescription().toUpperCase().contains(constraint)){
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
        adapter.objectList = (ArrayList<IGroups>) results.values;
        adapter.notifyDataSetChanged();
    }
}
