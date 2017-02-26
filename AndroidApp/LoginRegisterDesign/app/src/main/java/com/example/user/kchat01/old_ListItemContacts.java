package com.example.user.kchat01;

/**
 * Created by user on 19/02/2017.
 */

/*
This class is getter and setter on each list in user contacts list
 */

public class old_ListItemContacts {

    private int imageId;
    private String userName, message;

    public old_ListItemContacts(int imageId, String userName, String message){
        this.imageId = imageId;
        this.userName = userName;
        this.message = message;
    }

    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    public String getUsername() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

}

