package com.example.user.kchat01;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

/**
 * Created by user on 22/03/2017.
 */

public class ImageListener implements View.OnClickListener{

    private Context context;
    private String type;

    public ImageListener(Context context, String type) {
        this.context = context;
        this.type = type;
    }

    @Override
    public void onClick(View view) {
        String[] str_items = {
                    "CAMERA",
                    "FOLDER",
                    "CANCEL"};
            new AlertDialog.Builder(this.context)
                    .setTitle("What type of image to upload")
                    .setItems(str_items, new DialogInterface.OnClickListener(){

                        public void onClick(DialogInterface dialog, int which) {
                                    switch(which)
                                    {
                                        case 0:
                                            //Camera
                                            Toast.makeText(context,"CAMERA from "+type, Toast.LENGTH_SHORT).show();
                                            break;
                                        case 1:
                                            //Folder
                                            Toast.makeText(context,"Folder from "+type, Toast.LENGTH_SHORT).show();
                                            break;
                                        default:
                                            //Cancel
                                            break;
                                    }
                                }
                            }
                    ).show();
        }
}
