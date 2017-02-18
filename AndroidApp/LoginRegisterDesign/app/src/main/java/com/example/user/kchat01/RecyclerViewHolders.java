package com.example.user.kchat01;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by user on 17/02/2017.
 */

public class RecyclerViewHolders extends RecyclerView.ViewHolder {

    public TextView userName;
    public TextView message;
    public ImageView profilePhoto;

    public RecyclerViewHolders(View itemView) {
        super(itemView);

        userName = (TextView) itemView.findViewById(R.id.textViewUsername);
        message = (TextView) itemView.findViewById(R.id.textViewMessage);
        profilePhoto = (ImageView) itemView.findViewById(R.id.imageProfile);


    /*
    REST get username, latest message and profile image
     */
        userName.setText("hardcopy_userName");
        message.setText("This is a hardcopy message");
        profilePhoto.setImageResource(R.drawable.human);
    }
}
