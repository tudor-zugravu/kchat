package com.example.user.kchat01;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import API.IContacts;

/**
 * Created by user on 22/02/2017.
 */

/* This class sets recyclerview and listener */

public class AddGroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    //Related to item_add_group layout
    public TextView textViewUsername;
    public CheckBox checkBox;
    public ImageView imageProfile;
    public List<IContacts> objectList;
    //listener on view
    public AddGroupListener addGroupListener;

    //constructor
    public AddGroupViewHolder(View itemView){
        super(itemView);
        imageProfile = (ImageView)itemView.findViewById(R.id.imageProfile);
        textViewUsername = (TextView)itemView.findViewById(R.id.textViewUsername);
        checkBox = (CheckBox)itemView.findViewById(R.id.checkBox);
        // set listener at item
        checkBox.setOnClickListener(this);
    }


    public void setAddGroupListener(AddGroupListener viewholderlistener){
        this.addGroupListener = viewholderlistener;
    }

    @Override
    public void onClick(View v) {
        this.addGroupListener.onItemClick(v, getLayoutPosition());
    }

    /*

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.checkBox)

            //set listener to each item
        //when clicking item, set checkbox on or off

        if (viewholderlistener!=null){
            this.viewholderlistener.onClick(this);
        }
    }
*/
    public interface AddGroupListener {
        void onItemClick(View view, int position);
    }
}
