package com.example.user.kchat01;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by user on 22/02/2017.
 */

/*
This class is getter and setter on each list in chats
 */

public class ItemChats {

    private int id;
    private boolean isMe;
    private int imageId;
    private String username, message;
    private String dateTime;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setIsMe(boolean isMe) {
        this.isMe = isMe;
    }

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

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }


    public static ArrayList<ItemChats> getObjectList() {

        ArrayList<ItemChats> dataList = new ArrayList<>();

        ItemChats chat = new ItemChats();
        chat.setId(1);
        chat.setIsMe(false);
        chat.setMessage("Hi");
        chat.setDateTime(DateFormat.getDateTimeInstance().format(new Date()));
        chat.setImageId(R.drawable.human);
        chat.setUsername("user01");
        dataList.add(chat);

        ItemChats chat2 = new ItemChats();
        chat2.setId(2);
        chat2.setIsMe(true);
        chat2.setMessage("Hello! How are you??");
        chat2.setDateTime(DateFormat.getDateTimeInstance().format(new Date()));
        chat2.setImageId(R.drawable.ic_supervisor_account_black_24dp);
        chat2.setUsername("user01");
        dataList.add(chat2);

        ItemChats chat3 = new ItemChats();
        chat3.setId(3);
        chat3.setIsMe(false);
        chat3.setMessage("Fine! How about you?");
        chat3.setDateTime(DateFormat.getDateTimeInstance().format(new Date()));
        chat3.setImageId(0);
        chat3.setUsername("user01");
        dataList.add(chat3);

        ItemChats chat4 = new ItemChats();
        chat4.setId(4);
        chat4.setIsMe(true);
        chat4.setMessage("Really good!");
        chat4.setDateTime(DateFormat.getDateTimeInstance().format(new Date()));
        chat4.setImageId(R.drawable.ic_folder_shared_black_24dp);
        chat4.setUsername("user01");
        dataList.add(chat4);

        ItemChats chat5 = new ItemChats();
        chat5.setId(5);
        chat5.setIsMe(false);
        chat5.setMessage("It was really fun playing with you last month.");
        chat5.setDateTime(DateFormat.getDateTimeInstance().format(new Date()));
        chat5.setImageId(R.drawable.ic_phone_android_black_24dp);
        chat5.setUsername("user01");
        dataList.add(chat5);

        ItemChats chat6 = new ItemChats();
        chat6.setId(6);
        chat6.setIsMe(true);
        chat6.setMessage("Well, the meal in particular was good flavour. If you have time, I'd like to go to dinner with you again.");
        chat6.setDateTime(DateFormat.getDateTimeInstance().format(new Date()));
        chat6.setImageId(0);
        chat6.setUsername("user01");
        dataList.add(chat6);

        ItemChats chat7 = new ItemChats();
        chat7.setId(7);
        chat7.setIsMe(false);
        chat7.setMessage("Ok. but I don't have time until my project is finished. So, it will be in April.");
        chat7.setDateTime(DateFormat.getDateTimeInstance().format(new Date()));
        chat7.setImageId(R.drawable.chats_logo_white);
        chat7.setUsername("user01");
        dataList.add(chat7);

        ItemChats chat8 = new ItemChats();
        chat8.setId(8);
        chat8.setIsMe(true);
        chat8.setMessage("No problem. I hope you will achieve your project well.");
        chat8.setDateTime(DateFormat.getDateTimeInstance().format(new Date()));
        chat8.setImageId(R.drawable.menu_logo);
        chat8.setUsername("user01");
        dataList.add(chat8);

        return dataList;
    }
}
