package com.example.user.kchat01;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by user on 22/02/2017.
 */

/* This class sets recyclerview and listener */

public class ContactsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    //Related to ItemView
    ImageView imageProfile;
    TextView textViewUsername, textViewMessage;
    private List<ItemContacts> objectList;
    //listener on view
    private ContactsViewHolderListener viewholderlistener;

    //constructor
    public ContactsViewHolder(View itemView){
        super(itemView);
        imageProfile = (ImageView)itemView.findViewById(R.id.imageProfile);
        textViewUsername = (TextView)itemView.findViewById(R.id.textViewUsername);
        textViewMessage = (TextView)itemView.findViewById(R.id.textViewMessage);
        // set listener at item
        itemView.setOnClickListener(this);
    }

    public void setListener(ContactsViewHolderListener listener){
        viewholderlistener = listener;
    }

    @Override
    public void onClick(View v) {
        if (viewholderlistener!=null){
            viewholderlistener.onClick(this);
        }
    }

    public interface ContactsViewHolderListener {
        void onClick(ContactsViewHolder holder);
    }
}
