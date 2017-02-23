package com.example.user.kchat01;

import java.util.ArrayList;

/**
 * Created by user on 22/02/2017.
 */

/*
This class is getter and setter on each list in user contacts list
 */

public class ItemContacts {

    private int imageId;
    private String username, message;

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ArrayList<ItemContacts> getObjectList() {

        ArrayList<ItemContacts> dataList = new ArrayList<>();

        for (int i = 0; i <= 20; i++) {
            ItemContacts contact = new ItemContacts();
            contact.setImageId(R.drawable.human);
            contact.setUsername("user" + i);
            contact.setMessage("This is the message from user" + i);
            dataList.add(contact);
        }
        return dataList;
    }
}
