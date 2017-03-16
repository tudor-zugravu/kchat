package IMPL;

import com.example.user.kchat01.R;

import java.text.SimpleDateFormat;
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
    private Date timestamp;
    private String username;
    private int imageId;
    private boolean isMe;
    private int receiverId;
    private String strTimestamp;

    public Message(int messageId,int groupId,String message,Date timestamp,String username){
    this.messageId = messageId;
        this.groupId = groupId;
        this.message = message;
        this.timestamp = timestamp;
        this.username = username;
    }

    public Message( int senderId,int groupId,int receiverId,String message,Date timestamp){
        this.senderId = senderId;
        this.groupId = groupId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Message(int messageId, String message,Date timestamp,String username,int imageId){
        this.messageId = messageId;
        this.message = message;
        this.timestamp = timestamp;
        this.username = username;
        this.imageId = imageId;
    }

    public Message(int messageId, String message, String strTimestamp, String username, int imageId) {
        this.messageId = messageId;
        this.message = message;
        this.strTimestamp = strTimestamp;
        this.username = username;
        this.imageId = imageId;
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

    public static ArrayList<IMessage> getObjectList() {

        ArrayList<IMessage> dataList = new ArrayList<>();

        IMessage chat = new Message(1,"Hi",new Date(),"user01",R.drawable.human);
        chat.setMe(false);
        dataList.add(chat);

        IMessage chat2 = new Message(2,"hello",new Date(),"user01",R.drawable.ic_supervisor_account_black_24dp);
        chat2.setMe(false);
        dataList.add(chat2);

        IMessage chat3 = new Message(3,"Fine, how about you?",new Date(),"user01",0);
        chat3.setMe(false);
        dataList.add(chat3);

        IMessage chat4 = new Message(4,"Really Good!",new Date(),"user01",R.drawable.ic_folder_shared_black_24dp);
        chat4.setMe(true);
        dataList.add(chat4);

        IMessage chat5 = new Message(5,"It was really fun playing with you last month.",new Date(),"user01",R.drawable.ic_phone_android_black_24dp);
        chat5.setMe(false);
        dataList.add(chat5);

        IMessage chat6 = new Message(6,"Well, the meal in particular was good flavour. If you have time, I'd like to go to dinner with you again.",new Date(),"user01",0);
        chat6.setMe(true);
        dataList.add(chat6);

        return dataList;
    }
}
