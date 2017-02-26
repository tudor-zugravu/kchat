package com.example.user.kchat01;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by user on 19/02/2017.
 */

/* This class is the adapter to deal with profile image and text in user contacts list */
// type old_ListItemContacts is defined as another class in the same package

public class old_ArrayAdapterContacts extends ArrayAdapter<old_ListItemContacts> {

    private int resourceId;
    private List<old_ListItemContacts> items;
    private LayoutInflater inflater;

    //constructor
    public old_ArrayAdapterContacts(Context context, int resourceId, List<old_ListItemContacts> items) {

        super(context, resourceId, items);

        this.resourceId = resourceId;
        this.items = items;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            view = this.inflater.inflate(this.resourceId, null);
        }

        old_ListItemContacts item = this.items.get(position);

        // set profile image
        ImageView appInfoImage = (ImageView)view.findViewById(R.id.imageProfile);
        appInfoImage.setImageResource(item.getImageId());

        // set text (username)
        TextView appInfoText = (TextView)view.findViewById(R.id.textViewUsername);
        appInfoText.setText(item.getUsername());

        // set text (latest message)
        appInfoText = (TextView)view.findViewById(R.id.textViewMessage);
        appInfoText.setText(item.getMessage());

        return view;
    }
}