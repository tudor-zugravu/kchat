package com.example.user.kchat01;

import android.util.Log;
import android.widget.Filter;

import java.util.ArrayList;

/**
 * Created by user on 23/02/2017.
 */

public class ContactsFilter extends Filter {

    ContactsAdapter adapter;
    ArrayList<ItemContacts> filterList;

    //constructor
    public ContactsFilter(ArrayList<ItemContacts> filterList, ContactsAdapter adapter){
        this.adapter = adapter;
        this.filterList= filterList;
    }

    //perform filter by checking input data (constraint)
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        //whether search words are input or not
        Log.i("filterList.size ", String.valueOf(filterList.size()));
        if (constraint != null && constraint.length() > 0){
            constraint = constraint.toString().toUpperCase();
            ArrayList<ItemContacts> filteredContacts = new ArrayList<>();

            for (int i=0; i < filterList.size();i++){
                //matching between constraint and username or message
                if (filterList.get(i).getUsername().toUpperCase().contains(constraint)){
                    filteredContacts.add(filterList.get(i));
                }
            }
            results.count = filteredContacts.size();
            results.values = filteredContacts;
        }else
        {
            results.count=filterList.size();
            results.values=filterList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.objectList = (ArrayList<ItemContacts>) results.values;
        adapter.notifyDataSetChanged();
    }
}
