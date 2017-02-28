package com.example.user.kchat01;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import API.IMessage;

/**
 * Created by user on 22/02/2017.
 */

/* This class is the adapter to deal with chats */
// ItemChats type is defined as another class in the same package

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> implements Filterable{

    private LayoutInflater inflater;
    public ArrayList<IMessage> objectList;
    public ArrayList<IMessage> filterList;
    private ChatsFilter filter;
    public static final int SENDER = 1;
    public static final int RECEIVER = 0;

    //constructor
    public ChatsAdapter(Context context, ArrayList<IMessage> objectList) {
        inflater = LayoutInflater.from(context);
        this.objectList = objectList;
        this.filterList = objectList;
    }

    //create view holder
    @Override
    public ChatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //convert item layout to view object
        if (viewType == 1) {
            View view = inflater.inflate(R.layout.item_chats_right, parent, false);
            //create view holder
            ChatsViewHolder holder = new ChatsViewHolder(view);
            return holder;
        } else {
            View view = inflater.inflate(R.layout.item_chats_left, parent, false);
            ChatsViewHolder holder = new ChatsViewHolder(view);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(ChatsViewHolder holder, int position) {
        IMessage current = objectList.get(position);
        holder.setData(current, position);
    }
/*
    //bind data to view
    @Override
    public void onBindViewHolder(ChatsViewHolder holder, int position) {
        ItemChats current = objectList.get(position);
        holder.txtMessage.setText(current.getMessage());
        holder.txtInfo.setText(current.getDateTime());
    }
*/
    @Override
    public int getItemCount() {
        return objectList.size();
    }

    @Override
    public int getItemViewType(int position){
        IMessage chats = objectList.get(position);

        if (chats.isMe()) {
            return SENDER;
        } else {
            return RECEIVER;
        }

    }

    //filter
    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new ChatsFilter(filterList, this);
        }
        return filter;
    }

    //ChatsViewHolder Class definition
    public static class ChatsViewHolder extends RecyclerView.ViewHolder{

        //Related to ItemView
        public TextView txtMessage, txtTime;

        //constructor
        public ChatsViewHolder(View itemView){
            super(itemView);
            txtMessage = (TextView)itemView.findViewById(R.id.textViewMessage);
            txtTime = (TextView)itemView.findViewById(R.id.textViewTime);
        }

        //set each data on layout file
        public void setData(IMessage current, int position) {
            this.txtMessage.setText(current.getMessage());
            this.txtTime.setText(current.getTimestamp().toString());
        }
    }
}

