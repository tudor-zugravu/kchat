package IMPL;

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
    private Date timestamp;
    private String username;

    public Message(int messageId,int senderId,int groupId,String message,Date timestamp,String username){
    this.messageId = messageId;
        this.senderId = senderId;
        this.groupId = groupId;
        this.message = message;
        this.timestamp = timestamp;
        this.username = username;
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
    public Date getTimestamp() {
        return this.timestamp;
    }

    @Override
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public void setUsername(String username) {
    this.username = username;
    }
}
