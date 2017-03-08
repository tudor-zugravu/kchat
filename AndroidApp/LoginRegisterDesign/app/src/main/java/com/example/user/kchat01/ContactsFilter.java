package com.example.user.kchat01;

import android.util.Log;
import android.widget.Filter;

import java.util.ArrayList;

import API.IContacts;
import API.IGroups;

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
//            Log.d("FilterCheck_contacts", filterList.get(0).toString());

            if(!filterList.isEmpty() && filterList.get(0) instanceof IGroups) {
                ArrayList<IGroups> group = this.filterList;
                for (int i=0; i < filterList.size();i++){

                    //if (group.get(i).getName().toUpperCase().contains(constraint) || group.get(i).getDescription().toUpperCase().contains(constraint)) {
                    if (group.get(i).getName().toUpperCase().startsWith((String) constraint) ) {
                        filteredContacts.add(filterList.get(i));
                        //adapter.notifyDataSetChanged();
                       }
                }

            }else if(!filterList.isEmpty() && filterList.get(0) instanceof IContacts) {
                ArrayList<IContacts> contact = this.filterList;
                for (int i=0; i < filterList.size();i++){
                   // filteredContacts.clear();
                    Log.d("FilterCheck_contacts","prints here "+i);

                    //matching between constraint and username or message
//                    if (contact.get(i).getContactName().toUpperCase().contains(constraint) || contact.get(i).getEmail().toUpperCase().contains(constraint)) {
                    if (contact.get(i).getContactName().toUpperCase().startsWith((String)constraint)){
                        Log.d("FilterCheck_i", String.valueOf(i));
                        Log.d("FilterCheck_before_size", String.valueOf(filteredContacts.size()));
                        filteredContacts.add(filterList.get(i));

                        Log.d("SIZECHECK", Integer.toString(filteredContacts.size()));
                        Log.d("SIZECHECK", Integer.toString(filterList.size()));
                        Log.d("SIZECHECK", Integer.toString(contact.size()));

                        Log.d("FilterCheck_after_size", String.valueOf(filteredContacts.size()));

                 //       adapter.notifyDataSetChanged();
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
        Log.d("Check_berore_Results", String.valueOf(adapter.objectList.size())+adapter.objectList.get(0));
        adapter.notifyDataSetChanged();
        Log.d("Check_after_Results", String.valueOf(adapter.objectList.size())+adapter.objectList.get(0));
    }
/*
    @Override
    public void run() {
        ContactsFilter.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
*/
}
