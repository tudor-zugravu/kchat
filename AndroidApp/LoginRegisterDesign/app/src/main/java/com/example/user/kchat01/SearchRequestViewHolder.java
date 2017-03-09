package com.example.user.kchat01;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by user on 08/03/2017.
 */

/* This class sets recyclerview and listener */

public class SearchRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    //Related to ItemView
    ImageView imageProfile;
    TextView textViewUsername, textViewDescription;//listener on view
    private SearchRequestAdapter viewholderlistener;

    //constructor
    public SearchRequestViewHolder(View itemView){
        super(itemView);
        imageProfile = (ImageView)itemView.findViewById(R.id.imageProfile);
        textViewUsername = (TextView)itemView.findViewById(R.id.textViewUsername);
        textViewDescription = (TextView)itemView.findViewById(R.id.textViewDescription);
        // set listener at item
        itemView.setOnClickListener(this);
    }

    public void setListener(SearchRequestAdapter listener){
        viewholderlistener = listener;
    }

    @Override
    public void onClick(View v) {
        if (viewholderlistener!=null){
            viewholderlistener.onClick(this);
        }
    }

    public interface SearchRequestViewHolderListener {
        void onClick(SearchRequestViewHolder holder);
    }
}
