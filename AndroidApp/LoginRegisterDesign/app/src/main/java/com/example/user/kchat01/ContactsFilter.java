package com.example.user.kchat01;

import android.widget.Filter;

import java.util.ArrayList;

import API.IGroups;
import IMPL.Groups;

/**
 * Created by user on 23/02/2017.
 */

public class ContactsFilter extends Filter {

    ContactsAdapter adapter;
    ArrayList filterList;

    //constructor
    public ContactsFilter(ArrayList<?> filterList, ContactsAdapter adapter){
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
            ArrayList filteredContacts = new ArrayList<>();

            if(!filterList.isEmpty()&& filterList instanceof IGroups) {
                ArrayList<IGroups> group = this.filterList;
                for (int i=0; i < group.size();i++){
                //matching between constraint and username or message
                    if (group.get(i).getName().toUpperCase().contains(constraint) || group.get(i).getDescription().toUpperCase().contains(constraint)) {
                        filteredContacts.add(filterList.get(i));
                    }
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
        adapter.objectList = (ArrayList) results.values;
        adapter.notifyDataSetChanged();
    }
}
