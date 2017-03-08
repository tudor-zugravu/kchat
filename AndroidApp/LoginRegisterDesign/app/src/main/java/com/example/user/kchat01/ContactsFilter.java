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
    ArrayList filteredContacts;

    public ContactsFilter(ArrayList<?> filterList, ContactsAdapter adapter){
        this.adapter = adapter;
        this.filterList= filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //whether search words are input or not
        if (constraint != null && constraint.length() > 0){
            constraint = constraint.toString().toUpperCase();
            filteredContacts = new ArrayList<>();

            if(!filterList.isEmpty() && filterList.get(0) instanceof IGroups) {
                ArrayList<IGroups> group = this.filterList;
                for (int i=0; i < filterList.size();i++){
                    if (group.get(i).getName().toUpperCase().startsWith((String) constraint) ) {
                        filteredContacts.add(filterList.get(i));
                       }
                }

            }else if(!filterList.isEmpty() && filterList.get(0) instanceof IContacts) {
                ArrayList<IContacts> contact = this.filterList;
                for (int i=0; i < filterList.size();i++){
                    if (contact.get(i).getContactName().toUpperCase().startsWith((String)constraint)){
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
        //adapter.objectList = (ArrayList) results.values;
        adapter.notifyDataSetChanged();
        adapter.notifyItemRemoved(0);
        Log.d("CheckFilter_afterclear", String.valueOf(adapter.filterList.size()));
        adapter.filterList = (ArrayList)results.values;
//        Log.d("Check_berore_Results", String.valueOf(adapter.filterList.size())+adapter.filterList.get(0));
        adapter.notifyDataSetChanged();
//        Log.d("Check_after_Results", String.valueOf(adapter.filterList.size())+adapter.filterList.get(0));
       // adapter.filterList.clear();

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
