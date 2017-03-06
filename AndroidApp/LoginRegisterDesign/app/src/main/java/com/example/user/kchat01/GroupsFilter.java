package com.example.user.kchat01;

import android.widget.Filter;

import java.util.ArrayList;

import API.IGroups;

/**
 * Created by user on 23/02/2017.
 */

public class GroupsFilter extends Filter {

    GroupsAdapter adapter;
    ArrayList<IGroups> filterList;

    //constructor
    public GroupsFilter(ArrayList<IGroups> filterList, GroupsAdapter adapter){
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
            ArrayList<IGroups> filteredGroups = new ArrayList<>();

            for (int i=0; i < filterList.size();i++){
                //matching between constraint and username or message
                if (filterList.get(i).getName().toUpperCase().contains(constraint)){
                    filteredGroups.add(filterList.get(i));
                }
            }
            results.count = filteredGroups.size();
            results.values = filteredGroups;
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
