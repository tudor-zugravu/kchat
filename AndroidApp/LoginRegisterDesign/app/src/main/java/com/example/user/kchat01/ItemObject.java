package com.example.user.kchat01;

/**
 * Created by user on 17/02/2017.
 */

/*
This class is setter and getter about item objects(username, latest message and profile image)
 */


public class ItemObject {

    private String userName;
    private String message;
    private int photoId;

    public ItemObject(String userName, String message, int photoId){
        this.userName = userName;
        this.message = message;
        this.photoId = photoId;
    }

    public String getUserName(){
        return userName;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public int getPhotoId(){
        return photoId;
    }

    public void setPhotoId(int photoId){
        this.photoId = photoId;
    }

}
