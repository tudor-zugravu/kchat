package com.example.user.kchat01;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import API.IGroups;

import static com.example.user.kchat01.R.id.textViewGroupName;

/**
 * Created by user on 22/02/2017.
 */

/* This class sets recyclerview and listener */

public class GroupsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    //Related to ItemView
    ImageView imageProfile;
    TextView textViewGroupname;
    private List<IGroups> objectList;
    //listener on view
    private GroupsViewHolderListener viewholderlistener;

    //constructor
    public GroupsViewHolder(View itemView){
        super(itemView);
        imageProfile = (ImageView)itemView.findViewById(R.id.imageProfile);
        textViewGroupname = (TextView)itemView.findViewById(textViewGroupName);
        // set listener at item
        itemView.setOnClickListener(this);
    }

    public void setListener(GroupsViewHolderListener listener){
        viewholderlistener = listener;
    }

    @Override
    public void onClick(View v) {
        if (viewholderlistener!=null){
            viewholderlistener.onClick(this);
        }
    }

    public interface GroupsViewHolderListener {
        void onClick(GroupsViewHolder holder);
    }
}
