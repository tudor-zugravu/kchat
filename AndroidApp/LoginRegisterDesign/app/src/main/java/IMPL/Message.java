package IMPL;

import android.graphics.Bitmap;

import com.example.user.kchat01.R;

import java.util.ArrayList;
import java.util.Date;

import API.IMessage;

/**
 * Created by Tudor Vasile on 2/15/2017.
 */

public class Message implements IMessage {

    private int messageId;
    private int senderId;
    private int groupId;
    private String message;
    private String timestamp;
    private String username;
    private int imageId;
    private boolean isMe;
    private int receiverId;
    private String strTimestamp;

    public Message(int messageId,int senderId,String message,String timestamp){
        this.senderId = senderId;
          this.messageId = messageId;
        this.message = message;
        this.timestamp = timestamp;
    }


    @Override
    public int getMessageId() {
        return this.messageId;
    }

    @Override
    public void setMessageId(int id) {
        this.messageId = id;
    }

    @Override
    public int getSenderId() {
        return this.senderId;
    }

    @Override
    public void setSenderId(int messageId) {
        this.senderId = messageId;
    }

    @Override
    public int getGroupId() {
        return this.groupId;
    }

    @Override
    public void setGroupId(int GroupId) {
        this.groupId = GroupId;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getTimestamp() {
        return this.timestamp;
    }

    @Override
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public int getReceiverId() {
        return this.receiverId;
    }

    @Override
    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }
    @Override
    public int getImageId() {
        return this.imageId;
    }
    @Override
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    @Override
    public String getStrTimestamp() {
        return this.strTimestamp;
    }
    @Override
    public void setStrTimestamp(String strTimestamp) {
        this.strTimestamp = strTimestamp;
    }
    @Override
    public boolean isMe() {
        return isMe;
    }
    @Override
    public void setMe(boolean me) {
        isMe = me;
    }

}
