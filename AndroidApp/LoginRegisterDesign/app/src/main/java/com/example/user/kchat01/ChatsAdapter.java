package com.example.user.kchat01;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
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
    public ArrayList objectList;
    public ArrayList filterList;
    private ChatsFilter filter;
    public static final int SENDER = 1;
    public static final int RECEIVER = 0;
    public static boolean typechat;

    //constructor
    public ChatsAdapter(Context context, ArrayList<?> objectList) {
        inflater = LayoutInflater.from(context);
        this.objectList = objectList;
        this.filterList = objectList;
        this.typechat=false;
    }

    //constructor
    public ChatsAdapter(Context context, ArrayList<?> objectList,int condition) {
        inflater = LayoutInflater.from(context);
        this.objectList = objectList;
        this.filterList = objectList;
        this.typechat=true;
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
        if(typechat==false) {
            IMessage current = (IMessage) objectList.get(position);
            holder.setData(current, position,0);
        }else if(typechat==true){
            IMessage current = (IMessage)objectList.get(position);
            holder.setData(current, position,1);
        }
    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    //ViewType is used to judge me(right hand side view) or not me(left hand one)
    //actual usage method is onCreateViewHolder (param:viewType)
    @Override
    public int getItemViewType(int position){
        IMessage chats = (IMessage)objectList.get(position);

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
        public ImageView imageProfile;
        //constructor
        public ChatsViewHolder(View itemView){
            super(itemView);
            txtMessage = (TextView)itemView.findViewById(R.id.textViewMessage);
            txtTime = (TextView)itemView.findViewById(R.id.textViewTime);
            imageProfile = (ImageView)itemView.findViewById(R.id.imageProfile);
        }

        //set each data on layout file
        public void setData(IMessage current, int position, int type) {
            this.txtMessage.setText(current.getMessage());
            this.txtTime.setText(current.getTimestamp());
            if(type==0) {
                this.imageProfile.setImageBitmap(ChatsActivity.contactsBitmap);
            }else{
                int messageSenderId = current.getSenderId(); // id from the message
                Log.d("IDCHECKER","comparator=  "+Integer.toString(messageSenderId));
                for(int i=0; i<GroupChatsActivity.contactsInChat.size(); i++){
                    Log.d("IDCHECKER",GroupChatsActivity.contactsInChat.get(i).getUserId());
                    if(messageSenderId==Integer.parseInt(GroupChatsActivity.contactsInChat.get(i).getUserId())){
                        Log.d("IDCHECKER",GroupChatsActivity.contactsInChat.get(i).getUserId());
                        this.imageProfile.setImageBitmap(GroupChatsActivity.contactsInChat.get(i).getBitmap());
                    }
                }

            }
        }
    }
}

