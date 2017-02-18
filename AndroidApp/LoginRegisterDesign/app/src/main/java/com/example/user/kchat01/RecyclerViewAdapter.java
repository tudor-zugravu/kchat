package com.example.user.kchat01;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by user on 17/02/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {

    private List<ItemObject> itemList;
    private Context context;

    //constructor
    public RecyclerViewAdapter(Context context, List<ItemObject> itemList){
        this.itemList = itemList;
        this.context = context;
    }

    // return the ViewHolder generated View got from resource
    // identify View by each position because of getting viewType as argument
    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType){
        // create a new view
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, null);
        RecyclerViewHolders recyclervhs = new RecyclerViewHolders(layoutView);
        return recyclervhs;
    }

    // set the value to ViewHolder
    // convention: getView() in ArrayAdapter
    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position){
        holder.userName.setText(itemList.get(position).getUserName());
        holder.message.setText(itemList.get(position).getMessage());
        holder.profilePhoto.setImageResource(itemList.get(position).getPhotoId());
    }

    //return ViewType by each position
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    //return data size
    @Override
    public int getItemCount(){
        return this.itemList.size();
    }

    // ViewHolder for one line in the list.
    // event settings are performed here

    /*
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // a line is clicked
                    Log.i("info", "a line was clicked");
                }
            });
            mTextView = (TextView)v.findViewById(R.id.text);
        }
    }
    */
}
